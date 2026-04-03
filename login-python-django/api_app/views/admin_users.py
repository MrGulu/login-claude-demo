import json
from django.views.decorators.http import require_http_methods
from django.views.decorators.csrf import csrf_exempt
from utils.response import success, error
from utils.decorators import login_required
from utils.security import hash_password
from api_app.models import User, UserRole, UserPosition
from datetime import datetime

"""
用户列表查询与新增
"""
@csrf_exempt
@require_http_methods(["GET", "POST"])
@login_required
def users_list(request):
    if request.method == "GET":
        page_num = int(request.GET.get('pageNum', 1))
        page_size = int(request.GET.get('pageSize', 10))
        username = request.GET.get('username', '')
        phone = request.GET.get('phone', '')
        status = request.GET.get('status')
        
        query = User.objects.filter(deleted=0)
        if username: query = query.filter(username__icontains=username)
        if phone: query = query.filter(phone__icontains=phone)
        if status is not None: query = query.filter(status=status)
        
        total = query.count()
        users = query.order_by('-create_time')[(page_num - 1) * page_size : page_num * page_size]
        
        items = []
        for u in users:
            items.append({
                'id': str(u.id), 'username': u.username, 'nickname': u.nickname,
                'avatar': u.avatar, 'email': u.email, 'phone': u.phone,
                'status': u.status, 'remark': u.remark,
                'createTime': u.create_time.strftime('%Y-%m-%d %H:%M:%S') if u.create_time else None
            })
            
        return success({"total": total, "list": items})
    
    elif request.method == "POST":
        data = json.loads(request.body)
        if User.objects.filter(username=data.get('username'), deleted=0).exists():
            return error(400, "用户名已存在")

        new_user = User(
            username=data.get('username'),
            password=hash_password(data.get('password', '123456')),
            nickname=data.get('nickname'),
            phone=data.get('phone'),
            email=data.get('email'),
            status=data.get('status', 1),
            remark=data.get('remark'),
            create_by=request.username
        )
        new_user.save()
        return success(message="用户创建成功")

"""
单个用户详情查询、修改与逻辑删除
"""
@csrf_exempt
@require_http_methods(["GET", "PUT", "DELETE"])
@login_required
def user_detail(request, user_id):
    user = User.objects.filter(id=user_id, deleted=0).first()
    if not user:
        return error(404, "User not found")
        
    if request.method == "GET":
        return success({
            'id': str(user.id), 'username': user.username, 'nickname': user.nickname,
            'avatar': user.avatar, 'email': user.email, 'phone': user.phone,
            'status': user.status, 'remark': user.remark,
            'createTime': user.create_time.strftime('%Y-%m-%d %H:%M:%S') if user.create_time else None
        })
        
    elif request.method == "PUT":
        data = json.loads(request.body)
        if 'nickname' in data: user.nickname = data['nickname']
        if 'phone' in data: user.phone = data['phone']
        if 'email' in data: user.email = data['email']
        if 'status' in data: user.status = data['status']
        if 'remark' in data: user.remark = data['remark']
        user.update_by = request.username
        user.save()
        return success(message="用户更新成功")
        
    elif request.method == "DELETE":
        if user_id == 1 or user.username == 'admin':
            return error(400, "不能删除超级管理员")
        user.deleted = 1
        user.update_by = request.username
        user.save()
        return success(message="用户删除成功")

"""
修改用户状态（启用/禁用）
"""
@csrf_exempt
@require_http_methods(["PUT"])
@login_required
def user_status(request, user_id):
    user = User.objects.filter(id=user_id, deleted=0).first()
    if not user: return error(404, "User not found")
    if user_id == 1 or user.username == 'admin': return error(400, "不能禁用超级管理员")
        
    data = json.loads(request.body)
    user.status = data.get('status', 0)
    user.update_by = request.username
    user.save()
    return success(message="状态修改成功")

"""
获取及分配用户角色
"""
@csrf_exempt
@require_http_methods(["GET", "PUT"])
@login_required
def user_roles(request, user_id):
    if request.method == "GET":
        roles = UserRole.objects.filter(user_id=user_id)
        return success([str(r.role_id) for r in roles])
    elif request.method == "PUT":
        if user_id == 1: return error(400, "不能修改超级管理员的角色分配")
        role_ids = json.loads(request.body).get('roleIds', [])
        UserRole.objects.filter(user_id=user_id).delete()
        for rid in role_ids:
            UserRole.objects.create(user_id=user_id, role_id=rid)
        return success(message="角色分配成功")

"""
获取及分配用户岗位
"""
@csrf_exempt
@require_http_methods(["GET", "PUT"])
@login_required
def user_positions(request, user_id):
    if request.method == "GET":
        positions = UserPosition.objects.filter(user_id=user_id)
        return success([str(p.position_id) for p in positions])
    elif request.method == "PUT":
        position_ids = json.loads(request.body).get('positionIds', [])
        UserPosition.objects.filter(user_id=user_id).delete()
        for pid in position_ids:
            UserPosition.objects.create(user_id=user_id, position_id=pid)
        return success(message="岗位分配成功")
