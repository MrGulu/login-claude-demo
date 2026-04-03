import sqlite3
import os

db_path = r'c:\Users\it001\login-claude-demo\data\login.db'
if os.path.exists(db_path):
    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()
    cursor.execute("SELECT username, password FROM sys_user WHERE username='admin'")
    row = cursor.fetchone()
    if row:
        print(f"DEBUG_INFO: Username: {row[0]}")
        print(f"DEBUG_INFO: Hashed Password in DB: {row[1]}")
    else:
        print("DEBUG_INFO: Admin user not found in DB.")
    conn.close()
else:
    print(f"DEBUG_INFO: DB file not found at {db_path}")
