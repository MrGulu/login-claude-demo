from django.urls import path
from api_app.views import auth, captcha, user, menu, admin_users, admin_roles, admin_positions

urlpatterns = [
    # Auth
    path('api/auth/login', auth.login),
    path('api/auth/logout', auth.logout),
    path('api/auth/userinfo', auth.userinfo),
    path('api/auth/refresh', auth.refresh),
    
    # Captcha
    path('api/captcha/generate', captcha.generate),
    
    # User Profile
    path('api/user/profile', user.update_profile),
    path('api/user/avatar', user.update_avatar),
    path('api/user/password', user.update_password),
    
    # Menu
    path('api/menus/tree', menu.get_tree),
    path('api/menus/user', menu.get_user_menus),
    
    # Admin Users
    path('api/admin/users', admin_users.users_list),
    path('api/admin/users/<int:user_id>', admin_users.user_detail),
    path('api/admin/users/<int:user_id>/status', admin_users.user_status),
    path('api/admin/users/<int:user_id>/roles', admin_users.user_roles),
    path('api/admin/users/<int:user_id>/positions', admin_users.user_positions),
    
    # Admin Roles
    path('api/admin/roles', admin_roles.roles_list),
    path('api/admin/roles/<int:role_id>', admin_roles.role_detail),
    path('api/admin/roles/<int:role_id>/menus', admin_roles.role_menus),
    
    # Admin Positions
    path('api/admin/positions', admin_positions.positions_list),
    path('api/admin/positions/<int:position_id>', admin_positions.position_detail),
    path('api/admin/positions/<int:position_id>/status', admin_positions.position_status),
]
