import bcrypt
import jwt
from datetime import datetime, timedelta

SECRET_KEY = "login-demo-secret-key-2024"
ALGORITHM = "HS512"

def verify_password(plain_password, hashed_password):
    password_byte_enc = plain_password.encode('utf-8')
    if isinstance(hashed_password, str):
        hashed_password = hashed_password.encode('utf-8')
    if hashed_password.startswith(b"$2a$"):
        hashed_password = hashed_password.replace(b"$2a$", b"$2b$")
    return bcrypt.checkpw(password_byte_enc, hashed_password)

def hash_password(plain_password):
    pwd_bytes = plain_password.encode('utf-8')
    salt = bcrypt.gensalt(10)
    hashed_password = bcrypt.hashpw(pwd_bytes, salt)
    return hashed_password.decode('utf-8').replace('$2b$', '$2a$')

def create_access_token(user_id, username):
    payload = {
        'exp': datetime.utcnow() + timedelta(seconds=7200),
        'iat': datetime.utcnow(),
        'sub': username,
        'userId': user_id
    }
    return jwt.encode(payload, SECRET_KEY, algorithm=ALGORITHM)

def decode_token(token):
    try:
        payload = jwt.decode(token, SECRET_KEY, algorithms=[ALGORITHM])
        return payload
    except Exception:
        return None
