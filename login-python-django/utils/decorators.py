from functools import wraps
from utils.response import error
from utils.security import decode_token

def login_required(view_func):
    @wraps(view_func)
    def _wrapped_view(request, *args, **kwargs):
        auth_header = request.META.get('HTTP_AUTHORIZATION')
        if not auth_header or not auth_header.startswith("Bearer "):
            return error(401, "未登录或Token已过期，请重新登录")
            
        token = auth_header.split(" ")[1]
        payload = decode_token(token)
        
        if not payload:
            return error(401, 'Invalid token. Please log in again.')
            
        request.user_id = payload.get('userId')
        request.username = payload.get('sub')
        
        return view_func(request, *args, **kwargs)
    return _wrapped_view
