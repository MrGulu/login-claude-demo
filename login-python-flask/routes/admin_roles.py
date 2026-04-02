from flask import Blueprint, request
from utils.response import success, error
from utils.decorators import login_required
from models.models import Role, RoleMenu
from extensions import db
from datetime import datetime

admin_roles_bp = Blueprint('admin_roles', __name__, url_prefix='/api/admin/roles')

"""
角色模型转字典
"""
def role_to_dict(r):
    return {
        'id': r.id,
        'roleName': r.role_name,
        'roleKey': r.role_key,
        'isSystem': r.is_system,
        'status': r.status,
        'sort': r.sort,
        'remark': r.remark,
        'createTime': r.create_time.strftime('%Y-%m-%d %H:%M:%S') if r.create_time else None
    }

"""
查询角色列表
"""
@admin_roles_bp.route('', methods=['GET'])
@login_required
def get_roles():
    page_num = request.args.get('pageNum', 1, type=int)
    page_size = request.args.get('pageSize', 10, type=int)
    role_name = request.args.get('roleName', '')
    role_key = request.args.get('roleKey', '')
    status = request.args.get('status', type=int)

    query = Role.query.filter_by(deleted=0)
    if role_name:
        query = query.filter(Role.role_name.like(f"%{role_name}%"))
    if role_key:
        query = query.filter(Role.role_key.like(f"%{role_key}%"))
    if status is not None:
        query = query.filter_by(status=status)

    pagination = query.order_by(Role.sort.asc()).paginate(page=page_num, per_page=page_size, error_out=False)
    items = [role_to_dict(r) for r in pagination.items]
    return success({
        "total": pagination.total,
        "rows": items
    })

"""
获取单个角色
"""
@admin_roles_bp.route('/<int:id>', methods=['GET'])
@login_required
def get_role(id):
    role = Role.query.get(id)
    if not role or role.deleted == 1:
        return error(404, "Role not found")
    return success(role_to_dict(role))

"""
创建角色
"""
@admin_roles_bp.route('', methods=['POST'])
@login_required
def create_role():
    data = request.get_json() or {}
    role_name = data.get('roleName')
    role_key = data.get('roleKey')
    
    if Role.query.filter_by(role_key=role_key, deleted=0).first():
        return error(400, "角色标识已经存在")
        
    new_role = Role(
        role_name=role_name,
        role_key=role_key,
        status=data.get('status', 1),
        sort=data.get('sort', 0),
        remark=data.get('remark'),
        create_by=request.username
    )
    db.session.add(new_role)
    db.session.commit()
    return success(message="角色创建成功")

"""
修改角色信息
"""
@admin_roles_bp.route('/<int:id>', methods=['PUT'])
@login_required
def update_role(id):
    role = Role.query.get(id)
    if not role or role.deleted == 1:
        return error(404, "Role not found")
        
    data = request.get_json() or {}
    role.role_name = data.get('roleName', role.role_name)
    role.role_key = data.get('roleKey', role.role_key)
    role.status = data.get('status', role.status)
    role.sort = data.get('sort', role.sort)
    role.remark = data.get('remark', role.remark)
    role.update_by = request.username
    role.update_time = datetime.utcnow()
    
    db.session.commit()
    return success(message="角色更新成功")

"""
删除角色
"""
@admin_roles_bp.route('/<int:id>', methods=['DELETE'])
@login_required
def delete_role(id):
    role = Role.query.get(id)
    if not role or role.deleted == 1:
        return error(404, "Role not found")
    if role.is_system == 1:
        return error(400, "系统角色不允许删除")
        
    role.deleted = 1
    role.update_by = request.username
    role.update_time = datetime.utcnow()
    db.session.commit()
    return success(message="角色删除成功")

"""
获取角色菜单权限
"""
@admin_roles_bp.route('/<int:id>/menus', methods=['GET'])
@login_required
def get_role_menus(id):
    rm = RoleMenu.query.filter_by(role_id=id).all()
    return success([m.menu_id for m in rm])

"""
分配角色菜单权限
"""
@admin_roles_bp.route('/<int:id>/menus', methods=['PUT'])
@login_required
def update_role_menus(id):
    role = Role.query.get(id)
    if not role or role.deleted == 1:
         return error(404, "Role not found")
    if role.role_key == 'root':
         return error(400, "超级管理员拥有所有权限，无需分配")
         
    data = request.get_json() or {}
    menu_ids = data.get('menuIds', [])
    RoleMenu.query.filter_by(role_id=id).delete()
    for mid in menu_ids:
        db.session.add(RoleMenu(role_id=id, menu_id=mid))
    db.session.commit()
    return success(message="菜单分配成功")
