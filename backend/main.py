from contextlib import asynccontextmanager
from datetime import datetime
from pathlib import Path
from typing import List, Optional, Dict, Any
from pydantic import BaseModel

from fastapi import FastAPI, HTTPException, Query, status
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import FileResponse
from fastapi.staticfiles import StaticFiles

from backend.database import get_db_connection, init_db
from backend.models import (
    EventCreate,
    EventDetail,
    EventOut,
    RegistrationIn,
    RegistrationItem,
    RegistrationOut,
    StatsOut,
    TopicOut,
    TopicCreate,
    LectureNoteOut,
)
from backend.seed_data import seed_events

FRONTEND_DIR = Path(__file__).resolve().parent.parent / "frontend"


@asynccontextmanager
async def lifespan(app: FastAPI):
    # Ensure database tables and seed data are ready on startup
    init_db()
    try:
        seed_events()
    except Exception as e:
        print(f"Startup notice: {e}")
    yield


app = FastAPI(
    title="Early Learn LMS",
    description="Personal Knowledge-to-Learning Management System built from Obsidian vault.",
    version="1.0.0",
    lifespan=lifespan,
)

# Enable CORS for local development and testing
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


# ==========================================
# API Endpoints
# ==========================================

@app.get("/api/events", response_model=List[EventOut], tags=["Events"])
def get_events(
    category: Optional[str] = Query(None, description="Filter by event category"),
    search: Optional[str] = Query(None, description="Search in event title (case-insensitive)"),
):
    """
    Returns list of events with live computed available_seats.
    Supports optional category filter and title search.
    """
    conn = get_db_connection()
    cursor = conn.cursor()

    query = """
    SELECT 
        e.id, e.title, e.category, e.description, e.event_date, e.event_time, 
        e.venue, e.capacity, e.fee, e.icon,
        COUNT(r.id) AS registered_count
    FROM events e
    LEFT JOIN registrations r ON e.id = r.event_id
    WHERE 1=1
    """
    params = []

    if category and category.strip().lower() != "all":
        query += " AND LOWER(e.category) = LOWER(?)"
        params.append(category.strip())

    if search and search.strip():
        query += " AND LOWER(e.title) LIKE LOWER(?)"
        params.append(f"%{search.strip()}%")

    query += " GROUP BY e.id ORDER BY e.event_date ASC, e.event_time ASC"

    cursor.execute(query, params)
    rows = cursor.fetchall()
    conn.close()

    events_list = []
    for row in rows:
        capacity = row["capacity"]
        registered = row["registered_count"]
        available_seats = max(0, capacity - registered)
        events_list.append(
            EventOut(
                id=row["id"],
                title=row["title"],
                category=row["category"],
                description=row["description"] or "",
                event_date=row["event_date"],
                event_time=row["event_time"],
                venue=row["venue"],
                capacity=capacity,
                fee=row["fee"],
                icon=row["icon"] or "",
                available_seats=available_seats,
            )
        )
    return events_list


@app.get("/api/events/{event_id}", response_model=EventDetail, tags=["Events"])
def get_event_detail(event_id: int):
    """
    Returns one event with live computed available_seats, or 404 if not found.
    """
    conn = get_db_connection()
    cursor = conn.cursor()

    cursor.execute(
        """
        SELECT 
            e.id, e.title, e.category, e.description, e.event_date, e.event_time, 
            e.venue, e.capacity, e.fee, e.icon,
            COUNT(r.id) AS registered_count
        FROM events e
        LEFT JOIN registrations r ON e.id = r.event_id
        WHERE e.id = ?
        GROUP BY e.id
        """,
        (event_id,),
    )
    row = cursor.fetchone()
    conn.close()

    if not row:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Event with id {event_id} not found",
        )

    capacity = row["capacity"]
    registered = row["registered_count"]
    available_seats = max(0, capacity - registered)

    return EventDetail(
        id=row["id"],
        title=row["title"],
        category=row["category"],
        description=row["description"] or "",
        event_date=row["event_date"],
        event_time=row["event_time"],
        venue=row["venue"],
        capacity=capacity,
        fee=row["fee"],
        icon=row["icon"] or "",
        available_seats=available_seats,
    )


@app.post(
    "/api/events/{event_id}/register",
    response_model=RegistrationOut,
    status_code=status.HTTP_201_CREATED,
    tags=["Registrations"],
)
def register_for_event(event_id: int, payload: RegistrationIn):
    """
    Registers a student for an event with rigorous server-side validation:
    - Event exists (404)
    - Required fields not empty (400)
    - Email format check (400)
    - Capacity check (409 "Event is full")
    - Unique email per event check (409 "Already registered")
    """
    student_name = payload.student_name.strip() if payload.student_name else ""
    roll_number = payload.roll_number.strip() if payload.roll_number else ""
    email = payload.email.strip().lower() if payload.email else ""
    phone = payload.phone.strip() if payload.phone else None

    # Server-side validation: empty checks
    if not student_name or not roll_number or not email:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Student name, roll number, and email are required",
        )

    # Server-side validation: email must contain "@"
    if "@" not in email or len(email) < 5 or "." not in email.split("@")[-1]:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Invalid email address: must contain '@' and a valid domain",
        )

    conn = get_db_connection()
    cursor = conn.cursor()

    # Check event existence and capacity
    cursor.execute(
        """
        SELECT 
            e.id, e.title, e.capacity,
            COUNT(r.id) AS registered_count
        FROM events e
        LEFT JOIN registrations r ON e.id = r.event_id
        WHERE e.id = ?
        GROUP BY e.id
        """,
        (event_id,),
    )
    event_row = cursor.fetchone()

    if not event_row:
        conn.close()
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Event with id {event_id} not found",
        )

    capacity = event_row["capacity"]
    registered = event_row["registered_count"]

    # Check if event is full
    if registered >= capacity:
        conn.close()
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="Event is full",
        )

    # Check if student already registered for this event
    cursor.execute(
        "SELECT id FROM registrations WHERE event_id = ? AND LOWER(email) = ?",
        (event_id, email),
    )
    existing_reg = cursor.fetchone()
    if existing_reg:
        conn.close()
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="Already registered for this event",
        )

    # Insert registration
    registered_at = datetime.now().isoformat()
    cursor.execute(
        """
        INSERT INTO registrations (event_id, student_name, roll_number, email, phone, registered_at)
        VALUES (?, ?, ?, ?, ?, ?)
        """,
        (event_id, student_name, roll_number, email, phone, registered_at),
    )
    new_reg_id = cursor.lastrowid
    conn.commit()
    conn.close()

    return RegistrationOut(
        id=new_reg_id,
        event_id=event_id,
        student_name=student_name,
        registered_at=registered_at,
    )


@app.get(
    "/api/registrations",
    response_model=List[RegistrationItem],
    tags=["Registrations"],
)
def get_registrations(event_id: Optional[int] = Query(None, description="Filter by event ID")):
    """
    Returns list of registrations, optionally filtered by event_id.
    Includes event_title so the admin dashboard doesn't need a second lookup.
    """
    conn = get_db_connection()
    cursor = conn.cursor()

    query = """
    SELECT 
        r.id, r.event_id, r.student_name, r.roll_number, r.email, r.phone, r.registered_at,
        e.title AS event_title
    FROM registrations r
    JOIN events e ON r.event_id = e.id
    """
    params = []

    if event_id is not None:
        query += " WHERE r.event_id = ?"
        params.append(event_id)

    query += " ORDER BY r.id DESC"

    cursor.execute(query, params)
    rows = cursor.fetchall()
    conn.close()

    return [
        RegistrationItem(
            id=r["id"],
            event_id=r["event_id"],
            student_name=r["student_name"],
            roll_number=r["roll_number"],
            email=r["email"],
            phone=r["phone"],
            registered_at=r["registered_at"],
            event_title=r["event_title"],
        )
        for r in rows
    ]


@app.get("/api/stats", response_model=StatsOut, tags=["Admin"])
def get_stats():
    """
    Returns fest statistics computed live from the database:
    - total_events
    - total_registrations
    - total_capacity
    - overall_utilization_pct (total_registrations / total_capacity * 100, rounded to 1 decimal)
    - upcoming_events_count (events with event_date today or later)
    - events_by_category (dict of category -> count)
    """
    conn = get_db_connection()
    cursor = conn.cursor()

    # Total events and capacity
    cursor.execute("SELECT COUNT(*) AS total_events, COALESCE(SUM(capacity), 0) AS total_capacity FROM events")
    events_agg = cursor.fetchone()
    total_events = events_agg["total_events"]
    total_capacity = events_agg["total_capacity"]

    # Total registrations
    cursor.execute("SELECT COUNT(*) AS total_reg FROM registrations")
    total_registrations = cursor.fetchone()["total_reg"]

    # Utilization %
    if total_capacity > 0:
        overall_utilization_pct = round((total_registrations / total_capacity) * 100, 1)
    else:
        overall_utilization_pct = 0.0

    # Upcoming events count: event_date >= current date (or fest dates)
    today_iso = datetime.now().strftime("%Y-%m-%d")
    cursor.execute("SELECT COUNT(*) AS upcoming FROM events WHERE event_date >= ?", (today_iso,))
    upcoming_events_count = cursor.fetchone()["upcoming"]

    # Events by category
    cursor.execute("SELECT category, COUNT(*) AS cat_count FROM events GROUP BY category")
    cat_rows = cursor.fetchall()
    events_by_category = {row["category"]: row["cat_count"] for row in cat_rows}

    conn.close()

    return StatsOut(
        total_events=total_events,
        total_registrations=total_registrations,
        total_capacity=total_capacity,
        overall_utilization_pct=overall_utilization_pct,
        upcoming_events_count=upcoming_events_count,
        events_by_category=events_by_category,
    )


# ==========================================
# Stretch Goals: Admin Endpoints
# ==========================================

@app.post(
    "/api/events",
    response_model=EventOut,
    status_code=status.HTTP_201_CREATED,
    tags=["Stretch Goals - Admin"],
)
def create_event(payload: EventCreate):
    """
    Stretch Goal: Allows admin to add a new event with full server-side validation.
    """
    title = payload.title.strip()
    category = payload.category.strip()
    venue = payload.venue.strip()
    event_date = payload.event_date.strip()
    event_time = payload.event_time.strip()

    if not title or not category or not venue or not event_date or not event_time:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Title, category, venue, event date, and event time are required",
        )

    if payload.capacity <= 0:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Capacity must be greater than 0",
        )

    conn = get_db_connection()
    cursor = conn.cursor()

    cursor.execute(
        """
        INSERT INTO events (title, category, description, event_date, event_time, venue, capacity, fee, icon)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """,
        (
            title,
            category,
            payload.description or "",
            event_date,
            event_time,
            venue,
            payload.capacity,
            payload.fee,
            payload.icon or "🎉",
        ),
    )
    new_event_id = cursor.lastrowid
    conn.commit()
    conn.close()

    return EventOut(
        id=new_event_id,
        title=title,
        category=category,
        description=payload.description or "",
        event_date=event_date,
        event_time=event_time,
        venue=venue,
        capacity=payload.capacity,
        fee=payload.fee,
        icon=payload.icon or "🎉",
        available_seats=payload.capacity,
    )


@app.delete(
    "/api/registrations/{registration_id}",
    status_code=status.HTTP_200_OK,
    tags=["Stretch Goals - Admin"],
)
def cancel_registration(registration_id: int):
    """
    Stretch Goal: Admin cancels a registration, freeing up the seat immediately.
    """
    conn = get_db_connection()
    cursor = conn.cursor()

    cursor.execute("SELECT id FROM registrations WHERE id = ?", (registration_id,))
    reg = cursor.fetchone()
    if not reg:
        conn.close()
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Registration {registration_id} not found",
        )

    cursor.execute("DELETE FROM registrations WHERE id = ?", (registration_id,))
    conn.commit()
    conn.close()

    return {"detail": "Registration cancelled successfully", "id": registration_id}


# ==========================================
# Static Files & Frontend Serving (Step 4)
# ==========================================

# Mount frontend/js and frontend/css directories for script and style files
app.mount("/js", StaticFiles(directory=str(FRONTEND_DIR / "js")), name="js")
app.mount("/css", StaticFiles(directory=str(FRONTEND_DIR / "css")), name="css")


@app.get("/", include_in_schema=False)
@app.get("/index.html", include_in_schema=False)
@app.get("/learn.html", include_in_schema=False)
@app.get("/learn", include_in_schema=False)
def serve_index():
    """Serves the Early Learn LMS dashboard."""
    return FileResponse(FRONTEND_DIR / "learn.html")


@app.get("/admin.html", include_in_schema=False)
def serve_admin():
    """Serves the fest coordinator admin dashboard."""
    return FileResponse(FRONTEND_DIR / "admin.html")


# Fallback static mount for other potential static assets (css, images, favicons)
# Fallback static mount for other potential static assets (css, images, favicons)
app.mount("/static", StaticFiles(directory=str(FRONTEND_DIR)), name="frontend_root")

# -------------------------------------------------
# LearnEarly LMS Topic endpoints
@app.get("/api/topics", response_model=List[TopicOut], tags=["Topics"])
def get_topics():
    """Return all topics."""
    conn = get_db_connection()
    cursor = conn.cursor()
    cursor.execute("SELECT id, title, category, description, icon FROM topics ORDER BY title ASC")
    rows = cursor.fetchall()
    conn.close()
    return [TopicOut(**row) for row in rows]

@app.get("/api/topics/{topic_id}", response_model=TopicOut, tags=["Topics"])
def get_topic_detail(topic_id: int):
    """Return a single topic."""
    conn = get_db_connection()
    cursor = conn.cursor()
    cursor.execute("SELECT id, title, category, description, icon FROM topics WHERE id = ?", (topic_id,))
    row = cursor.fetchone()
    conn.close()
    if not row:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail=f"Topic {topic_id} not found")
    return TopicOut(**row)

@app.get("/api/topics/{topic_id}/notes", response_model=List[LectureNoteOut], tags=["Topics"])
def get_topic_notes(topic_id: int):
    """Return all lecture notes linked to a topic."""
    conn = get_db_connection()
    cursor = conn.cursor()
    cursor.execute(
        "SELECT id, topic_id, note_path, note_title FROM lecture_notes WHERE topic_id = ? ORDER BY note_title ASC",
        (topic_id,),
    )
    rows = cursor.fetchall()
    conn.close()
    return [LectureNoteOut(**row) for row in rows]

@app.get("/api/notes/content", tags=["Notes"])
def get_note_content(path: str):
    """Render a markdown note from the vault as HTML.
    ``path`` should be the absolute path to the markdown file, or relative to vault.
    """
    import pathlib, markdown
    note_path = pathlib.Path(path)
    
    # Fallback to bundled data/vault if local path not found (e.g. on Vercel deployment)
    if not note_path.is_file():
        vault_fallback = Path(__file__).resolve().parent.parent / "data" / "vault"
        filename = note_path.name
        candidates = list(vault_fallback.rglob(f"*{filename}*"))
        if candidates:
            note_path = candidates[0]

    if not note_path.is_file():
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Note file not found")
    try:
        text = note_path.read_text(encoding="utf-8")
    except Exception as e:
        raise HTTPException(status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, detail=str(e))
    html = markdown.markdown(text, extensions=["fenced_code", "codehilite", "tables", "toc"])
    return {"html": html}


@app.get("/api/vault/status", tags=["Vault"])
def get_vault_status():
    """Returns connected vault information and sync metrics."""
    conn = get_db_connection()
    cursor = conn.cursor()
    cursor.execute("SELECT COUNT(*) AS total_notes FROM lecture_notes")
    total_notes = cursor.fetchone()["total_notes"]
    
    cursor.execute("SELECT t.title, COUNT(n.id) as count FROM topics t LEFT JOIN lecture_notes n ON t.id = n.topic_id GROUP BY t.id")
    category_rows = cursor.fetchall()
    conn.close()

    vault_path = Path(r"D:\o.vault\temp_unzip\PM2-Obsidian-Vault - Copy")
    if not vault_path.is_dir():
        vault_path = Path(__file__).resolve().parent.parent / "data" / "vault"
    exists = vault_path.is_dir()
    
    return {
        "status": "connected" if exists else "disconnected",
        "vault_path": str(vault_path),
        "total_notes": total_notes,
        "is_connected": exists,
        "last_sync": "Active (Obsidian Vault)",
        "categories": [{"name": row["title"], "count": row["count"]} for row in category_rows]
    }


@app.get("/api/notes/search", tags=["Notes"])
def search_notes(q: str = Query(..., min_length=1, description="Search query")):
    """Searches notes by title or content."""
    conn = get_db_connection()
    cursor = conn.cursor()
    cursor.execute(
        """
        SELECT n.id, n.topic_id, n.note_path, n.note_title, t.title AS topic_title
        FROM lecture_notes n
        JOIN topics t ON n.topic_id = t.id
        WHERE LOWER(n.note_title) LIKE LOWER(?) OR LOWER(n.note_path) LIKE LOWER(?)
        LIMIT 25
        """,
        (f"%{q}%", f"%{q}%")
    )
    rows = cursor.fetchall()
    conn.close()
    return [
        {
            "id": r["id"],
            "note_title": r["note_title"],
            "note_path": r["note_path"],
            "topic_title": r["topic_title"]
        }
        for r in rows
    ]


@app.get("/api/lms/dashboard", tags=["LMS"])
def get_lms_dashboard():
    """Returns dynamic data for the Learn Early dashboard from SQLite."""
    conn = get_db_connection()
    cursor = conn.cursor()

    # User Settings
    cursor.execute("SELECT * FROM lms_settings WHERE id = 1")
    user_row = cursor.fetchone()
    if user_row:
        user_data = {
            "name": user_row["user_name"],
            "streak_pct": user_row["streak_pct"],
            "streak_days": user_row["streak_days"],
            "avatar": user_row["avatar_url"] or "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=100&auto=format&fit=crop&q=80",
            "theme": user_row["theme"],
            "total_studied": user_row["total_studied"]
        }
    else:
        user_data = {
            "name": "Khushi",
            "streak_pct": 10,
            "streak_days": 4,
            "avatar": "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=100&auto=format&fit=crop&q=80",
            "theme": "dark",
            "total_studied": 18
        }

    # Active Daily Class
    cursor.execute("SELECT * FROM lms_daily_classes WHERE is_active = 1 LIMIT 1")
    dc_row = cursor.fetchone()
    if not dc_row:
        cursor.execute("SELECT * FROM lms_daily_classes ORDER BY day_number ASC LIMIT 1")
        dc_row = cursor.fetchone()

    live_class = {
        "day": dc_row["day_tag"] if dc_row else "DAY 4",
        "title": dc_row["title"] if dc_row else "DAY 4: API Testing with Postman",
        "subtitle": dc_row["subtitle"] if dc_row else "Live interactive class, JWT tokens, and hands-on testing.",
        "note_path": dc_row["note_path"] if dc_row else "",
        "icon": dc_row["icon"] if dc_row else "🚀"
    }

    # Tools
    cursor.execute("SELECT * FROM lms_tools ORDER BY display_order ASC, id ASC")
    tool_rows = cursor.fetchall()
    toolkit = []
    for tr in tool_rows:
        t_id = tr["id"]
        cursor.execute("SELECT step_number, title, level, summary, note_path, status FROM lms_tool_topics WHERE tool_id = ? ORDER BY step_number ASC", (t_id,))
        topics = [dict(row) for row in cursor.fetchall()]
        toolkit.append({
            "id": t_id,
            "name": tr["name"],
            "category": tr["category"],
            "icon": tr["icon"],
            "badge_color": tr["badge_color"],
            "completed_pct": tr["completed_pct"],
            "hands_on": bool(tr["hands_on"]),
            "note_title": tr["note_title"],
            "note_path": tr["note_path"],
            "is_custom": bool(tr["is_custom"]),
            "topics": topics
        })

    # Challenges
    challenges = [
        {"id": 1, "title": "Deploy SQLite Query", "progress": 75, "xp": 250, "note_path": toolkit[-1]["note_path"] if toolkit else ""},
        {"id": 2, "title": "Graph 10 Obsidian Links", "progress": 40, "xp": 180, "note_path": toolkit[2]["note_path"] if len(toolkit) > 2 else ""},
        {"id": 3, "title": "Master Postman API Testing", "progress": 85, "xp": 300, "note_path": toolkit[1]["note_path"] if len(toolkit) > 1 else ""},
        {"id": 4, "title": "Claud Architecture Blueprint", "progress": 60, "xp": 220, "note_path": toolkit[4]["note_path"] if len(toolkit) > 4 else ""}
    ]

    # Knowledge Graph with ALL tools: Anti-Gravity, Postman, Obsidian, Pomeli AI, Claud Design, SQLite
    cursor.execute("SELECT * FROM lms_knowledge_links")
    link_rows = cursor.fetchall()
    links = [
        {
            "source": r["source_id"],
            "target": r["target_id"],
            "strength": r["strength"],
            "weight": r["weight"],
            "why_linked": r["why_linked"],
            "data_flow": r["data_flow"]
        }
        for r in link_rows
    ]

    nodes = [
        {"id": "anti-gravity", "label": "Anti-Gravity", "sub": "Prioritization", "group": "cyan", "x": 60, "y": 60, "connections": 3},
        {"id": "postman", "label": "Postman", "sub": "API Testing", "group": "orange", "x": 200, "y": 50, "connections": 2},
        {"id": "obsidian", "label": "Obsidian", "sub": "Knowledge Vault", "group": "purple", "x": 70, "y": 180, "connections": 3},
        {"id": "pomeli-ai", "label": "Pomeli AI", "sub": "Product Research", "group": "purple", "x": 160, "y": 115, "connections": 3},
        {"id": "claud-design", "label": "Claud Design", "sub": "System Arch.", "group": "blue", "x": 270, "y": 110, "connections": 3},
        {"id": "sqlite", "label": "SQLite", "sub": "Data Analytics", "group": "blue", "x": 220, "y": 195, "connections": 3},
        {"id": "pm-frameworks", "label": "PM Frameworks", "sub": "Core Models", "group": "indigo", "x": 330, "y": 185, "connections": 2}
    ]

    conn.close()

    return {
        "user": user_data,
        "live_class": live_class,
        "toolkit": toolkit,
        "challenges": challenges,
        "graph": {
            "nodes": nodes,
            "links": links
        }
    }


@app.get("/api/lms/tools/{tool_id}/topics", tags=["LMS"])
def get_tool_topics(tool_id: str):
    """Returns numbered 1, 2, 3, 4 topics from basics to advanced for a tool."""
    conn = get_db_connection()
    cursor = conn.cursor()
    cursor.execute("""
    SELECT step_number, title, level, summary, note_path, status
    FROM lms_tool_topics
    WHERE tool_id = ?
    ORDER BY step_number ASC
    """, (tool_id,))
    rows = cursor.fetchall()
    conn.close()
    return [dict(r) for r in rows]


@app.get("/api/lms/study/questions", tags=["LMS"])
def get_study_questions(tool_id: Optional[str] = Query(None)):
    """Returns question bank for Study Labs practice."""
    conn = get_db_connection()
    cursor = conn.cursor()
    if tool_id and tool_id != "all":
        cursor.execute("SELECT * FROM lms_study_questions WHERE tool_id = ? ORDER BY id ASC", (tool_id,))
    else:
        cursor.execute("SELECT * FROM lms_study_questions ORDER BY id ASC")
    rows = cursor.fetchall()
    conn.close()
    import json
    return [
        {
            "id": r["id"],
            "tool_id": r["tool_id"],
            "topic_title": r["topic_title"],
            "question": r["question"],
            "options": json.loads(r["options_json"]),
            "correct_index": r["correct_index"],
            "explanation": r["explanation"]
        }
        for r in rows
    ]


@app.get("/api/lms/study/tasks", tags=["LMS"])
def get_study_tasks(tool_id: Optional[str] = Query(None)):
    """Returns hands-on practical assignments distinct from MCQs."""
    conn = get_db_connection()
    cursor = conn.cursor()
    if tool_id and tool_id != "all":
        cursor.execute("SELECT * FROM lms_tasks WHERE tool_id = ? ORDER BY id ASC", (tool_id,))
    else:
        cursor.execute("SELECT * FROM lms_tasks ORDER BY id ASC")
    rows = cursor.fetchall()
    conn.close()
    import json
    return [
        {
            "id": r["id"],
            "tool_id": r["tool_id"],
            "title": r["title"],
            "description": r["description"],
            "steps": json.loads(r["steps_json"]),
            "difficulty": r["difficulty"],
            "xp": r["xp"]
        }
        for r in rows
    ]


@app.get("/api/lms/knowledge-web", tags=["LMS"])
def get_knowledge_web_details():
    """Returns full Knowledge Web network data, weights, and connection reasons."""
    conn = get_db_connection()
    cursor = conn.cursor()
    cursor.execute("SELECT * FROM lms_knowledge_links")
    link_rows = cursor.fetchall()
    cursor.execute("SELECT id, name, category, icon FROM lms_tools")
    tool_rows = cursor.fetchall()
    conn.close()

    tools_map = {r["id"]: dict(r) for r in tool_rows}
    links = [
        {
            "source_id": r["source_id"],
            "target_id": r["target_id"],
            "strength": r["strength"],
            "weight": r["weight"],
            "why_linked": r["why_linked"],
            "data_flow": r["data_flow"]
        }
        for r in link_rows
    ]
    return {"tools": tools_map, "links": links}


@app.get("/api/lms/settings", tags=["LMS"])
def get_lms_settings():
    """Returns current user settings, study analytics, and session timeline."""
    conn = get_db_connection()
    cursor = conn.cursor()
    cursor.execute("SELECT * FROM lms_settings WHERE id = 1")
    user_row = cursor.fetchone()
    cursor.execute("SELECT * FROM lms_activity_log ORDER BY id DESC LIMIT 15")
    logs = [dict(r) for r in cursor.fetchall()]
    cursor.execute("SELECT COUNT(*) as tool_cnt FROM lms_tools")
    tool_cnt = cursor.fetchone()["tool_cnt"]
    cursor.execute("SELECT COUNT(*) as class_cnt FROM lms_daily_classes")
    class_cnt = cursor.fetchone()["class_cnt"]
    conn.close()

    return {
        "user_name": user_row["user_name"] if user_row else "Khushi",
        "avatar_url": user_row["avatar_url"] if user_row else "",
        "theme": user_row["theme"] if user_row else "dark",
        "streak_pct": user_row["streak_pct"] if user_row else 10,
        "streak_days": user_row["streak_days"] if user_row else 4,
        "total_studied": user_row["total_studied"] if user_row else 18,
        "mcqs_attempted": user_row["mcqs_attempted"] if user_row else 42,
        "mcqs_correct": user_row["mcqs_correct"] if user_row else 38,
        "total_tools": tool_cnt,
        "total_classes": class_cnt,
        "activity_logs": logs
    }


class SettingsUpdateIn(BaseModel):
    user_name: Optional[str] = None
    avatar_url: Optional[str] = None
    theme: Optional[str] = None


@app.post("/api/lms/settings", tags=["LMS"])
def update_lms_settings(payload: SettingsUpdateIn):
    """Updates user profile settings (name, avatar, theme)."""
    conn = get_db_connection()
    cursor = conn.cursor()
    if payload.user_name:
        cursor.execute("UPDATE lms_settings SET user_name = ? WHERE id = 1", (payload.user_name.strip(),))
    if payload.avatar_url:
        cursor.execute("UPDATE lms_settings SET avatar_url = ? WHERE id = 1", (payload.avatar_url.strip(),))
    if payload.theme:
        cursor.execute("UPDATE lms_settings SET theme = ? WHERE id = 1", (payload.theme.strip().lower(),))
    conn.commit()
    conn.close()
    return {"status": "success", "message": "Settings updated"}


class ToolCreateIn(BaseModel):
    id: str
    name: str
    category: str
    icon: Optional[str] = "delta"
    topics: Optional[List[str]] = []


@app.post("/api/lms/tools", tags=["LMS"])
def create_lms_tool(payload: ToolCreateIn):
    """Allows user to add a new Tool & Application with line-wise topics."""
    conn = get_db_connection()
    cursor = conn.cursor()
    tool_id = payload.id.strip().lower().replace(" ", "-")
    cursor.execute("""
    INSERT OR REPLACE INTO lms_tools (id, name, category, icon, badge_color, completed_pct, hands_on, note_title, note_path, is_custom, display_order)
    VALUES (?, ?, ?, ?, ?, 0, 1, ?, '', 1, 99)
    """, (tool_id, payload.name.strip(), payload.category.strip(), payload.icon or "delta", "cyan", f"{payload.name} Overview"))

    if payload.topics:
        for idx, t_title in enumerate(payload.topics, start=1):
            level = "Basics" if idx <= 2 else ("Intermediate" if idx <= 4 else "Advanced")
            cursor.execute("""
            INSERT INTO lms_tool_topics (tool_id, step_number, title, level, summary, note_path, status)
            VALUES (?, ?, ?, ?, ?, '', 'pending')
            """, (tool_id, idx, t_title.strip(), level, f"Core principles of {t_title}"))

    conn.commit()
    conn.close()
    return {"status": "success", "tool_id": tool_id}


@app.delete("/api/lms/tools/{tool_id}", tags=["LMS"])
def delete_lms_tool(tool_id: str):
    """Allows user to eliminate / delete a Tool & Application."""
    conn = get_db_connection()
    cursor = conn.cursor()
    cursor.execute("DELETE FROM lms_tools WHERE id = ?", (tool_id,))
    cursor.execute("DELETE FROM lms_tool_topics WHERE tool_id = ?", (tool_id,))
    conn.commit()
    conn.close()
    return {"status": "success", "deleted_id": tool_id}


class DailyClassCreateIn(BaseModel):
    day_number: int
    day_tag: str
    title: str
    subtitle: Optional[str] = ""
    note_path: Optional[str] = ""
    icon: Optional[str] = "🚀"


@app.post("/api/lms/daily-classes", tags=["LMS"])
def add_daily_class(payload: DailyClassCreateIn):
    """Allows user to add a new Daily Class to the hub."""
    conn = get_db_connection()
    cursor = conn.cursor()
    cursor.execute("""
    INSERT INTO lms_daily_classes (day_number, day_tag, title, subtitle, note_path, icon, is_active)
    VALUES (?, ?, ?, ?, ?, ?, 0)
    """, (payload.day_number, payload.day_tag.strip(), payload.title.strip(), payload.subtitle.strip(), payload.note_path.strip(), payload.icon or "🚀"))
    conn.commit()
    conn.close()
    return {"status": "success"}


@app.get("/api/lms/daily-classes", tags=["LMS"])
def get_daily_classes():
    """Returns all scheduled daily classes."""
    conn = get_db_connection()
    cursor = conn.cursor()
    cursor.execute("SELECT * FROM lms_daily_classes ORDER BY day_number ASC")
    rows = cursor.fetchall()
    conn.close()
    return [dict(r) for r in rows]


