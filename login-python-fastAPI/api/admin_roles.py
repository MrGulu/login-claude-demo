from fastapi import APIRouter, Depends, Query, Body
from sqlalchemy.orm import Session
from datetime import datetime
from models.database import get_db
from models.models import Role, RoleMenu, User
from schemas.schemas import ResponseModel, RoleCreate, success, error
from api.deps import get_current_user

router = APIRouter(prefix="/admin/roles", tags=["Admin Roles"])

"""
角色模型转字典
"""
def role_to_dict(r):
    """
    角色模型转字典
    """
    return {
        'id': str(r.id), 'roleName': r.role_name, 'roleKey': r.role_key,
        'isSystem': r.is_system, 'status': r.status, 'sort': r.sort,
        'remark': r.remark, 
        'createTime': r.create_time.strftime('%Y-%m-%d %H:%M:%S') if r.create_time else None
    }

"""
分页查询角色列表
"""
@router.get("", response_model=ResponseModel)
def get_roles(
    pageNum: int = Query(1),
    pageSize: int = Query(10),
    roleName: str = Query(""),
    roleKey: str = Query(""),
    status: int = Query(None),
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    query = db.query(Role).filter(Role.deleted == 0)
    if roleName: query = query.filter(Role.role_name.like(f"%{roleName}%"))
    if roleKey: query = query.filter(Role.role_key.like(f"%{roleKey}%"))
    if status is not None: query = query.filter(Role.status == status)

    total = query.count()
    roles = query.order_by(Role.sort.asc()).offset((pageNum - 1) * pageSize).limit(pageSize).all()
    items = [role_to_dict(r) for r in roles]
        
    return success({"total": total, "records": items})

"""
获取角色详情
"""
@router.get("/{id}", response_model=ResponseModel)
def get_role(id: int, current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    r = db.query(Role).filter(Role.id == id, Role.deleted == 0).first()
    if not r: return error(404, "Role not found")
    return success(role_to_dict(r))

"""
新增角色
"""
@router.post("", response_model=ResponseModel)
def create_role(data: RoleCreate, current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    if db.query(Role).filter(Role.role_key == data.roleKey, Role.deleted == 0).first():
        return error(400, "角色标识已经存在")

    new_role = Role(
        role_name=data.roleName,
        role_key=data.roleKey,
        status=data.status,
        sort=data.sort,
        remark=data.remark,
        create_by=current_user.username
    )
    db.add(new_role)
    db.commit()
    return success(message="角色创建成功")

"""
修改角色信息
"""
@router.put("/{id}", response_model=ResponseModel)
def update_role(id: int, data: dict = Body(...), current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    role = db.query(Role).filter(Role.id == id, Role.deleted == 0).first()
    if not role: return error(404, "Role not found")
        
    if 'roleName' in data: role.role_name = data['roleName']
    if 'roleKey' in data: role.role_key = data['roleKey']
    if 'status' in data: role.status = data['status']
    if 'sort' in data: role.sort = data['sort']
    if 'remark' in data: role.remark = data['remark']
    role.update_by = current_user.username
    role.update_time = datetime.utcnow()
    
    db.commit()
    return success(message="角色更新成功")

"""
删除角色（逻辑删除）
"""
@router.delete("/{id}", response_model=ResponseModel)
def delete_role(id: int, current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    role = db.query(Role).filter(Role.id == id, Role.deleted == 0).first()
    if not role: return error(404, "Role not found")
    if role.is_system == 1: return error(400, "系统角色不允许删除")
        
    role.deleted = 1
    role.update_by = current_user.username
    role.update_time = datetime.utcnow()
    db.commit()
    return success(message="角色删除成功")

"""
获取角色关联的菜单 ID 列表
"""
@router.get("/{id}/menus", response_model=ResponseModel)
def get_role_menus(id: int, current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    menus = db.query(RoleMenu).filter(RoleMenu.role_id == id).all()
    return success([str(m.menu_id) for m in menus])

"""
更新角色的权限菜单分配
"""
@router.put("/{id}/menus", response_model=ResponseModel)
def update_role_menus(id: int, data: dict = Body(...), current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    role = db.query(Role).filter(Role.id == id, Role.deleted == 0).first()
    if not role: return error(404, "Role not found")
    if role.role_key == 'root': return error(400, "超级管理员拥有所有权限，无需分配")

    menu_ids = data.get('menuIds', [])
    db.query(RoleMenu).filter(RoleMenu.role_id == id).delete()
    for mid in menu_ids:
        db.add(RoleMenu(role_id=id, menu_id=mid))
    db.commit()
    return success(message="菜单分配成功")
