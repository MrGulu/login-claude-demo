from flask import Blueprint, request
from utils.response import success, error
from utils.decorators import login_required
from models.models import Position
from extensions import db
from datetime import datetime

admin_positions_bp = Blueprint('admin_positions', __name__, url_prefix='/api/admin/positions')

"""
岗位模型转字典
"""
def pos_to_dict(p):
    return {
        'id': p.id,
        'positionName': p.position_name,
        'positionCode': p.position_code,
        'status': p.status,
        'sort': p.sort,
        'remark': p.remark,
        'createTime': p.create_time.strftime('%Y-%m-%d %H:%M:%S') if p.create_time else None
    }

"""
查询岗位列表
"""
@admin_positions_bp.route('', methods=['GET'])
@login_required
def get_positions():
    page_num = request.args.get('pageNum', 1, type=int)
    page_size = request.args.get('pageSize', 10, type=int)
    position_name = request.args.get('positionName', '')
    position_code = request.args.get('positionCode', '')
    status = request.args.get('status', type=int)

    query = Position.query.filter_by(deleted=0)
    if position_name:
        query = query.filter(Position.position_name.like(f"%{position_name}%"))
    if position_code:
        query = query.filter(Position.position_code.like(f"%{position_code}%"))
    if status is not None:
        query = query.filter_by(status=status)

    pagination = query.order_by(Position.sort.asc()).paginate(page=page_num, per_page=page_size, error_out=False)
    items = [pos_to_dict(p) for p in pagination.items]
    return success({
        "total": pagination.total,
        "rows": items
    })

"""
获取单个岗位
"""
@admin_positions_bp.route('/<int:id>', methods=['GET'])
@login_required
def get_position(id):
    pos = Position.query.get(id)
    if not pos or pos.deleted == 1:
        return error(404, "Position not found")
    return success(pos_to_dict(pos))

"""
创建岗位
"""
@admin_positions_bp.route('', methods=['POST'])
@login_required
def create_position():
    data = request.get_json() or {}
    position_name = data.get('positionName')
    position_code = data.get('positionCode')
    
    if Position.query.filter_by(position_code=position_code, deleted=0).first():
        return error(400, "岗位编码已经存在")
        
    new_pos = Position(
        position_name=position_name,
        position_code=position_code,
        status=data.get('status', 1),
        sort=data.get('sort', 0),
        remark=data.get('remark'),
        create_by=request.username
    )
    db.session.add(new_pos)
    db.session.commit()
    return success(message="岗位创建成功")

"""
修改岗位信息
"""
@admin_positions_bp.route('/<int:id>', methods=['PUT'])
@login_required
def update_position(id):
    pos = Position.query.get(id)
    if not pos or pos.deleted == 1:
        return error(404, "Position not found")
        
    data = request.get_json() or {}
    pos.position_name = data.get('positionName', pos.position_name)
    pos.position_code = data.get('positionCode', pos.position_code)
    pos.status = data.get('status', pos.status)
    pos.sort = data.get('sort', pos.sort)
    pos.remark = data.get('remark', pos.remark)
    pos.update_by = request.username
    pos.update_time = datetime.utcnow()
    
    db.session.commit()
    return success(message="岗位更新成功")

"""
删除岗位
"""
@admin_positions_bp.route('/<int:id>', methods=['DELETE'])
@login_required
def delete_position(id):
    pos = Position.query.get(id)
    if not pos or pos.deleted == 1:
        return error(404, "Position not found")
        
    pos.deleted = 1
    pos.update_by = request.username
    pos.update_time = datetime.utcnow()
    db.session.commit()
    return success(message="岗位删除成功")

"""
修改岗位状态
"""
@admin_positions_bp.route('/<int:id>/status', methods=['PUT'])
@login_required
def update_position_status(id):
    pos = Position.query.get(id)
    if not pos or pos.deleted == 1:
        return error(404, "Position not found")
        
    pos.status = request.get_json().get('status', 0)
    pos.update_by = request.username
    pos.update_time = datetime.utcnow()
    db.session.commit()
    return success(message="状态修改成功")
