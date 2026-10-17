import os
import shutil
import sqlite3
from pathlib import Path

# Resolve path to fest.db at project root level (zenith26-fest-system/fest.db)
ROOT_DIR = Path(__file__).resolve().parent.parent
DB_PATH = ROOT_DIR / "fest.db"

# On Vercel serverless functions, copy sqlite database to /tmp if needed
if os.environ.get("VERCEL"):
    tmp_db = Path("/tmp/fest.db")
    if not tmp_db.exists() and DB_PATH.exists():
        shutil.copy2(DB_PATH, tmp_db)
    DB_PATH = tmp_db


def get_db_connection() -> sqlite3.Connection:
    """Creates and returns a SQLite connection with row access by column name."""
    conn = sqlite3.connect(str(DB_PATH))
    conn.row_factory = sqlite3.Row
    conn.execute("PRAGMA foreign_keys = ON")
    return conn


def init_db() -> None:
    """Initializes the database schema if tables do not already exist."""
    conn = get_db_connection()
    cursor = conn.cursor()

    cursor.execute("""
    CREATE TABLE IF NOT EXISTS events (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        title TEXT NOT NULL,
        category TEXT NOT NULL,
        description TEXT,
        event_date TEXT NOT NULL,
        event_time TEXT NOT NULL,
        venue TEXT NOT NULL,
        capacity INTEGER NOT NULL,
        fee INTEGER NOT NULL DEFAULT 0,
        icon TEXT
    )
    """)

    cursor.execute("""
    CREATE TABLE IF NOT EXISTS registrations (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        event_id INTEGER NOT NULL REFERENCES events(id),
        student_name TEXT NOT NULL,
        roll_number TEXT NOT NULL,
        email TEXT NOT NULL,
        phone TEXT,
        registered_at TEXT NOT NULL,
        UNIQUE(event_id, email)
    )
    """)

    # New tables for LearnEarly LMS
    cursor.execute("""
    CREATE TABLE IF NOT EXISTS topics (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        title TEXT NOT NULL,
        category TEXT NOT NULL,
        description TEXT,
        icon TEXT
    )
    """)

    cursor.execute("""
    CREATE TABLE IF NOT EXISTS lecture_notes (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        topic_id INTEGER NOT NULL REFERENCES topics(id) ON DELETE CASCADE,
        note_path TEXT NOT NULL,
        note_title TEXT NOT NULL
    )
    """)

    conn.commit()
    conn.close()

    try:
        from backend.lms_seed import init_lms_tables, seed_lms_data
        init_lms_tables()
        seed_lms_data()
    except Exception as e:
        print(f"LMS init notice: {e}")


if __name__ == "__main__":
    init_db()
    print("Database tables initialized successfully.")
