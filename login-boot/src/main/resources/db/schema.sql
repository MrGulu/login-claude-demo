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

-- 插入测试数据（密码为 123456 的 BCrypt 加密）
INSERT OR IGNORE INTO sys_user (username, password, nickname, avatar, email, phone, status, deleted, remark)
VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '管理员',
 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
 'admin@example.com', '13800138000', 1, 0, '系统管理员'),
('user', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '普通用户',
 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
 'user@example.com', '13800138001', 1, 0, '普通用户');
