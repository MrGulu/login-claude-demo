import json
from django.views.decorators.http import require_http_methods
from django.views.decorators.csrf import csrf_exempt
from utils.response import success, error
from utils.decorators import login_required
from api_app.models import Role, RoleMenu
from datetime import datetime

"""
角色模型转字典
"""
def role_to_dict(r):
    return {
        'id': r.id, 'roleName': r.role_name, 'roleKey': r.role_key,
        'isSystem': r.is_system, 'status': r.status, 'sort': r.sort,
        'remark': r.remark, 
        'createTime': r.create_time.strftime('%Y-%m-%d %H:%M:%S') if r.create_time else None
    }

"""
角色列表查询与新增
"""
@csrf_exempt
@require_http_methods(["GET", "POST"])
@login_required
def roles_list(request):
    if request.method == "GET":
        page_num = int(request.GET.get('pageNum', 1))
        page_size = int(request.GET.get('pageSize', 10))
        role_name = request.GET.get('roleName', '')
        role_key = request.GET.get('roleKey', '')
        status = request.GET.get('status')
        
        query = Role.objects.filter(deleted=0)
        if role_name: query = query.filter(role_name__icontains=role_name)
        if role_key: query = query.filter(role_key__icontains=role_key)
        if status is not None: query = query.filter(status=status)
        
        total = query.count()
        roles = query.order_by('sort')[(page_num - 1) * page_size : page_num * page_size]
        items = [role_to_dict(r) for r in roles]
            
        return success({"total": total, "rows": items})
    
    elif request.method == "POST":
        data = json.loads(request.body)
        if Role.objects.filter(role_key=data.get('roleKey'), deleted=0).exists():
            return error(400, "角色标识已经存在")

        new_role = Role(
            role_name=data.get('roleName'),
            role_key=data.get('roleKey'),
            status=data.get('status', 1),
            sort=data.get('sort', 0),
            remark=data.get('remark'),
            create_by=request.username
        )
        new_role.save()
        return success(message="角色创建成功")

"""
获取、修改或删除角色详情
"""
@csrf_exempt
@require_http_methods(["GET", "PUT", "DELETE"])
@login_required
def role_detail(request, role_id):
    role = Role.objects.filter(id=role_id, deleted=0).first()
    if not role:
        return error(404, "Role not found")
        
    if request.method == "GET":
        return success(role_to_dict(role))
        
    elif request.method == "PUT":
        data = json.loads(request.body)
        if 'roleName' in data: role.role_name = data['roleName']
        if 'roleKey' in data: role.role_key = data['roleKey']
        if 'status' in data: role.status = data['status']
        if 'sort' in data: role.sort = data['sort']
        if 'remark' in data: role.remark = data['remark']
        role.update_by = request.username
        role.save()
        return success(message="角色更新成功")
        
    elif request.method == "DELETE":
        if role.is_system == 1:
            return error(400, "系统角色不允许删除")
        role.deleted = 1
        role.update_by = request.username
        role.save()
        return success(message="角色删除成功")

"""
获取或分配角色的菜单权限
"""
@csrf_exempt
@require_http_methods(["GET", "PUT"])
@login_required
def role_menus(request, role_id):
    role = Role.objects.filter(id=role_id, deleted=0).first()
    if not role: return error(404, "Role not found")
    
    if request.method == "GET":
        menus = RoleMenu.objects.filter(role_id=role_id)
        return success([m.menu_id for m in menus])
        
    elif request.method == "PUT":
        if role.role_key == 'root':
             return error(400, "超级管理员拥有所有权限，无需分配")
        menu_ids = json.loads(request.body).get('menuIds', [])
        RoleMenu.objects.filter(role_id=role_id).delete()
        for mid in menu_ids:
            RoleMenu.objects.create(role_id=role_id, menu_id=mid)
        return success(message="菜单分配成功")
