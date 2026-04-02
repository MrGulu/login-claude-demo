from flask import Blueprint, request
from utils.response import success, error
from utils.decorators import login_required
from utils.security import hash_password
from models.models import User, UserRole, UserPosition
from extensions import db
from datetime import datetime

admin_users_bp = Blueprint('admin_users', __name__, url_prefix='/api/admin/users')

"""
获取用户列表
"""
@admin_users_bp.route('', methods=['GET'])
@login_required
def get_users():
    page_num = request.args.get('pageNum', 1, type=int)
    page_size = request.args.get('pageSize', 10, type=int)
    username = request.args.get('username', '')
    phone = request.args.get('phone', '')
    status = request.args.get('status', type=int)

    query = User.query.filter_by(deleted=0)
    if username:
        query = query.filter(User.username.like(f"%{username}%"))
    if phone:
        query = query.filter(User.phone.like(f"%{phone}%"))
    if status is not None:
        query = query.filter_by(status=status)

    pagination = query.order_by(User.create_time.desc()).paginate(page=page_num, per_page=page_size, error_out=False)
    items = [u.to_dict() for u in pagination.items]
    return success({
        "total": pagination.total,
        "rows": items
    })

"""
获取单个用户信息
"""
@admin_users_bp.route('/<int:id>', methods=['GET'])
@login_required
def get_user(id):
    user = User.query.get(id)
    if not user or user.deleted == 1:
        return error(404, "User not found")
    return success(user.to_dict())

"""
创建用户
"""
@admin_users_bp.route('', methods=['POST'])
@login_required
def create_user():
    data = request.get_json() or {}
    username = data.get('username')
    password = data.get('password', '123456') # Default password if not provided
    
    if User.query.filter_by(username=username, deleted=0).first():
        return error(400, "用户名已存在")

    new_user = User(
        username=username,
        password=hash_password(password),
        nickname=data.get('nickname'),
        phone=data.get('phone'),
        email=data.get('email'),
        status=data.get('status', 1),
        remark=data.get('remark'),
        create_by=request.username
    )
    db.session.add(new_user)
    db.session.commit()
    return success(message="用户创建成功")

"""
修改用户信息
"""
@admin_users_bp.route('/<int:id>', methods=['PUT'])
@login_required
def update_user(id):
    user = User.query.get(id)
    if not user or user.deleted == 1:
        return error(404, "User not found")
        
    data = request.get_json() or {}
    user.nickname = data.get('nickname', user.nickname)
    user.phone = data.get('phone', user.phone)
    user.email = data.get('email', user.email)
    user.status = data.get('status', user.status)
    user.remark = data.get('remark', user.remark)
    user.update_by = request.username
    user.update_time = datetime.utcnow()
    
    db.session.commit()
    return success(message="用户更新成功")

"""
删除用户
"""
@admin_users_bp.route('/<int:id>', methods=['DELETE'])
@login_required
def delete_user(id):
    user = User.query.get(id)
    if not user or user.deleted == 1:
        return error(404, "User not found")
    if id == 1 or user.username == 'admin':
        return error(400, "不能删除超级管理员")
        
    user.deleted = 1
    user.update_by = request.username
    user.update_time = datetime.utcnow()
    db.session.commit()
    return success(message="用户删除成功")

"""
修改用户状态
"""
@admin_users_bp.route('/<int:id>/status', methods=['PUT'])
@login_required
def update_user_status(id):
    user = User.query.get(id)
    if not user or user.deleted == 1:
        return error(404, "User not found")
    if id == 1 or user.username == 'admin':
        return error(400, "不能禁用超级管理员")
        
    user.status = request.get_json().get('status', 0)
    user.update_by = request.username
    user.update_time = datetime.utcnow()
    db.session.commit()
    return success(message="状态修改成功")

"""
获取用户已分配角色
"""
@admin_users_bp.route('/<int:id>/roles', methods=['GET'])
@login_required
def get_user_roles(id):
    ur = UserRole.query.filter_by(user_id=id).all()
    return success([r.role_id for r in ur])

"""
更新用户已分配角色
"""
@admin_users_bp.route('/<int:id>/roles', methods=['PUT'])
@login_required
def update_user_roles(id):
    if id == 1:
         return error(400, "不能修改超级管理员的角色分配")
    data = request.get_json() or {}
    role_ids = data.get('roleIds', [])
    
    UserRole.query.filter_by(user_id=id).delete()
    for rid in role_ids:
        db.session.add(UserRole(user_id=id, role_id=rid))
    db.session.commit()
    return success(message="角色分配成功")

"""
获取用户已分配岗位
"""
@admin_users_bp.route('/<int:id>/positions', methods=['GET'])
@login_required
def get_user_positions(id):
    up = UserPosition.query.filter_by(user_id=id).all()
    return success([p.position_id for p in up])

"""
更新用户已分配岗位
"""
@admin_users_bp.route('/<int:id>/positions', methods=['PUT'])
@login_required
def update_user_positions(id):
    data = request.get_json() or {}
    position_ids = data.get('positionIds', [])
    UserPosition.query.filter_by(user_id=id).delete()
    for pid in position_ids:
        db.session.add(UserPosition(user_id=id, position_id=pid))
    db.session.commit()
    return success(message="岗位分配成功")
