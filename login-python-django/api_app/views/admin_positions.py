import json
from django.views.decorators.http import require_http_methods
from django.views.decorators.csrf import csrf_exempt
from utils.response import success, error
from utils.decorators import login_required
from api_app.models import Position
from datetime import datetime

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
岗位列表查询与新增
"""
@csrf_exempt
@require_http_methods(["GET", "POST"])
@login_required
def positions_list(request):
    if request.method == "GET":
        page_num = int(request.GET.get('pageNum', 1))
        page_size = int(request.GET.get('pageSize', 10))
        position_name = request.GET.get('positionName', '')
        position_code = request.GET.get('positionCode', '')
        status = request.GET.get('status')
        
        query = Position.objects.filter(deleted=0)
        if position_name: query = query.filter(position_name__icontains=position_name)
        if position_code: query = query.filter(position_code__icontains=position_code)
        if status is not None: query = query.filter(status=status)
        
        total = query.count()
        positions = query.order_by('sort')[(page_num - 1) * page_size : page_num * page_size]
        items = [pos_to_dict(p) for p in positions]
            
        return success({"total": total, "rows": items})
    
    elif request.method == "POST":
        data = json.loads(request.body)
        if Position.objects.filter(position_code=data.get('positionCode'), deleted=0).exists():
            return error(400, "岗位编码已经存在")

        new_pos = Position(
            position_name=data.get('positionName'),
            position_code=data.get('positionCode'),
            status=data.get('status', 1),
            sort=data.get('sort', 0),
            remark=data.get('remark'),
            create_by=request.username
        )
        new_pos.save()
        return success(message="岗位创建成功")

"""
获取、修改或删除岗位详情
"""
@csrf_exempt
@require_http_methods(["GET", "PUT", "DELETE"])
@login_required
def position_detail(request, position_id):
    pos = Position.objects.filter(id=position_id, deleted=0).first()
    if not pos:
        return error(404, "Position not found")
        
    if request.method == "GET":
        return success(pos_to_dict(pos))
        
    elif request.method == "PUT":
        data = json.loads(request.body)
        if 'positionName' in data: pos.position_name = data['positionName']
        if 'positionCode' in data: pos.position_code = data['positionCode']
        if 'status' in data: pos.status = data['status']
        if 'sort' in data: pos.sort = data['sort']
        if 'remark' in data: pos.remark = data['remark']
        pos.update_by = request.username
        pos.save()
        return success(message="岗位更新成功")
        
    elif request.method == "DELETE":
        pos.deleted = 1
        pos.update_by = request.username
        pos.save()
        return success(message="岗位删除成功")

"""
修改岗位状态
"""
@csrf_exempt
@require_http_methods(["PUT"])
@login_required
def position_status(request, position_id):
    pos = Position.objects.filter(id=position_id, deleted=0).first()
    if not pos: return error(404, "Position not found")
        
    data = json.loads(request.body)
    pos.status = data.get('status', 0)
    pos.update_by = request.username
    pos.save()
    return success(message="状态修改成功")
