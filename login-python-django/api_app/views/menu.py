from django.views.decorators.http import require_http_methods
from django.views.decorators.csrf import csrf_exempt
from utils.response import success, error
from utils.decorators import login_required
from api_app.models import Menu, UserRole, Role, RoleMenu, User

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
获取系统菜单树
"""
@csrf_exempt
@require_http_methods(["GET"])
@login_required
def get_tree(request):
    menus = Menu.objects.filter(deleted=0).order_by('sort')
    tree = build_menu_tree(menus, 0)
    return success(tree)

"""
获取用户专属动态菜单
"""
@csrf_exempt
@require_http_methods(["GET"])
@login_required
def get_user_menus(request):
    user = User.objects.filter(id=request.user_id, deleted=0).first()
    if not user:
        return error(404, "User not found")
        
    user_roles = UserRole.objects.filter(user_id=user.id)
    role_ids = [r.role_id for r in user_roles]
    roles = Role.objects.filter(id__in=role_ids, status=1)
    roles_dict = [r.role_key for r in roles] 
    
    if 'root' in roles_dict or user.username == 'admin':
        menus = Menu.objects.filter(status=1, deleted=0, menu_type__in=['M', 'C'])
    else:
        role_menus = RoleMenu.objects.filter(role_id__in=role_ids)
        menu_ids = [rm.menu_id for rm in role_menus]
        menus = Menu.objects.filter(id__in=menu_ids, status=1, deleted=0, menu_type__in=['M', 'C'])
        
    tree = build_menu_tree(menus, 0)
    return success(tree)
