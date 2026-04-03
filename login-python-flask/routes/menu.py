from flask import Blueprint, request
from utils.response import success, error
from utils.decorators import login_required
from models.models import Menu, UserRole, Role, RoleMenu, User
from extensions import db

menu_bp = Blueprint('menu', __name__, url_prefix='/api/menus')

"""
递归构建菜单树
"""
def build_menu_tree(menus, parent_id=0):
    tree = []
    for m in menus:
        if m.parent_id == parent_id:
            children = build_menu_tree(menus, m.id)
            node = {
                'id': str(m.id),
                'parentId': str(m.parent_id),
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
获取全量菜单树数据
"""
@menu_bp.route('/tree', methods=['GET'])
@login_required
def get_tree():
    menus = Menu.query.filter_by(deleted=0).order_by(Menu.sort.asc()).all()
    tree = build_menu_tree(menus, 0)
    return success(tree)

"""
根据当前登录用户的权限获取其可用的菜单树
"""
@menu_bp.route('/user', methods=['GET'])
@login_required
def get_user_menus():
    user = User.query.get(request.user_id)
    if not user:
        return error(404, "User not found")
        
    user_roles = UserRole.query.filter_by(user_id=user.id).all()
    role_ids = [r.role_id for r in user_roles]
    roles = Role.query.filter(Role.id.in_(role_ids), Role.status == 1).all()
    roles_dict = [r.role_key for r in roles] 
    
    # Root admin gets all menus
    if 'root' in roles_dict or user.username == 'admin':
        menus = Menu.query.filter(Menu.status == 1, Menu.deleted == 0, Menu.menu_type.in_(['M', 'C'])).all()
    else:
        role_menus = RoleMenu.query.filter(RoleMenu.role_id.in_(role_ids)).all()
        menu_ids = [rm.menu_id for rm in role_menus]
        menus = Menu.query.filter(Menu.id.in_(menu_ids), Menu.status == 1, Menu.deleted == 0, Menu.menu_type.in_(['M', 'C'])).all()
        
    tree = build_menu_tree(menus, 0)
    return success(tree)

