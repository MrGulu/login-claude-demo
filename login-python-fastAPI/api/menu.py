from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from models.database import get_db
from models.models import Menu, UserRole, Role, RoleMenu, User
from schemas.schemas import ResponseModel, success, error
from api.deps import get_current_user

router = APIRouter(prefix="/menus", tags=["Menus"])

"""
递归构建菜单树结构
:param menus: 菜单列表
:param parent_id: 父级菜单 ID
:return: 嵌套格式的菜单树
"""
def build_menu_tree(menus, parent_id=0):
    tree = []
    for m in menus:
        if m.parent_id == parent_id:
            children = build_menu_tree(menus, m.id)
            node = {
                'id': m.id,
                'parentId': m.parent_id,
                'menuName': m.menu_name,
                'menuType': m.menu_type,
                'path': m.path,
                'component': m.component,
                'perms': m.perms,
                'icon': m.icon,
                'sort': m.sort,
                'visible': m.visible,
                'status': m.status,
                'createTime': m.create_time.strftime('%Y-%m-%d %H:%M:%S') if m.create_time else None
            }
            if children:
                node['children'] = children
            elif m.menu_type in ['M', 'C']:
                node['children'] = []
            tree.append(node)
    return sorted(tree, key=lambda x: (x.get('sort', 0)))

"""
获取系统中所有未删除菜单的树状结构
:param current_user: 当前用户（需登录）
:param db: 数据库会话
:return: 全量菜单树响应
"""
@router.get("/tree", response_model=ResponseModel)
def get_tree(
    current_user: User = Depends(get_current_user), 
    db: Session = Depends(get_db)
):
    menus = db.query(Menu).filter(Menu.deleted == 0).order_by(Menu.sort.asc()).all()
    tree = build_menu_tree(menus, 0)
    return success(tree)

"""
根据当前用户权限获取其可查看的菜单树（通常用于前端导航渲染）
:param current_user: 当前登录用户
:param db: 数据库会话
:return: 用户专属菜单树响应
"""
@router.get("/user", response_model=ResponseModel)
def get_user_menus(
    current_user: User = Depends(get_current_user), 
    db: Session = Depends(get_db)
):
    user_roles = db.query(UserRole).filter(UserRole.user_id == current_user.id).all()
    role_ids = [r.role_id for r in user_roles]
    roles = db.query(Role).filter(Role.id.in_(role_ids), Role.status == 1).all()
    roles_dict = [r.role_key for r in roles] 
    
    if 'root' in roles_dict or current_user.username == 'admin':
        menus = db.query(Menu).filter(Menu.status == 1, Menu.deleted == 0, Menu.menu_type.in_(['M', 'C'])).all()
    else:
        role_menus = db.query(RoleMenu).filter(RoleMenu.role_id.in_(role_ids)).all()
        menu_ids = [rm.menu_id for rm in role_menus]
        menus = db.query(Menu).filter(Menu.id.in_(menu_ids), Menu.status == 1, Menu.deleted == 0, Menu.menu_type.in_(['M', 'C'])).all()
        
    tree = build_menu_tree(menus, 0)
    return success(tree)
