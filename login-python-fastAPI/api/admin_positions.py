from fastapi import APIRouter, Depends, Query, Body
from sqlalchemy.orm import Session
from datetime import datetime
from models.database import get_db
from models.models import Position, User
from schemas.schemas import ResponseModel, PositionCreate, success, error
from api.deps import get_current_user

router = APIRouter(prefix="/admin/positions", tags=["Admin Positions"])

"""
岗位模型转字典
"""
def pos_to_dict(p):
    return {
        'id': p.id, 'positionName': p.position_name, 'positionCode': p.position_code,
        'status': p.status, 'sort': p.sort, 'remark': p.remark, 
        'createTime': p.create_time.strftime('%Y-%m-%d %H:%M:%S') if p.create_time else None
    }

"""
分页查询岗位列表
"""
@router.get("", response_model=ResponseModel)
def get_positions(
    pageNum: int = Query(1),
    pageSize: int = Query(10),
    positionName: str = Query(""),
    positionCode: str = Query(""),
    status: int = Query(None),
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    query = db.query(Position).filter(Position.deleted == 0)
    if positionName: query = query.filter(Position.position_name.like(f"%{positionName}%"))
    if positionCode: query = query.filter(Position.position_code.like(f"%{positionCode}%"))
    if status is not None: query = query.filter(Position.status == status)

    total = query.count()
    positions = query.order_by(Position.sort.asc()).offset((pageNum - 1) * pageSize).limit(pageSize).all()
    items = [pos_to_dict(p) for p in positions]
        
    return success({"total": total, "rows": items})

"""
获取岗位详情
"""
@router.get("/{id}", response_model=ResponseModel)
def get_position(id: int, current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    p = db.query(Position).filter(Position.id == id, Position.deleted == 0).first()
    if not p: return error(404, "Position not found")
    return success(pos_to_dict(p))

"""
新增岗位
"""
@router.post("", response_model=ResponseModel)
def create_position(data: PositionCreate, current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    if db.query(Position).filter(Position.position_code == data.positionCode, Position.deleted == 0).first():
        return error(400, "岗位编码已经存在")

    new_pos = Position(
        position_name=data.positionName,
        position_code=data.positionCode,
        status=data.status,
        sort=data.sort,
        remark=data.remark,
        create_by=current_user.username
    )
    db.add(new_pos)
    db.commit()
    return success(message="岗位创建成功")

"""
修改岗位信息
"""
@router.put("/{id}", response_model=ResponseModel)
def update_position(id: int, data: dict = Body(...), current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    pos = db.query(Position).filter(Position.id == id, Position.deleted == 0).first()
    if not pos: return error(404, "Position not found")
        
    if 'positionName' in data: pos.position_name = data['positionName']
    if 'positionCode' in data: pos.position_code = data['positionCode']
    if 'status' in data: pos.status = data['status']
    if 'sort' in data: pos.sort = data['sort']
    if 'remark' in data: pos.remark = data['remark']
    pos.update_by = current_user.username
    pos.update_time = datetime.utcnow()
    
    db.commit()
    return success(message="岗位更新成功")

"""
删除岗位（逻辑删除）
"""
@router.delete("/{id}", response_model=ResponseModel)
def delete_position(id: int, current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    pos = db.query(Position).filter(Position.id == id, Position.deleted == 0).first()
    if not pos: return error(404, "Position not found")
        
    pos.deleted = 1
    pos.update_by = current_user.username
    pos.update_time = datetime.utcnow()
    db.commit()
    return success(message="岗位删除成功")

"""
修改岗位状态
"""
@router.put("/{id}/status", response_model=ResponseModel)
def update_position_status(id: int, data: dict = Body(...), current_user: User = Depends(get_current_user), db: Session = Depends(get_db)):
    pos = db.query(Position).filter(Position.id == id, Position.deleted == 0).first()
    if not pos: return error(404, "Position not found")
        
    pos.status = data.get('status', 0)
    pos.update_by = current_user.username
    pos.update_time = datetime.utcnow()
    db.commit()
    return success(message="状态修改成功")
