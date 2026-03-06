-- SQLite数据库初始化脚本
-- 注意：SQLite语法与PostgreSQL有所不同

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(50),
    avatar VARCHAR(255),
    email VARCHAR(100),
    phone VARCHAR(20),
    status INTEGER DEFAULT 1,
    deleted INTEGER DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    create_by VARCHAR(50),
    update_by VARCHAR(50),
    remark VARCHAR(500)
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_username ON sys_user(username);
CREATE INDEX IF NOT EXISTS idx_status ON sys_user(status);
CREATE INDEX IF NOT EXISTS idx_deleted ON sys_user(deleted);

-- 登录日志表
CREATE TABLE IF NOT EXISTS sys_login_log (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER,
    username VARCHAR(50),
    ip_address VARCHAR(50),
    login_location VARCHAR(100),
    browser VARCHAR(50),
    os VARCHAR(50),
    status INTEGER,
    message VARCHAR(255),
    login_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_user_id ON sys_login_log(user_id);
CREATE INDEX IF NOT EXISTS idx_login_time ON sys_login_log(login_time);

-- 密码重置日志表
CREATE TABLE IF NOT EXISTS sys_password_reset_log (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER,
    username VARCHAR(50) NOT NULL,
    reset_type VARCHAR(20),
    ip_address VARCHAR(50),
    status INTEGER,
    message VARCHAR(255),
    reset_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_reset_user_id ON sys_password_reset_log(user_id);
CREATE INDEX IF NOT EXISTS idx_reset_time ON sys_password_reset_log(reset_time);

-- 角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    role_name VARCHAR(50) NOT NULL,
    role_key VARCHAR(50) NOT NULL UNIQUE,
    is_system INTEGER DEFAULT 0,
    status INTEGER DEFAULT 1,
    sort INTEGER DEFAULT 0,
    remark VARCHAR(500),
    deleted INTEGER DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    create_by VARCHAR(50),
    update_by VARCHAR(50)
);

CREATE INDEX IF NOT EXISTS idx_role_key ON sys_role(role_key);
CREATE INDEX IF NOT EXISTS idx_role_status ON sys_role(status);

-- 用户-角色关联表
CREATE TABLE IF NOT EXISTS sys_user_role (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    role_id INTEGER NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_user_role ON sys_user_role(user_id, role_id);
CREATE INDEX IF NOT EXISTS idx_ur_user_id ON sys_user_role(user_id);
CREATE INDEX IF NOT EXISTS idx_ur_role_id ON sys_user_role(role_id);

-- 菜单表
CREATE TABLE IF NOT EXISTS sys_menu (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    parent_id INTEGER DEFAULT 0,
    menu_name VARCHAR(50) NOT NULL,
    menu_type VARCHAR(1) DEFAULT 'C',
    path VARCHAR(200),
    component VARCHAR(200),
    perms VARCHAR(100),
    icon VARCHAR(50),
    sort INTEGER DEFAULT 0,
    visible INTEGER DEFAULT 1,
    status INTEGER DEFAULT 1,
    deleted INTEGER DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    create_by VARCHAR(50),
    update_by VARCHAR(50),
    remark VARCHAR(500)
);

CREATE INDEX IF NOT EXISTS idx_parent_id ON sys_menu(parent_id);
CREATE INDEX IF NOT EXISTS idx_menu_type ON sys_menu(menu_type);
CREATE INDEX IF NOT EXISTS idx_menu_status ON sys_menu(status);

-- 角色-菜单关联表
CREATE TABLE IF NOT EXISTS sys_role_menu (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    role_id INTEGER NOT NULL,
    menu_id INTEGER NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_role_menu ON sys_role_menu(role_id, menu_id);
CREATE INDEX IF NOT EXISTS idx_rm_role_id ON sys_role_menu(role_id);
CREATE INDEX IF NOT EXISTS idx_rm_menu_id ON sys_role_menu(menu_id);

-- 插入测试数据（密码为 123456 的 BCrypt 加密）
INSERT OR IGNORE INTO sys_user (username, password, nickname, avatar, email, phone, status, deleted, remark)
VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '管理员',
 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
 'admin@example.com', '13800138000', 1, 0, '系统管理员'),
('user', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '普通用户',
 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
 'user@example.com', '13800138001', 1, 0, '普通用户');

-- 插入菜单数据
INSERT OR IGNORE INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, icon, sort, perms) VALUES
-- 一级菜单
(1, 0, '首页', 'C', '/home', 'HomeView', 'HomeFilled', 1, 'home:view'),
(2, 0, '个人资料', 'C', '/profile', 'ProfileView', 'User', 2, 'profile:view'),
(3, 0, '系统管理', 'M', '/system', NULL, 'Setting', 3, NULL),
-- 用户管理菜单及权限
(4, 3, '用户管理', 'C', '/system/users', 'system/UserManagement', 'User', 1, 'system:user:view'),
(5, 4, '查询用户', 'F', NULL, NULL, NULL, 1, 'system:user:query'),
(6, 4, '新增用户', 'F', NULL, NULL, NULL, 2, 'system:user:add'),
(7, 4, '编辑用户', 'F', NULL, NULL, NULL, 3, 'system:user:edit'),
(8, 4, '删除用户', 'F', NULL, NULL, NULL, 4, 'system:user:delete'),
(9, 4, '修改状态', 'F', NULL, NULL, NULL, 5, 'system:user:status'),
(10, 4, '分配角色', 'F', NULL, NULL, NULL, 6, 'system:user:role'),
-- 角色管理菜单及权限
(11, 3, '角色管理', 'C', '/system/roles', 'system/RoleManagement', 'UserFilled', 2, 'system:role:view'),
(12, 11, '查询角色', 'F', NULL, NULL, NULL, 1, 'system:role:query'),
(13, 11, '新增角色', 'F', NULL, NULL, NULL, 2, 'system:role:add'),
(14, 11, '编辑角色', 'F', NULL, NULL, NULL, 3, 'system:role:edit'),
(15, 11, '删除角色', 'F', NULL, NULL, NULL, 4, 'system:role:delete'),
(16, 11, '分配权限', 'F', NULL, NULL, NULL, 5, 'system:role:assign');

-- 插入 root 角色（系统角色，不可删除）
INSERT OR IGNORE INTO sys_role (id, role_name, role_key, is_system, status, sort, remark) VALUES
(1, '超级管理员', 'root', 1, 1, 1, '拥有所有权限，不可删除');

-- root 角色关联所有菜单
INSERT OR IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT 1, id FROM sys_menu WHERE deleted = 0;

-- admin 用户绑定 root 角色
INSERT OR IGNORE INTO sys_user_role (user_id, role_id)
SELECT id, 1 FROM sys_user WHERE username = 'admin';
