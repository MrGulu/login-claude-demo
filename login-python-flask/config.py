import os

class Config:
    SECRET_KEY = os.environ.get('SECRET_KEY') or 'login-demo-secret-key-2024'
    BASE_DIR = os.path.dirname(os.path.abspath(__file__))
    ROOT_DIR = os.path.dirname(BASE_DIR)
    DATA_DIR = os.path.join(ROOT_DIR, 'data')
    SQLALCHEMY_DATABASE_URI = os.environ.get('DATABASE_URL') or 'sqlite:///' + os.path.join(DATA_DIR, 'login.db')
    SQLALCHEMY_TRACK_MODIFICATIONS = False
    JWT_SECRET = 'login-demo-secret-key-2024'
    JWT_EXPIRE = 7200
