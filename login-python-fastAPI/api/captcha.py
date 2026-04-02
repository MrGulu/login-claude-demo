from fastapi import APIRouter
from schemas.schemas import ResponseModel, success, error
from utils.captcha_generator import generate_math_captcha
from utils.cache import cache
import uuid

router = APIRouter(prefix="/captcha", tags=["Captcha"])

"""
生成数学验证码（加减法图片及结果）
:return: 包含验证码 UUID 和 Base64 图片数据的响应
"""
@router.get("/generate", response_model=ResponseModel)
def generate():
    try:
        result, img_base64 = generate_math_captcha()
        uuid_str = str(uuid.uuid4())
        cache.set(f"captcha_codes:{uuid_str}", str(result), timeout=300)
        
        return success({
            "uuid": uuid_str,
            "img": img_base64
        })
    except Exception as e:
        return error(500, f"验证码生成失败: {str(e)}")
