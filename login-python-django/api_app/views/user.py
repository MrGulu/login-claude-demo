import json
from django.views.decorators.http import require_http_methods
from django.views.decorators.csrf import csrf_exempt
from utils.response import success, error
from utils.decorators import login_required
from utils.security import hash_password, verify_password
from api_app.models import User

"""
修改用户资料
"""
@csrf_exempt
@require_http_methods(["PUT"])
@login_required
def update_profile(request):
    try:
        data = json.loads(request.body)
    except Exception:
        data = {}
        
    user = User.objects.filter(id=request.user_id, deleted=0).first()
    if not user:
         return error(404, "User not found")
         
    if 'nickname' in data: user.nickname = data['nickname']
    if 'email' in data: user.email = data['email']
    if 'phone' in data: user.phone = data['phone']
    if 'remark' in data: user.remark = data['remark']
    
    user.save()
    return success(message="用户信息已更新")
    
"""
更换头像
"""
@csrf_exempt
@require_http_methods(["POST"])
@login_required
def update_avatar(request):
    try:
        data = json.loads(request.body)
    except Exception:
        data = {}
        
    avatar = data.get('avatar')
    if not avatar:
         return error(400, "Avatar URL required")
         
    user = User.objects.filter(id=request.user_id, deleted=0).first()
    if not user:
         return error(404, "User not found")
         
    user.avatar = avatar
    user.save()
    return success(message="头像更新成功")

"""
修改登录密码
"""
@csrf_exempt
@require_http_methods(["PUT"])
@login_required
def update_password(request):
    try:
        data = json.loads(request.body)
    except Exception:
        data = {}
        
    old_pwd = data.get('oldPassword')
    new_pwd = data.get('newPassword')
    
    if not old_pwd or not new_pwd:
         return error(400, "请提供旧密码和新密码")
         
    user = User.objects.filter(id=request.user_id, deleted=0).first()
    if not user:
         return error(404, "用户未找到")
         
    if not verify_password(old_pwd, user.password):
         return error(400, "旧密码错误")
         
    user.password = hash_password(new_pwd)
    user.save()
    return success(message="密码更改成功")
