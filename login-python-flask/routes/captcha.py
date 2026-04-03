from flask import Blueprint, request
from utils.response import success, error
from utils.captcha_generator import generate_captcha
from utils.cache import cache
import uuid

captcha_bp = Blueprint('captcha', __name__, url_prefix='/api/captcha')

"""
生成验证码并返回 Base64 图片及 UUID
"""
@captcha_bp.route('/generate', methods=['GET'])
def generate():
    try:
        result, img_base64 = generate_captcha()
        # print("captcha_result=", result)
        
        # generate uuid
        uuid_str = str(uuid.uuid4())
        
        # save to cache, valid for 5 mins
        cache.set(f"captcha_codes:{uuid_str}", str(result), timeout=300)
        
        # match the typical spring boot VO
        return success({
            "captchaKey": uuid_str,
            "captchaImage": f"data:image/png;base64,{img_base64}"
        })
    except Exception as e:
        return error(500, f"验证码生成失败: {str(e)}")
