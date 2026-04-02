from fastapi import APIRouter, Depends, Body
from sqlalchemy.orm import Session
from models.database import get_db
from models.models import User
from schemas.schemas import ResponseModel, UserProfileUpdate, PasswordUpdate, success, error
from utils.security import hash_password, verify_password
from api.deps import get_current_user

router = APIRouter(prefix="/user", tags=["User"])

"""
修改当前登录用户的基础个人信息
:param data: 更新内容
:param current_user: 当前用户
:param db: 数据库会话
:return: 响应
"""
@router.put("/profile", response_model=ResponseModel)
def update_profile(
    data: UserProfileUpdate, 
    current_user: User = Depends(get_current_user), 
    db: Session = Depends(get_db)
):
    if data.nickname is not None: current_user.nickname = data.nickname
    if data.email is not None: current_user.email = data.email
    if data.phone is not None: current_user.phone = data.phone
    if data.remark is not None: current_user.remark = data.remark
    
    db.commit()
    return success(message="用户信息已更新")

"""
更新用户头像
:param avatar_data: 图片数据
:param current_user: 当前用户
:return: 响应
"""
@router.post("/avatar", response_model=ResponseModel)
def update_avatar(
    avatar_data: dict = Body(...),
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    avatar = avatar_data.get('avatar')
    if not avatar:
        return error(400, "Avatar URL required")
        
    current_user.avatar = avatar
    db.commit()
    return success(message="头像更新成功")

"""
修改用户密码
:param data: 旧密码和新密码
:return: 响应
"""
@router.put("/password", response_model=ResponseModel)
def update_password(
    data: PasswordUpdate, 
    current_user: User = Depends(get_current_user), 
    db: Session = Depends(get_db)
):
    if not verify_password(data.oldPassword, current_user.password):
        return error(400, "旧密码错误")
        
    current_user.password = hash_password(data.newPassword)
    db.commit()
    return success(message="密码更改成功")
