#!/usr/bin/env python
"""Django's command-line utility for administrative tasks."""
import os
import sys


def main():
    """Run administrative tasks."""
    import os
    import sys
    import sqlite3

    os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'login_django.settings')

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

    try:
        from django.core.management import execute_from_command_line
    except ImportError as exc:
        raise ImportError(
            "Couldn't import Django. Are you sure it's installed and "
            "available on your PYTHONPATH environment variable? Did you "
            "forget to activate a virtual environment?"
        ) from exc
    execute_from_command_line(sys.argv)


if __name__ == '__main__':
    main()
