from sqlalchemy import Column, Integer, String, DateTime
from datetime import datetime
from .database import Base

class User(Base):
    __tablename__ = 'sys_user'
    id = Column(Integer, primary_key=True, index=True, autoincrement=True)
    username = Column(String(50), unique=True, index=True, nullable=False)
    password = Column(String(255), nullable=False)
    nickname = Column(String(50))
    avatar = Column(String(255))
    email = Column(String(100))
    phone = Column(String(20))
    status = Column(Integer, default=1)
    deleted = Column(Integer, default=0)
    create_time = Column(DateTime, default=datetime.utcnow)
    update_time = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)
    create_by = Column(String(50))
    update_by = Column(String(50))
    remark = Column(String(500))

class Role(Base):
    __tablename__ = 'sys_role'
    id = Column(Integer, primary_key=True, index=True, autoincrement=True)
    role_name = Column(String(50), nullable=False)
    role_key = Column(String(50), nullable=False, unique=True, index=True)
    is_system = Column(Integer, default=0)
    status = Column(Integer, default=1)
    sort = Column(Integer, default=0)
    remark = Column(String(500))
    deleted = Column(Integer, default=0)
    create_time = Column(DateTime, default=datetime.utcnow)
    update_time = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)
    create_by = Column(String(50))
    update_by = Column(String(50))

class UserRole(Base):
    __tablename__ = 'sys_user_role'
    id = Column(Integer, primary_key=True, autoincrement=True)
    user_id = Column(Integer, nullable=False, index=True)
    role_id = Column(Integer, nullable=False, index=True)
    create_time = Column(DateTime, default=datetime.utcnow)

class Menu(Base):
    __tablename__ = 'sys_menu'
    id = Column(Integer, primary_key=True, index=True, autoincrement=True)
    parent_id = Column(Integer, default=0)
    menu_name = Column(String(50), nullable=False)
    menu_type = Column(String(1), default='C')
    path = Column(String(200))
    component = Column(String(200))
    perms = Column(String(100))
    icon = Column(String(50))
    sort = Column(Integer, default=0)
    visible = Column(Integer, default=1)
    status = Column(Integer, default=1)
    deleted = Column(Integer, default=0)
    create_time = Column(DateTime, default=datetime.utcnow)
    update_time = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)
    create_by = Column(String(50))
    update_by = Column(String(50))
    remark = Column(String(500))

class RoleMenu(Base):
    __tablename__ = 'sys_role_menu'
    id = Column(Integer, primary_key=True, autoincrement=True)
    role_id = Column(Integer, nullable=False, index=True)
    menu_id = Column(Integer, nullable=False, index=True)
    create_time = Column(DateTime, default=datetime.utcnow)

class Position(Base):
    __tablename__ = 'sys_position'
    id = Column(Integer, primary_key=True, autoincrement=True)
    position_name = Column(String(50), nullable=False)
    position_code = Column(String(50), nullable=False, unique=True, index=True)
    status = Column(Integer, default=1)
    sort = Column(Integer, default=0)
    remark = Column(String(500))
    deleted = Column(Integer, default=0)
    create_time = Column(DateTime, default=datetime.utcnow)
    update_time = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)
    create_by = Column(String(50))
    update_by = Column(String(50))

class UserPosition(Base):
    __tablename__ = 'sys_user_position'
    id = Column(Integer, primary_key=True, autoincrement=True)
    user_id = Column(Integer, nullable=False, index=True)
    position_id = Column(Integer, nullable=False, index=True)
    create_time = Column(DateTime, default=datetime.utcnow)
