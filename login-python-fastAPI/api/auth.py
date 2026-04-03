from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from models.database import get_db
from models.models import User, Role, RoleMenu, Menu, UserRole
from schemas.schemas import LoginRequest, ResponseModel, success, error
from utils.security import create_access_token, verify_password
from utils.cache import cache
from api.deps import get_current_user

router = APIRouter(prefix="/auth", tags=["Auth"])

"""
用户登录
:param login_data: 登录请求体
:param db: 数据库会话
:return: 响应
"""
@router.post("/login", response_model=ResponseModel)
def login(login_data: LoginRequest, db: Session = Depends(get_db)):
    # Captcha check (Optional if captchaKey/captcha not provided for compatibility)
    captcha_key = login_data.captchaKey or login_data.uuid
    captcha_code = login_data.captcha or login_data.code
    
    if captcha_key and captcha_code:
        cached_code = cache.get(f"captcha_codes:{captcha_key}")
        if not cached_code:
            return error(400, "验证码已过期")
        if str(captcha_code).lower() != str(cached_code).lower():
            return error(400, "验证码错误")
        cache.delete(f"captcha_codes:{captcha_key}")
    
    user = db.query(User).filter(User.username == login_data.username, User.deleted == 0).first()
    if not user or not verify_password(login_data.password, user.password):
        return error(401, "用户名或密码错误")
    if user.status == 0:
        return error(401, "账户已被停用")
        
    token = create_access_token(user.id, user.username)
    
    # Get user info and permissions for login response
    user_roles = db.query(UserRole).filter(UserRole.user_id == user.id).all()
    role_ids = [ur.role_id for ur in user_roles]
    roles = db.query(Role).filter(Role.id.in_(role_ids), Role.status == 1).all()
    roles_keys = [r.role_key for r in roles]
    
    permissions = []
    if 'root' in roles_keys or user.username == 'admin':
        permissions = ['*:*:*']
    else:
        role_menus = db.query(RoleMenu).filter(RoleMenu.role_id.in_(role_ids)).all()
        menu_ids = [rm.menu_id for rm in role_menus]
        menus = db.query(Menu).filter(Menu.id.in_(menu_ids), Menu.status == 1).all()
        permissions = [m.perms for m in menus if m.perms]

    user_info = {
        "id": str(user.id),
        "username": user.username,
        "nickname": user.nickname,
        "avatar": user.avatar,
        "email": user.email,
        "phone": user.phone
    }

    return success(data={
        "token": token,
        "userInfo": user_info,
        "permissions": permissions
    }, message="登录成功")

"""
用户注销登录
:param current_user: 当前登录用户
:return: 成功响应
"""
@router.post("/logout", response_model=ResponseModel)
def logout(current_user: User = Depends(get_current_user)):
    return success(message="退出登出成功")

"""
获取当前登录用户信息、角色和权限标识
:param current_user: 当前登录用户
:param db: 数据库会话
:return: 响应
"""
@router.get("/userinfo", response_model=ResponseModel)
def get_user_info(current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    user_roles = db.query(UserRole).filter(UserRole.user_id == current_user.id).all()
    role_ids = [ur.role_id for ur in user_roles]
    roles = db.query(Role).filter(Role.id.in_(role_ids), Role.status == 1).all()
    roles_dict = [r.role_key for r in roles]

    permissions = []
    if 'root' in roles_dict or current_user.username == 'admin':
        permissions = ['*:*:*']
    else:
        role_menus = db.query(RoleMenu).filter(RoleMenu.role_id.in_(role_ids)).all()
        menu_ids = [rm.menu_id for rm in role_menus]
        menus = db.query(Menu).filter(Menu.id.in_(menu_ids), Menu.status == 1).all()
        permissions = [m.perms for m in menus if m.perms]

    user_data = {
        'id': str(current_user.id),
        'username': current_user.username,
        'nickname': current_user.nickname,
        'avatar': current_user.avatar,
        'email': current_user.email,
        'phone': current_user.phone,
        'status': current_user.status,
        'remark': current_user.remark,
        'createTime': current_user.create_time.strftime('%Y-%m-%d %H:%M:%S') if current_user.create_time else None
    }

    return success(data=user_data)

"""
刷新用户访问令牌
:param current_user: 当前登录用户
:return: 响应
"""
@router.post("/refresh", response_model=ResponseModel)
def refresh_token(current_user: User = Depends(get_current_user)):
    token = create_access_token(current_user.id, current_user.username)
    return success(data={"token": token}, message="刷新成功")
