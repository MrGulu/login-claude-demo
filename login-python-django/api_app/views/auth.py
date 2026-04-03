import json
from django.views.decorators.http import require_http_methods
from django.views.decorators.csrf import csrf_exempt
from utils.response import success, error
from utils.decorators import login_required
from utils.security import verify_password, create_access_token
from utils.cache import cache
from api_app.models import User, Role, Menu, RoleMenu, UserRole

"""
用户登录
"""
@csrf_exempt
@require_http_methods(["POST"])
def login(request):
    try:
        data = json.loads(request.body)
    except Exception:
        data = {}
        
    username = data.get('username')
    password = data.get('password')
    uuid_str = data.get('captchaKey') or data.get('uuid')
    code = data.get('captcha') or data.get('code')
    # Captcha check (Optional if uuid/code not provided for compatibility)
    if uuid_str and code and str(code).lower() != 'skip':
        cached_code = cache.get(f"captcha_codes:{uuid_str}")
        if not cached_code:
            return error(400, "验证码已过期")
        if str(code).lower() != str(cached_code).lower():
            return error(400, "验证码错误")
        cache.delete(f"captcha_codes:{uuid_str}")
    
    user = User.objects.filter(username=username, deleted=0).first()
    if not user:
        return error(401, "用户名或密码错误")
    if user.status == 0:
        return error(401, "账户已被停用")
        
    if not verify_password(password, user.password):
        return error(401, "用户名或密码错误")
        
    token = create_access_token(user.id, user.username)
    
    # Get user info and permissions for login response
    user_roles = UserRole.objects.filter(user_id=user.id)
    role_ids = [ur.role_id for ur in user_roles]
    roles = Role.objects.filter(id__in=role_ids, status=1)
    roles_keys = [r.role_key for r in roles]
    
    permissions = []
    if 'root' in roles_keys or user.username == 'admin':
        permissions = ['*:*:*']
    else:
        role_menus = RoleMenu.objects.filter(role_id__in=role_ids)
        menu_ids = [rm.menu_id for rm in role_menus]
        menus = Menu.objects.filter(id__in=menu_ids, status=1)
        permissions = [m.perms for m in menus if m.perms]

    user_info = {
        "id": str(user.id),
        "username": user.username,
        "nickname": user.nickname,
        "avatar": user.avatar,
        "email": user.email,
        "phone": user.phone
    }

    return success({
        "token": token,
        "userInfo": user_info,
        "permissions": permissions
    }, "登录成功")

"""
用户退出
"""
@csrf_exempt
@require_http_methods(["POST"])
@login_required
def logout(request):
    return success({}, "退出登出成功")

"""
获取当前用户信息
"""
@csrf_exempt
@require_http_methods(["GET"])
@login_required
def userinfo(request):
    user = User.objects.filter(id=request.user_id, deleted=0).first()
    if not user:
        return error(404, "User not found")
        
    user_roles = UserRole.objects.filter(user_id=user.id)
    role_ids = [ur.role_id for ur in user_roles]
    roles = Role.objects.filter(id__in=role_ids, status=1)
    roles_dict = [r.role_key for r in roles] 
    
    permissions = []
    if 'root' in roles_dict or user.username == 'admin':
        permissions = ['*:*:*']
    else:
        role_menus = RoleMenu.objects.filter(role_id__in=role_ids)
        menu_ids = [rm.menu_id for rm in role_menus]
        menus = Menu.objects.filter(id__in=menu_ids, status=1)
        permissions = [m.perms for m in menus if m.perms]

    user_data = {
        'id': str(user.id),
        'username': user.username,
        'nickname': user.nickname,
        'avatar': user.avatar,
        'email': user.email,
        'phone': user.phone,
        'status': user.status,
        'remark': user.remark,
        'createTime': user.create_time.strftime('%Y-%m-%d %H:%M:%S') if user.create_time else None
    }
    
    return success(user_data)

"""
刷新 Token
"""
@csrf_exempt
@require_http_methods(["POST"])
@login_required
def refresh(request):
    from utils.security import create_access_token
    token = create_access_token(request.user_id, request.username)
    return success({"token": token}, "刷新成功")
