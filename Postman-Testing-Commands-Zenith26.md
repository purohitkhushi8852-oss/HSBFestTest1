# Postman Testing Commands: Zenith '26 API Test Suite

**Companion to:** PM2-01 (How Software Works) and PM2-02 (Front-End, Back-End & Mobile)  
**Target API:** Zenith '26 College Fest Management System (`http://localhost:8081`)  
**Postman Collection:** `zenith26-api.postman_collection.json`

---

## 1. Quick Setup in Postman

1. Ensure the backend server is running:
   ```bash
   cd zenith26-fest-system
   .\.venv\Scripts\uvicorn.exe backend.main:app --reload
   ```
2. Open **Postman Desktop** or Postman Web.
3. Click **Import** (top left) and select the file `zenith26-api.postman_collection.json` located in your `zenith26-fest-system/` root.
4. The collection variable `{{baseUrl}}` defaults to `http://localhost:8081`.

---

## 2. Test Execution Checklist (18 Scenarios)

### Folder 1: Events Endpoints

#### Request 1.1: List All Events
* **Method & URL:** `GET http://localhost:8081/api/events`
* **Expected Status:** `200 OK`
* **Verification:** An array of 14 event objects. Each event contains `available_seats` equal to `capacity` (when zero registrations exist).

#### Request 1.2: Filter by Category (Technical)
* **Method & URL:** `GET http://localhost:8081/api/events?category=Technical`
* **Expected Status:** `200 OK`
* **Verification:** Returns only events where `category` is "Technical" (e.g. Code Sprint 2.0, Robo Race, AI Pitch Fest, Web Design Rumble).

#### Request 1.3: Search by Title Query
* **Method & URL:** `GET http://localhost:8081/api/events?search=code`
* **Expected Status:** `200 OK`
* **Verification:** Returns matching events (e.g. "Code Sprint 2.0" and "Intro to Vibe Coding").

#### Request 1.4: Combined Filter and Search
* **Method & URL:** `GET http://localhost:8081/api/events?category=Workshop&search=vibe`
* **Expected Status:** `200 OK`
* **Verification:** Returns exactly 1 event: "Intro to Vibe Coding".

#### Request 1.5: Get Existing Event Detail
* **Method & URL:** `GET http://localhost:8081/api/events/1`
* **Expected Status:** `200 OK`
* **Verification:** Returns single object for "Code Sprint 2.0".

#### Request 1.6: Get Non-Existent Event (404 Check)
* **Method & URL:** `GET http://localhost:8081/api/events/9999`
* **Expected Status:** `404 Not Found`
* **Expected Body:**
  ```json
  {
    "detail": "Event with id 9999 not found"
  }
  ```

---

### Folder 2: Registration Endpoints & Validation Disciplines

#### Request 2.1: Valid Registration (Success 201)
* **Method & URL:** `POST http://localhost:8081/api/events/1/register`
* **Headers:** `Content-Type: application/json`
* **Body:**
  ```json
  {
    "student_name": "Aarav Sharma",
    "roll_number": "2024BCSE042",
    "email": "aarav.sharma@college.edu",
    "phone": "+91 9876543210"
  }
  ```
* **Expected Status:** `201 Created`
* **Expected Response:**
  ```json
  {
    "id": 1,
    "event_id": 1,
    "student_name": "Aarav Sharma",
    "registered_at": "..."
  }
  ```

#### Request 2.2: Verify Seat Count Decrement
* **Method & URL:** `GET http://localhost:8081/api/events/1`
* **Expected Status:** `200 OK`
* **Verification:** `available_seats` is now `99` (down from 100).

#### Request 2.3: Duplicate Registration (409 Conflict)
* **Method & URL:** `POST http://localhost:8081/api/events/1/register`
* **Body:** Same email `aarav.sharma@college.edu` for event 1.
* **Expected Status:** `409 Conflict`
* **Expected Response:**
  ```json
  {
    "detail": "Already registered for this event"
  }
  ```

#### Request 2.4: Missing Student Name (400 Bad Request)
* **Method & URL:** `POST http://localhost:8081/api/events/1/register`
* **Body:**
  ```json
  {
    "student_name": "",
    "roll_number": "2024BCSE042",
    "email": "test@college.edu"
  }
  ```
* **Expected Status:** `400 Bad Request`
* **Expected Response:**
  ```json
  {
    "detail": "Student name, roll number, and email are required"
  }
  ```

#### Request 2.5: Invalid Email Format (400 Bad Request)
* **Method & URL:** `POST http://localhost:8081/api/events/1/register`
* **Body:**
  ```json
  {
    "student_name": "Neha Gupta",
    "roll_number": "2024BCSE099",
    "email": "invalid-email-address"
  }
  ```
* **Expected Status:** `400 Bad Request`
* **Expected Response:**
  ```json
  {
    "detail": "Invalid email address: must contain '@' and a valid domain"
  }
  ```

#### Request 2.6: Register for Non-Existent Event (404 Not Found)
* **Method & URL:** `POST http://localhost:8081/api/events/9999/register`
* **Body:** Any valid JSON.
* **Expected Status:** `404 Not Found`

---

### Folder 3: Admin & Registrations Inspection

#### Request 3.1: List All Registrations
* **Method & URL:** `GET http://localhost:8081/api/registrations`
* **Expected Status:** `200 OK`
* **Verification:** Array containing the registration record with `event_title: "Code Sprint 2.0"`.

#### Request 3.2: Filter Registrations by Event
* **Method & URL:** `GET http://localhost:8081/api/registrations?event_id=1`
* **Expected Status:** `200 OK`

#### Request 3.3: Live Fest Statistics
* **Method & URL:** `GET http://localhost:8081/api/stats`
* **Expected Status:** `200 OK`
* **Verification:**
  - `total_events`: 14
  - `total_registrations`: at least 1
  - `overall_utilization_pct`: computed live (`total_registrations / total_capacity * 100`)
  - `events_by_category`: dict containing counts for Technical, Cultural, etc.

---

### Folder 4: Stretch Goals

#### Request 4.1: Create New Event (POST /api/events)
* **Method & URL:** `POST http://localhost:8081/api/events`
* **Body:**
  ```json
  {
    "title": "Autonomous Drone Racing",
    "category": "Technical",
    "description": "High-speed obstacle course competition.",
    "event_date": "2026-11-15",
    "event_time": "03:30 PM",
    "venue": "Campus Grounds",
    "capacity": 50,
    "fee": 100,
    "icon": "🛸"
  }
  ```
* **Expected Status:** `201 Created`

#### Request 4.2: Cancel Registration (DELETE /api/registrations/{id})
* **Method & URL:** `DELETE http://localhost:8081/api/registrations/1`
* **Expected Status:** `200 OK`
* **Verification:** Event 1's `available_seats` immediately restores back to `100`.
