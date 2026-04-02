from flask import Blueprint, request
from utils.response import success, error
from utils.decorators import login_required
from utils.security import hash_password, verify_password
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
    return success(message="用户信息已更新")
    
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
    return success(message="头像更新成功")

"""
修改当前用户的登录密码
"""
@user_bp.route('/password', methods=['PUT'])
@login_required
def update_password():
    data = request.get_json() or {}
    old_pwd = data.get('oldPassword')
    new_pwd = data.get('newPassword')
    
    if not old_pwd or not new_pwd:
         return error(400, "请提供旧密码和新密码")
         
    user = User.query.get(request.user_id)
    if not user:
         return error(404, "用户未找到")
         
    if not verify_password(old_pwd, user.password):
         return error(400, "旧密码错误")
         
    user.password = hash_password(new_pwd)
    db.session.commit()
    return success(message="密码更改成功")
