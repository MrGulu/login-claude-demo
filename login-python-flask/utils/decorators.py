from functools import wraps
from flask import request
from utils.response import error
from utils.security import decode_token

def login_required(f):
    @wraps(f)
    def decorated(*args, **kwargs):
        auth_header = request.headers.get("Authorization")
        if not auth_header or not auth_header.startswith("Bearer "):
            return error(401, "未登录或Token已过期，请重新登录")
            
        token = auth_header.split(" ")[1]
        payload = decode_token(token)
        
        if isinstance(payload, str):
            # Error string returned
            return error(401, payload)
            
        # Attach the user payload to the request context
        request.user_id = payload.get('userId')
        request.username = payload.get('sub')
        
        return f(*args, **kwargs)
    return decorated
