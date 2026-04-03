from pydantic import BaseModel
from typing import Optional, List, Any
from datetime import datetime

class ResponseModel(BaseModel):
    code: int = 200
    message: str = "成功"
    data: Any = None

class LoginRequest(BaseModel):
    username: str
    password: str
    remember: Optional[bool] = False
    captchaKey: Optional[str] = None
    captcha: Optional[str] = None
    # For backward compatibility
    uuid: Optional[str] = None
    code: Optional[str] = None

class CaptchaResponse(BaseModel):
    captchaImage: str
    captchaKey: str

class PasswordUpdate(BaseModel):
    oldPassword: str
    newPassword: str

class UserProfileUpdate(BaseModel):
    nickname: Optional[str] = None
    email: Optional[str] = None
    phone: Optional[str] = None
    remark: Optional[str] = None

class UserCreate(BaseModel):
    username: str
    password: Optional[str] = '123456'
    nickname: Optional[str] = None
    email: Optional[str] = None
    phone: Optional[str] = None
    status: Optional[int] = 1
    remark: Optional[str] = None

class RoleCreate(BaseModel):
    roleName: str
    roleKey: str
    status: Optional[int] = 1
    sort: Optional[int] = 0
    remark: Optional[str] = None

class PositionCreate(BaseModel):
    positionName: str
    positionCode: str
    status: Optional[int] = 1
    sort: Optional[int] = 0
    remark: Optional[str] = None

def success(data=None, message="操作成功"):
    return ResponseModel(code=200, message=message, data=data)

def error(code=500, message="操作失败"):
    return ResponseModel(code=code, message=message, data=None)
