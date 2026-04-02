from passlib.context import CryptContext
import jwt
from datetime import datetime, timedelta

pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")
SECRET_KEY = "login-demo-secret-key-2024"
ALGORITHM = "HS512"

def verify_password(plain_password, hashed_password):
    if hashed_password.startswith("$2a$"):
        hashed_password = hashed_password.replace("$2a$", "$2b$")
    return pwd_context.verify(plain_password, hashed_password)

def hash_password(password):
    h = pwd_context.hash(password)
    return h.replace("$2b$", "$2a$")

def create_access_token(user_id: int, username: str, expires_delta: timedelta = timedelta(seconds=7200)):
    expire = datetime.utcnow() + expires_delta
    to_encode = {"userId": user_id, "sub": username, "exp": expire, "iat": datetime.utcnow()}
    encoded_jwt = jwt.encode(to_encode, SECRET_KEY, algorithm=ALGORITHM)
    return encoded_jwt

def decode_token(token: str):
    try:
        payload = jwt.decode(token, SECRET_KEY, algorithms=[ALGORITHM])
        return payload
    except Exception:
        return None
