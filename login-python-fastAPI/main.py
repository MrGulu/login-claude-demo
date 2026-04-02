import os
import sqlite3
from fastapi import FastAPI, Request
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse
from api import auth, captcha, user, menu, admin_users, admin_roles, admin_positions

app = FastAPI(title="Login API FastAPI")

# CORS setup
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Exception handler to return {"code": 500, "message": str(e), "data": None} equivalent to Spring Boot
@app.exception_handler(Exception)
async def global_exception_handler(request: Request, exc: Exception):
    return JSONResponse(
        status_code=200, # Return HTTP 200 with code 500 in body
        content={"code": 500, "message": f"服务器内部错误: {str(exc)}", "data": None}
    )

def init_db():
    BASE_DIR = os.path.dirname(os.path.abspath(__file__))
    ROOT_DIR = os.path.dirname(BASE_DIR)
    DATA_DIR = os.path.join(ROOT_DIR, 'data')
    
    db_path = os.path.join(DATA_DIR, 'login.db')
    schema_path = os.path.join(DATA_DIR, 'schema.sql')
    
    if not os.path.exists(db_path):
        conn = sqlite3.connect(db_path)
        cursor = conn.cursor()
        
        with open(schema_path, 'r', encoding='utf-8') as f:
            schema_script = f.read()
            
        cursor.executescript(schema_script)
        conn.commit()
        conn.close()
        print("Database initialized from schema.sql")
        
@app.on_event("startup")
def startup_event():
    init_db()

# Prefix matches SpringBoot /api path
api_prefix = "/api"

app.include_router(auth.router, prefix=api_prefix)
app.include_router(captcha.router, prefix=api_prefix)
app.include_router(user.router, prefix=api_prefix)
app.include_router(menu.router, prefix=api_prefix)
app.include_router(admin_users.router, prefix=api_prefix)
app.include_router(admin_roles.router, prefix=api_prefix)
app.include_router(admin_positions.router, prefix=api_prefix)

if __name__ == '__main__':
    import uvicorn
    uvicorn.run("main:app", host="0.0.0.0", port=8080, reload=True)
