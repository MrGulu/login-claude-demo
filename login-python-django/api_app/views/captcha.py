from django.views.decorators.http import require_http_methods
from django.views.decorators.csrf import csrf_exempt
from utils.response import success, error
from utils.captcha_generator import generate_math_captcha
from utils.cache import cache
import uuid

"""
生成验证码
"""
@csrf_exempt
@require_http_methods(["GET"])
def generate(request):
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
