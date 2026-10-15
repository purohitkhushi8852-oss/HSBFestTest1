from contextlib import asynccontextmanager
from datetime import datetime
from pathlib import Path
from typing import List, Optional, Dict, Any

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
    title="Zenith '26 College Fest Management System",
    description="API for the Zenith '26 College Fest student portal and admin dashboard.",
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

# Mount frontend/js directory for script files
app.mount("/js", StaticFiles(directory=str(FRONTEND_DIR / "js")), name="js")


@app.get("/", include_in_schema=False)
def serve_index():
    """Serves the student event portal."""
    return FileResponse(FRONTEND_DIR / "index.html")


@app.get("/admin.html", include_in_schema=False)
def serve_admin():
    """Serves the fest coordinator admin dashboard."""
    return FileResponse(FRONTEND_DIR / "admin.html")


# Fallback static mount for other potential static assets (css, images, favicons)
app.mount("/", StaticFiles(directory=str(FRONTEND_DIR)), name="frontend_root")
