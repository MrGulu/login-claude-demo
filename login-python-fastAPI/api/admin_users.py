from fastapi import APIRouter, Depends, Query, Body
from sqlalchemy.orm import Session
from datetime import datetime
from models.database import get_db
from models.models import User, UserRole, UserPosition
from schemas.schemas import ResponseModel, UserCreate, success, error
from utils.security import hash_password
from api.deps import get_current_user

router = APIRouter(prefix="/admin/users", tags=["Admin Users"])

"""
分页查询用户列表
:param pageNum: 页码
:param pageSize: 每页数量
:param username: 用户名模糊匹配
:param phone: 手机号模糊匹配
:param status: 状态过滤
:return: 用户列表
"""
@router.get("", response_model=ResponseModel)
def get_users(
    pageNum: int = Query(1),
    pageSize: int = Query(10),
    username: str = Query(""),
    phone: str = Query(""),
    status: int = Query(None),
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    query = db.query(User).filter(User.deleted == 0)
    if username:
        query = query.filter(User.username.like(f"%{username}%"))
    if phone:
        query = query.filter(User.phone.like(f"%{phone}%"))
    if status is not None:
        query = query.filter(User.status == status)

    total = query.count()
    users = query.order_by(User.create_time.desc()).offset((pageNum - 1) * pageSize).limit(pageSize).all()
    
    items = []
    for u in users:
        items.append({
            'id': str(u.id), 'username': u.username, 'nickname': u.nickname,
            'avatar': u.avatar, 'email': u.email, 'phone': u.phone,
            'status': u.status, 'remark': u.remark,
            'createTime': u.create_time.strftime('%Y-%m-%d %H:%M:%S') if u.create_time else None
        })
        
    return success({"total": total, "list": items})

"""
根据 ID 获取特定用户信息
"""
@router.get("/{id}", response_model=ResponseModel)
def get_user(id: int, current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    u = db.query(User).filter(User.id == id, User.deleted == 0).first()
    if not u:
        return error(404, "User not found")
    return success({
        'id': str(u.id), 'username': u.username, 'nickname': u.nickname,
        'avatar': u.avatar, 'email': u.email, 'phone': u.phone,
        'status': u.status, 'remark': u.remark,
        'createTime': u.create_time.strftime('%Y-%m-%d %H:%M:%S') if u.create_time else None
    })

"""
新增后台管理用户
"""
@router.post("", response_model=ResponseModel)
def create_user(data: UserCreate, current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    if db.query(User).filter(User.username == data.username, User.deleted == 0).first():
        return error(400, "用户名已存在")

    new_user = User(
        username=data.username,
        password=hash_password(data.password),
        nickname=data.nickname,
        phone=data.phone,
        email=data.email,
        status=data.status,
        remark=data.remark,
        create_by=current_user.username
    )
    db.add(new_user)
    db.commit()
    return success(message="用户创建成功")

"""
修改用户信息
"""
@router.put("/{id}", response_model=ResponseModel)
def update_user(id: int, data: dict = Body(...), current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    user = db.query(User).filter(User.id == id, User.deleted == 0).first()
    if not user:
        return error(404, "User not found")
        
    if 'nickname' in data: user.nickname = data['nickname']
    if 'phone' in data: user.phone = data['phone']
    if 'email' in data: user.email = data['email']
    if 'status' in data: user.status = data['status']
    if 'remark' in data: user.remark = data['remark']
    user.update_by = current_user.username
    user.update_time = datetime.utcnow()
    
    db.commit()
    return success(message="用户更新成功")

"""
逻辑删除用户
"""
@router.delete("/{id}", response_model=ResponseModel)
def delete_user(id: int, current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    user = db.query(User).filter(User.id == id, User.deleted == 0).first()
    if not user:
        return error(404, "User not found")
    if id == 1 or user.username == 'admin':
        return error(400, "不能删除超级管理员")
        
    user.deleted = 1
    user.update_by = current_user.username
    user.update_time = datetime.utcnow()
    db.commit()
    return success(message="用户删除成功")

"""
修改用户启用/禁用状态
"""
@router.put("/{id}/status", response_model=ResponseModel)
def update_user_status(id: int, data: dict = Body(...), current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    user = db.query(User).filter(User.id == id, User.deleted == 0).first()
    if not user:
        return error(404, "User not found")
    if id == 1 or user.username == 'admin':
        return error(400, "不能禁用超级管理员")
        
    user.status = data.get('status', 0)
    user.update_by = current_user.username
    user.update_time = datetime.utcnow()
    db.commit()
    return success(message="状态修改成功")

"""
获取指定用户已分配的角色 ID 列表
"""
@router.get("/{id}/roles", response_model=ResponseModel)
def get_user_roles(id: int, current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    roles = db.query(UserRole).filter(UserRole.user_id == id).all()
    return success([str(r.role_id) for r in roles])

"""
重新分配用户的角色
"""
@router.put("/{id}/roles", response_model=ResponseModel)
def update_user_roles(id: int, data: dict = Body(...), current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    if id == 1: return error(400, "不能修改超级管理员的角色分配")
    role_ids = data.get('roleIds', [])
    db.query(UserRole).filter(UserRole.user_id == id).delete()
    for rid in role_ids:
        db.add(UserRole(user_id=id, role_id=rid))
    db.commit()
    return success(message="角色分配成功")

"""
获取指定用户已分配的岗位 ID 列表
"""
@router.get("/{id}/positions", response_model=ResponseModel)
def get_user_positions(id: int, current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    positions = db.query(UserPosition).filter(UserPosition.user_id == id).all()
    return success([str(p.position_id) for p in positions])

"""
重新分配用户的岗位
"""
@router.put("/{id}/positions", response_model=ResponseModel)
def update_user_positions(id: int, data: dict = Body(...), current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    position_ids = data.get('positionIds', [])
    db.query(UserPosition).filter(UserPosition.user_id == id).delete()
    for pid in position_ids:
        db.add(UserPosition(user_id=id, position_id=pid))
    db.commit()
    return success(message="岗位分配成功")
