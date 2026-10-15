# Zenith '26 — College Fest Management System

[![Live Demo](https://img.shields.io/badge/Live%20Demo-GitHub%20Pages-cyan?style=for-the-badge&logo=github)](https://purohitkhushi8852-oss.github.io/HSBFestTest1/)
[![Download APK](https://img.shields.io/badge/Download-Android%20APK-emerald?style=for-the-badge&logo=android)](https://github.com/purohitkhushi8852-oss/HSBFestTest1/raw/main/Zenith26.apk)
[![FastAPI Backend](https://img.shields.io/badge/Backend-FastAPI%208081-violet?style=for-the-badge&logo=fastapi)](http://127.0.0.1:8081/docs)

> 🌐 **Live Web Portal**: [https://purohitkhushi8852-oss.github.io/HSBFestTest1/](https://purohitkhushi8852-oss.github.io/HSBFestTest1/)  
> 📊 **Admin Dashboard**: [https://purohitkhushi8852-oss.github.io/HSBFestTest1/admin.html](https://purohitkhushi8852-oss.github.io/HSBFestTest1/admin.html)  
> 📱 **Download Native Android APK (1.47 MB)**: [Zenith26.apk](https://github.com/purohitkhushi8852-oss/HSBFestTest1/raw/main/Zenith26.apk)

A high-performance College Fest Event Management System featuring a **FastAPI backend**, an **SQLite database**, a **responsive Web Portal & Admin Dashboard**, and a **pixel-perfect Native Android Mobile Application built with Kotlin & Jetpack Compose**.

---

## 🏛️ System Architecture

```text
┌─────────────────────────────────┐       ┌─────────────────────────────────┐
│     Android Kotlin Client       │       │       Web Frontend Client       │
│  (Jetpack Compose Native App)   │       │   (HTML5 + Tailwind CSS + JS)   │
└────────────────┬────────────────┘       └────────────────┬────────────────┘
                 │                                         │
                 │ HTTP / REST API                         │ HTTP / REST API
                 │ (10.0.2.2:8081 / LAN IP)                │ (localhost:8081)
                 ▼                                         ▼
         ┌─────────────────────────────────────────────────────────┐
         │                  FastAPI Backend Server                 │
         │             (Python 3.12 + Uvicorn + Pydantic)          │
         └────────────────────────────┬────────────────────────────┘
                                      │
                                      │ SQL Queries (Connection Pool)
                                      ▼
                         ┌─────────────────────────┐
                         │     SQLite Database     │
                         │       (`fest.db`)       │
                         └─────────────────────────┘
```

The system preserves strict architectural separation:
- **Android Kotlin App**: Native client consuming REST endpoints, with MVVM pattern, Retrofit HTTP client, Coroutines, StateFlow, and Jetpack Compose UI reproducing the exact web interface.
- **FastAPI Backend**: Serves API endpoints, enforces validation ("Never Trust The Client"), maintains concurrency safeguards, handles seat allocation, and serves the web frontend.
- **SQLite Database**: Relational schema tracking events and registrations with transactional integrity.

---

## 📁 Project Structure

```text
zenith26-fest-system/
│
├── backend/
│   ├── main.py              # FastAPI endpoints, static file mounting, lifespan handler
│   ├── database.py          # SQLite connection manager, schema initialization
│   ├── models.py            # Pydantic schemas (Event, Registration, Stats)
│   └── seed_data.py         # Seed loader importing events from Excel / fallback dict
│
├── frontend/
│   ├── index.html           # Web Student Portal (Dark theme, cyan/violet accents)
│   ├── admin.html           # Web Admin Dashboard (Metrics, capacity tables)
│   └── js/
│       ├── app.js           # Student portal logic, search, category filter, modal
│       └── admin.js         # Admin dashboard logic, charts, utilization alerts
│
├── android/
│   └── Zenith26/            # Native Kotlin Android Project
│       ├── app/
│       │   ├── src/
│       │   │   ├── main/
│       │   │   │   ├── java/com/zenith26/fest/
│       │   │   │   │   ├── MainActivity.kt               # Entry activity & navigation
│       │   │   │   │   ├── data/
│       │   │   │   │   │   ├── api/                      # Retrofit client, ApiService, ApiConfig
│       │   │   │   │   │   ├── model/                    # Kotlin data models (Event, Registration, Stats)
│       │   │   │   │   │   └── repository/               # ZenithRepository (Coroutines & IO dispatcher)
│       │   │   │   │   ├── ui/
│       │   │   │   │   │   ├── components/               # Navbar, Hero, EventCard, DetailModal, Stats, Tables
│       │   │   │   │   │   ├── screens/                  # StudentPortalScreen, AdminDashboardScreen
│       │   │   │   │   │   ├── theme/                    # Color.kt, Type.kt, Theme.kt (Web-matching tokens)
│       │   │   │   │   │   └── viewmodel/                # StudentViewModel, AdminViewModel
│       │   │   │   └── AndroidManifest.xml               # Network permissions & application config
│       │   │   └── test/                                 # Unit test suite (ZenithUnitTest.kt)
│       │   └── build.gradle.kts                          # Compose, Retrofit, Coroutines dependencies
│       ├── gradle/wrapper/                               # Gradle 8.7 wrapper
│       ├── build.gradle.kts                              # AGP 8.4.2 & Kotlin 1.9.24
│       └── settings.gradle.kts                           # Repositories & project include
│
├── data/
│   └── events_seed.xlsx     # 14 curated campus events across 5 categories
│
├── fest.db                  # SQLite database file
├── requirements.txt         # Python dependencies
├── zenith26-api.postman_collection.json # 18-request automated Postman collection
├── Postman-Testing-Commands-Zenith26.md # Testing guide
└── README.md
```

---

## ⚡ Quick Start: Running the Backend

### 1. Prerequisites
- Python 3.10+ (Python 3.12 recommended)
- Virtual environment with dependencies installed:
  ```powershell
  cd "zenith26-fest-system"
  python -m venv .venv
  .\.venv\Scripts\pip install -r requirements.txt
  ```

### 2. Start the FastAPI Server
Run on `0.0.0.0` so both the web browser and Android devices/emulators can connect:
```powershell
.\.venv\Scripts\python.exe -m uvicorn backend.main:app --host 0.0.0.0 --port 8081 --reload
```

The server automatically initializes `fest.db` and seeds the database from `data/events_seed.xlsx`.

### 3. Verify Endpoints in Browser
- **Student Portal:** [http://localhost:8081/](http://localhost:8081/)
- **Admin Dashboard:** [http://localhost:8081/admin.html](http://localhost:8081/admin.html)
- **Interactive Swagger Docs:** [http://localhost:8081/docs](http://localhost:8081/docs)
- **ReDoc API Documentation:** [http://localhost:8081/redoc](http://localhost:8081/redoc)

---

## 📱 Building & Running the Android App

### 1. Android Prerequisites
- **JDK 17** (e.g. Microsoft OpenJDK 17 or Eclipse Temurin 17)
- **Android SDK Platform 34** and **Build-Tools 34.0.0**
- Configured in `android/Zenith26/local.properties`:
  ```properties
  sdk.dir=C\:\\Users\\<username>\\AppData\\Local\\Android\\Sdk
  ```

### 2. Run Android Unit Tests
```powershell
cd android\Zenith26
.\gradlew.bat testDebugUnitTest
```

### 3. Assemble Debug APK
```powershell
cd android\Zenith26
.\gradlew.bat assembleDebug
```
The output APK is generated at:
```text
android/Zenith26/app/build/outputs/apk/debug/app-debug.apk
```

### 4. Install & Launch on Device or Emulator
```powershell
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.zenith26.fest/.MainActivity
```

---

## 🌐 Android Backend Connectivity & Network Configuration

Android emulators and physical devices access host services over distinct network interfaces:

| Client Type | Base URL Setting | Explanation |
|---|---|---|
| **Android Official Emulator** | `http://10.0.2.2:8081/` | `10.0.2.2` maps directly to the host machine's loopback interface (`127.0.0.1`). *(Default setting)* |
| **Physical Android Device** | `http://192.168.x.x:8081/` | Use your host machine's local Wi-Fi IP address. Ensure device and PC share the same Wi-Fi network. |
| **ADB Reverse Tunnel** | `http://127.0.0.1:8081/` | Run `adb reverse tcp:8081 tcp:8081` to route loopback traffic directly through USB. |

### In-App URL Switcher
The Android app includes a **Server Settings** modal accessible directly from the top navigation bar (gear/server icon). You can view, test, and switch the active backend URL on the fly without recompilation or reinstallation.

### Cleartext Traffic
Cleartext HTTP is enabled via `res/xml/network_security_config.xml` with explicit permissions for `10.0.2.2`, `127.0.0.1`, and private LAN subnets (`192.168.0.0/16`, `10.0.0.0/8`).

---

## 🎨 UI Fidelity & Parity Matrix

The Android native implementation reproduces the exact styling, color tokens, and components of the Tailwind CSS web application:

| Web Component / Style | CSS Token / Class | Android Jetpack Compose Equivalent |
|---|---|---|
| **Background** | `#080D1A` / slate-950 | `Surface(color = DarkBackground)` (`0xFF080D1A`) |
| **Card Surface** | `#0F172A` / slate-900 border slate-800 | `Surface(color = SurfaceCard, border = BorderSubtle)` |
| **Primary Gradient** | `bg-gradient-to-r from-cyan-400 to-violet-500` | `Brush.horizontalGradient(listOf(Cyan400, Violet500))` |
| **Accent Glow** | Electric Cyan `#06B6D4`, Violet `#8B5CF6`, Fuchsia `#D946EF` | `CyanAccent`, `VioletAccent`, `FuchsiaAccent` |
| **Category Badges** | `Technical` (Cyan), `Cultural` (Fuchsia), `Sports` (Amber), `Gaming` (Emerald), `Workshop` (Indigo) | Matching pill badges with 15% opacity fills and matching text colors |
| **Live Pulsing Badge** | Tailwind `animate-ping` + green dot | Jetpack Compose `infiniteTransition` pulsing indicator with `Alpha` animation |
| **Event Card** | Grid card with icon, title, datetime, fee, seats progress, "View Details" button | `EventCard` composable with seat progress bar, capacity count, and full state |
| **Event Full State** | Grayed out badge "FULL" + red warning text | `EventCard` displays `FULL (0 left)` with disabled CTA state |
| **Event Detail Modal** | Dialog overlay with full description, metadata grid, registration form | `EventDetailModal` rendered using `Dialog` with form validation |
| **Validation Rules** | Name required, roll number required, email format check | Real-time inline field validation + server-side 400/409 error propagation |
| **Admin Stats** | 4-card grid: Total Events, Registrations, Capacity Utilization %, Upcoming Events | `AdminStatsViews` with color-coded metric cards and overall utilization bar |
| **Utilization Alert** | Events with $\ge 90\%$ utilization highlighted with red badge and warning pulse | Capacity table applies `ErrorRed` warning badge for events over 90% |
| **Registration Management** | Admin table listing attendee details with event filter and cancellation | `AdminTablesViews` with dynamic event filter and cancel confirmation dialog |

---

## 📡 REST API Reference

| Method | Endpoint | Description | Status Codes |
|---|---|---|---|
| `GET` | `/api/events` | List all events (supports `?category=...` & `?search=...`) | `200 OK` |
| `GET` | `/api/events/{id}` | Detailed metadata for a specific event | `200 OK`, `404 Not Found` |
| `POST` | `/api/events/{id}/register` | Register a student for an event | `201 Created`, `400 Bad Request`, `409 Conflict` |
| `GET` | `/api/registrations` | List attendee registrations (supports `?event_id=...`) | `200 OK` |
| `GET` | `/api/stats` | Aggregated fest metrics and category breakdown | `200 OK` |
| `POST` | `/api/events` | *(Stretch Goal)* Create a new event | `201 Created`, `400 Bad Request` |
| `DELETE` | `/api/registrations/{id}` | *(Stretch Goal)* Cancel registration & restore seat | `200 OK`, `404 Not Found` |

---

## 🧪 Postman & Automated Verification

### Postman Test Suite
An automated collection containing 18 comprehensive tests is included at `zenith26-api.postman_collection.json`.

1. Import `zenith26-api.postman_collection.json` into Postman Desktop.
2. Select or set `{{baseUrl}}` to `http://localhost:8081`.
3. Run the collection runner — **18/18 tests pass**, verifying:
   - Event listing, category filtering, search, and combined filtering.
   - Event details and 404 responses.
   - Successful 201 registration and real-time seat decrement.
   - Duplicate registration prevention (409 Conflict).
   - Input validation for empty names and malformed emails (400 Bad Request).
   - Stats calculations, utilization percentages, and category groupings.
   - Event creation and registration cancellation (seat restoration).

---

## 👥 Contributors & Acknowledgements
- **Zenith '26 Technical Committee**
- Built for College Fest Management & Real-World Software Engineering Case Study.
