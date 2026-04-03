from datetime import datetime
from extensions import db

class User(db.Model):
    __tablename__ = 'sys_user'
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    username = db.Column(db.String(50), unique=True, nullable=False)
    password = db.Column(db.String(255), nullable=False)
    nickname = db.Column(db.String(50))
    avatar = db.Column(db.String(255))
    email = db.Column(db.String(100))
    phone = db.Column(db.String(20))
    status = db.Column(db.Integer, default=1)
    deleted = db.Column(db.Integer, default=0)
    create_time = db.Column(db.DateTime, default=datetime.utcnow)
    update_time = db.Column(db.DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)
    create_by = db.Column(db.String(50))
    update_by = db.Column(db.String(50))
    remark = db.Column(db.String(500))

    def to_dict(self):
        return {
            'id': str(self.id),
            'username': self.username,
            'nickname': self.nickname,
            'avatar': self.avatar,
            'email': self.email,
            'phone': self.phone,
            'status': self.status,
            'remark': self.remark,
            'createTime': self.create_time.strftime('%Y-%m-%d %H:%M:%S') if self.create_time else None
        }

class LoginLog(db.Model):
    __tablename__ = 'sys_login_log'
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    user_id = db.Column(db.Integer)
    username = db.Column(db.String(50))
    ip_address = db.Column(db.String(50))
    login_location = db.Column(db.String(100))
    browser = db.Column(db.String(50))
    os = db.Column(db.String(50))
    status = db.Column(db.Integer)
    message = db.Column(db.String(255))
    login_time = db.Column(db.DateTime, default=datetime.utcnow)

class PasswordResetLog(db.Model):
    __tablename__ = 'sys_password_reset_log'
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    user_id = db.Column(db.Integer)
    username = db.Column(db.String(50), nullable=False)
    reset_type = db.Column(db.String(20))
    ip_address = db.Column(db.String(50))
    status = db.Column(db.Integer)
    message = db.Column(db.String(255))
    reset_time = db.Column(db.DateTime, default=datetime.utcnow)

class Role(db.Model):
    __tablename__ = 'sys_role'
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    role_name = db.Column(db.String(50), nullable=False)
    role_key = db.Column(db.String(50), nullable=False, unique=True)
    is_system = db.Column(db.Integer, default=0)
    status = db.Column(db.Integer, default=1)
    sort = db.Column(db.Integer, default=0)
    remark = db.Column(db.String(500))
    deleted = db.Column(db.Integer, default=0)
    create_time = db.Column(db.DateTime, default=datetime.utcnow)
    update_time = db.Column(db.DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)
    create_by = db.Column(db.String(50))
    update_by = db.Column(db.String(50))

class UserRole(db.Model):
    __tablename__ = 'sys_user_role'
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    user_id = db.Column(db.Integer, nullable=False)
    role_id = db.Column(db.Integer, nullable=False)
    create_time = db.Column(db.DateTime, default=datetime.utcnow)

class Menu(db.Model):
    __tablename__ = 'sys_menu'
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    parent_id = db.Column(db.Integer, default=0)
    menu_name = db.Column(db.String(50), nullable=False)
    menu_type = db.Column(db.String(1), default='C')
    path = db.Column(db.String(200))
    component = db.Column(db.String(200))
    perms = db.Column(db.String(100))
    icon = db.Column(db.String(50))
    sort = db.Column(db.Integer, default=0)
    visible = db.Column(db.Integer, default=1)
    status = db.Column(db.Integer, default=1)
    deleted = db.Column(db.Integer, default=0)
    create_time = db.Column(db.DateTime, default=datetime.utcnow)
    update_time = db.Column(db.DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)
    create_by = db.Column(db.String(50))
    update_by = db.Column(db.String(50))
    remark = db.Column(db.String(500))

class RoleMenu(db.Model):
    __tablename__ = 'sys_role_menu'
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    role_id = db.Column(db.Integer, nullable=False)
    menu_id = db.Column(db.Integer, nullable=False)
    create_time = db.Column(db.DateTime, default=datetime.utcnow)

class Position(db.Model):
    __tablename__ = 'sys_position'
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    position_name = db.Column(db.String(50), nullable=False)
    position_code = db.Column(db.String(50), nullable=False, unique=True)
    status = db.Column(db.Integer, default=1)
    sort = db.Column(db.Integer, default=0)
    remark = db.Column(db.String(500))
    deleted = db.Column(db.Integer, default=0)
    create_time = db.Column(db.DateTime, default=datetime.utcnow)
    update_time = db.Column(db.DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)
    create_by = db.Column(db.String(50))
    update_by = db.Column(db.String(50))

class UserPosition(db.Model):
    __tablename__ = 'sys_user_position'
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    user_id = db.Column(db.Integer, nullable=False)
    position_id = db.Column(db.Integer, nullable=False)
    create_time = db.Column(db.DateTime, default=datetime.utcnow)
