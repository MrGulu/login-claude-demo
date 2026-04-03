import bcrypt
import jwt
from datetime import datetime, timedelta

SECRET_KEY = "login-demo-secret-key-2024"
ALGORITHM = "HS512"

def verify_password(plain_password, hashed_password):
    """
    校验明文密码与哈希密文是否匹配
    """
    password_byte_enc = plain_password.encode('utf-8')
    if isinstance(hashed_password, str):
        hashed_password = hashed_password.encode('utf-8')
    
    # 兼容 SQLite 中存储的 $2a$ 格式（bcrypt 库目前通常期待 $2b$）
    if hashed_password.startswith(b"$2a$"):
        hashed_password = hashed_password.replace(b"$2a$", b"$2b$")
        
    return bcrypt.checkpw(password_byte_enc, hashed_password)

def hash_password(password):
    """
    对密码进行 bcrypt 加密并统一存储格式为 $2a$
    """
    pwd_bytes = password.encode('utf-8')
    salt = bcrypt.gensalt(10)
    hashed_password = bcrypt.hashpw(pwd_bytes, salt)
    return hashed_password.decode('utf-8').replace('$2b$', '$2a$')

def create_access_token(user_id: int, username: str, expires_delta: timedelta = timedelta(seconds=7200)):
    """
    生成 JWT 访问令牌
    """
    expire = datetime.utcnow() + expires_delta
    to_encode = {"userId": user_id, "sub": username, "exp": expire, "iat": datetime.utcnow()}
    encoded_jwt = jwt.encode(to_encode, SECRET_KEY, algorithm=ALGORITHM)
    return encoded_jwt

def decode_token(token: str):
    """
    解码 JWT 访问令牌
    """
    try:
        payload = jwt.decode(token, SECRET_KEY, algorithms=[ALGORITHM])
        return payload
    except Exception:
        return None
