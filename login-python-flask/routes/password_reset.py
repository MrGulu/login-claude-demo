from flask import Blueprint, request
from utils.response import success, error
from utils.security import hash_password
from models.models import User
from extensions import db
import uuid
import re

password_reset_bp = Blueprint('password_reset', __name__, url_prefix='/api/password')

# Mock storage for Reset Tokens as per Boot logic (Boot used ConcurrentHashMap)
RESET_TOKEN_STORE = {}

"""
发送验证码
"""
@password_reset_bp.route('/send-code', methods=['POST'])
def send_code():
    data = request.get_json() or {}
    account = data.get('account', '').strip()
    
    if not account:
        return error(400, "手机号或邮箱不能为空")
        
    user = User.query.filter((User.email == account) | (User.phone == account)).first()
    if not user:
        return error(400, "该手机号/邮箱未注册")
    
    # Simulate sending code
    print(f"验证码已发送至 {account}")
    return success(None, "验证码已发送")

"""
验证验证码
"""
@password_reset_bp.route('/verify-code', methods=['POST'])
def verify_code():
    data = request.get_json() or {}
    account = data.get('account', '').strip()
    code = data.get('verificationCode', '').strip()
    
    if not account:
        return error(400, "手机号或邮箱不能为空")
        
    if not re.match(r'^\d{6}$', code):
        return error(400, "验证码格式错误，请输入6位数字")
        
    user = User.query.filter((User.email == account) | (User.phone == account)).first()
    if not user:
        return error(400, "用户不存在")
        
    # Generate token
    reset_token = str(uuid.uuid4()).replace('-', '')
    
    # Store token in memory
    RESET_TOKEN_STORE[reset_token] = user.id
    
    return success({"resetToken": reset_token}, "验证成功")

"""
重置密码
"""
@password_reset_bp.route('/reset', methods=['POST'])
def reset_password():
    data = request.get_json() or {}
    reset_token = data.get('resetToken')
    new_pwd = data.get('newPassword')
    confirm_pwd = data.get('confirmPassword')
    
    if not reset_token:
        return error(400, "重置令牌不能为空")
    if not new_pwd:
        return error(400, "新密码不能为空")
    if len(new_pwd) < 6 or len(new_pwd) > 20:
        return error(400, "密码长度必须在6-20个字符之间")
        
    if new_pwd != confirm_pwd:
        return error(400, "两次输入的密码不一致")
        
    user_id = RESET_TOKEN_STORE.get(reset_token)
    if not user_id:
        return error(400, "重置令牌已过期，请重新操作")
        
    user = User.query.get(user_id)
    if not user:
        return error(400, "用户不存在")
        
    user.password = hash_password(new_pwd)
    db.session.commit()
    
    # Clear token
    del RESET_TOKEN_STORE[reset_token]
    
    return success(None, "密码重置成功")
