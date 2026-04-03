from flask import Blueprint, request
from utils.response import success, error
from utils.decorators import login_required
from utils.security import hash_password, verify_password
from utils.cache import cache
from extensions import db
from models.models import User

user_bp = Blueprint('user', __name__, url_prefix='/api/user')

"""
修改当前用户的个人资料（昵称、邮箱、电话、备注）
"""
@user_bp.route('/profile', methods=['PUT'])
@login_required
def update_profile():
    data = request.get_json() or {}
    user = User.query.get(request.user_id)
    if not user:
         return error(404, "User not found")
         
    user.nickname = data.get('nickname', user.nickname)
    user.email = data.get('email', user.email)
    user.phone = data.get('phone', user.phone)
    user.remark = data.get('remark', user.remark)
    
    db.session.commit()
    
    user_info = {
        "id": str(user.id),
        "username": user.username,
        "nickname": user.nickname,
        "avatar": user.avatar,
        "email": user.email,
        "phone": user.phone
    }
    return success(user_info, "更新成功")
    
"""
更新用户头像
"""
@user_bp.route('/avatar', methods=['POST'])
@login_required
def update_avatar():
    data = request.get_json() or {}
    avatar = data.get('avatar')
    
    if not avatar:
         return error(400, "Avatar URL required")
         
    user = User.query.get(request.user_id)
    if not user:
         return error(404, "User not found")
         
    user.avatar = avatar
    db.session.commit()
    return success({"avatar": avatar}, "头像上传成功")

"""
修改当前用户的登录密码
"""
@user_bp.route('/password', methods=['PUT'])
@login_required
def update_password():
    data = request.get_json() or {}
    old_pwd = data.get('oldPassword')
    new_pwd = data.get('newPassword')
    captcha = data.get('captcha')
    captcha_key = data.get('captchaKey')
    
    if not old_pwd or not new_pwd:
         return error(400, "请提供旧密码和新密码")
         
    # Check captcha
    if not captcha_key or not captcha:
         return error(400, "验证码不能为空")
         
    cached_code = cache.get(f"captcha_codes:{captcha_key}")
    if not cached_code:
        return error(400, "验证码错误或已过期")
    if str(captcha).lower() != str(cached_code).lower():
        return error(400, "验证码错误或已过期")
    # Clear used captcha
    cache.delete(f"captcha_codes:{captcha_key}")
         
    user = User.query.get(request.user_id)
    if not user:
         return error(404, "用户未找到")
         
    if not verify_password(old_pwd, user.password):
         return error(400, "旧密码错误")
         
    user.password = hash_password(new_pwd)
    db.session.commit()
    return success(None, "密码修改成功")
