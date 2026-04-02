from flask import Blueprint, request
from utils.response import success, error
from utils.decorators import login_required
from utils.security import verify_password, generate_token
from utils.cache import cache
from models.models import User, Role, Menu, RoleMenu, UserRole
from extensions import db

auth_bp = Blueprint('auth', __name__, url_prefix='/api/auth')

"""
用户登录
接收用户名、密码、验证码及 UUID，验证通过后返回 JWT Token
"""
@auth_bp.route('/login', methods=['POST'])
def login():
    data = request.get_json() or {}
    username = data.get('username')
    password = data.get('password')
    uuid = data.get('uuid')
    code = data.get('code')
    
    # Check captcha (optional for testing)
    if uuid and code:
        cached_code = cache.get(f"captcha_codes:{uuid}")
        if not cached_code:
            return error(400, "验证码已过期")
        if str(code).lower() != str(cached_code).lower():
            return error(400, "验证码错误")
        # Clear used captcha
        cache.delete(f"captcha_codes:{uuid}")
    
    # Authenticate user
    user = User.query.filter_by(username=username, deleted=0).first()
    if not user:
        return error(401, "用户名或密码错误")
    if user.status == 0:
        return error(401, "账户已被停用")
        
    if not verify_password(password, user.password):
        return error(401, "用户名或密码错误")
        
    # Generate token
    token = generate_token(user.id, user.username)
    
    return success({"token": token}, "登录成功")

"""
用户注销
"""
@auth_bp.route('/logout', methods=['POST'])
@login_required
def logout():
    # In a stateless JWT implementation, logout usually happens on client side by deleting the token
    # We could implement a token blocklist here using redis if needed
    return success({}, "退出登出成功")

"""
获取当前登录用户信息、角色和权限标识
"""
@auth_bp.route('/userinfo', methods=['GET'])
@login_required
def userinfo():
    user = User.query.get(request.user_id)
    if not user:
        return error(404, "用户不存在")
        
    # Get Roles
    user_roles = UserRole.query.filter_by(user_id=user.id).all()
    role_ids = [r.role_id for r in user_roles]
    roles = Role.query.filter(Role.id.in_(role_ids), Role.status == 1).all()
    roles_dict = [r.role_key for r in roles] # simplified role output
    
    # Get Permissions
    # admin has all permissions conventionally
    permissions = []
    if 'root' in roles_dict or user.username == 'admin':
        permissions = ['*:*:*']
    else:
        role_menus = RoleMenu.query.filter(RoleMenu.role_id.in_(role_ids)).all()
        menu_ids = [rm.menu_id for rm in role_menus]
        menus = Menu.query.filter(Menu.id.in_(menu_ids), Menu.status == 1).all()
        permissions = [m.perms for m in menus if m.perms]

    user_data = user.to_dict()
    user_data['roles'] = roles_dict
    
    return success({
        "user": user_data,
        "roles": roles_dict,
        "permissions": permissions
    })

"""
刷新用户 Token
"""
@auth_bp.route('/refresh', methods=['POST'])
@login_required
def refresh():
    # Generate new token
    token = generate_token(request.user_id, request.username)
    return success({"token": token}, "刷新成功")
