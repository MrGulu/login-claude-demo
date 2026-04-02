import bcrypt
import jwt
from datetime import datetime, timedelta
from config import Config

def verify_password(plain_password, hashed_password):
    # Spring security generates `$2a$` prefix, python bcrypt needs bytes
    password_byte_enc = plain_password.encode('utf-8')
    if isinstance(hashed_password, str):
        hashed_password = hashed_password.encode('utf-8')
    return bcrypt.checkpw(password_byte_enc, hashed_password)

def hash_password(plain_password):
    pwd_bytes = plain_password.encode('utf-8')
    salt = bcrypt.gensalt(10) # 10 rounds as standard Spring Boot BCrypt
    hashed_password = bcrypt.hashpw(pwd_bytes, salt)
    return hashed_password.decode('utf-8').replace('$2b$', '$2a$')

def generate_token(user_id, username):
    payload = {
        'exp': datetime.utcnow() + timedelta(seconds=Config.JWT_EXPIRE),
        'iat': datetime.utcnow(),
        'sub': username,
        'userId': user_id
    }
    return jwt.encode(payload, Config.JWT_SECRET, algorithm='HS512')

def decode_token(token):
    try:
        payload = jwt.decode(token, Config.JWT_SECRET, algorithms=['HS512'])
        return payload
    except jwt.ExpiredSignatureError:
        return 'Signature expired. Please log in again.'
    except jwt.InvalidTokenError:
        return 'Invalid token. Please log in again.'
