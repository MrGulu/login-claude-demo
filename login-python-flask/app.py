import os
import sqlite3
from flask import Flask, jsonify
from config import Config
from extensions import db, cors

def init_db(app):
    with app.app_context():
        # Using native sqlite to execute the schema script because SQLAlchemy can't execute multiple statements at once easily
        BASE_DIR = os.path.dirname(os.path.abspath(__file__))
        ROOT_DIR = os.path.dirname(BASE_DIR)
        DATA_DIR = os.path.join(ROOT_DIR, 'data')
        
        db_path = os.path.join(DATA_DIR, 'login.db')
        schema_path = os.path.join(DATA_DIR, 'schema.sql')
        
        # Check if sys_user exists, if not execute schema script
        if not os.path.exists(db_path):
            conn = sqlite3.connect(db_path)
            cursor = conn.cursor()
            
            with open(schema_path, 'r', encoding='utf-8') as f:
                schema_script = f.read()
                
            cursor.executescript(schema_script)
            conn.commit()
            conn.close()
            print("Database initialized from schema.sql")
        else:
            print("Database already exists.")
            
def create_app():
    app = Flask(__name__)
    app.config.from_object(Config)

    # Initialize extensions
    db.init_app(app)
    cors.init_app(app, resources={r"/api/*": {"origins": "*"}})

    # Register blueprints
    from routes.auth import auth_bp
    from routes.captcha import captcha_bp
    from routes.user import user_bp
    from routes.menu import menu_bp
    from routes.admin_users import admin_users_bp
    from routes.admin_roles import admin_roles_bp
    from routes.admin_positions import admin_positions_bp
    from routes.password_reset import password_reset_bp

    app.register_blueprint(auth_bp)
    app.register_blueprint(captcha_bp)
    app.register_blueprint(user_bp)
    app.register_blueprint(menu_bp)
    app.register_blueprint(admin_users_bp)
    app.register_blueprint(admin_roles_bp)
    app.register_blueprint(admin_positions_bp)
    app.register_blueprint(password_reset_bp)

    # Global exception handler
    @app.errorhandler(Exception)
    def handle_exception(e):
        return jsonify({
            "code": 500,
            "message": f"服务器内部错误: {str(e)}",
            "data": None
        }), 200 # Returning 200 HTTP status to match common Spring Boot frontend setups

    # Init DB
    init_db(app)

    return app

if __name__ == '__main__':
    app = create_app()
    app.run(host='0.0.0.0', port=8080, debug=True)
