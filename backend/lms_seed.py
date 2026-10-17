import sqlite3
import json
from pathlib import Path
from backend.database import get_db_connection

def init_lms_tables():
    conn = get_db_connection()
    cursor = conn.cursor()

    # 1. User Settings & Profile
    cursor.execute("""
    CREATE TABLE IF NOT EXISTS lms_settings (
        id INTEGER PRIMARY KEY DEFAULT 1,
        user_name TEXT NOT NULL DEFAULT 'Khushi',
        avatar_url TEXT,
        theme TEXT NOT NULL DEFAULT 'dark',
        streak_pct INTEGER DEFAULT 10,
        streak_days INTEGER DEFAULT 4,
        total_studied INTEGER DEFAULT 18,
        mcqs_attempted INTEGER DEFAULT 42,
        mcqs_correct INTEGER DEFAULT 38
    )
    """)

    # 2. Activity / Session Log (Times of day)
    cursor.execute("""
    CREATE TABLE IF NOT EXISTS lms_activity_log (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        action TEXT NOT NULL,
        details TEXT,
        timestamp TEXT NOT NULL,
        time_of_day TEXT NOT NULL
    )
    """)

    # 3. Tools & Applications
    cursor.execute("""
    CREATE TABLE IF NOT EXISTS lms_tools (
        id TEXT PRIMARY KEY,
        name TEXT NOT NULL,
        category TEXT NOT NULL,
        icon TEXT NOT NULL,
        badge_color TEXT NOT NULL,
        completed_pct INTEGER DEFAULT 0,
        hands_on INTEGER DEFAULT 1,
        note_title TEXT,
        note_path TEXT,
        is_custom INTEGER DEFAULT 0,
        display_order INTEGER DEFAULT 0
    )
    """)

    # 4. Step-by-Step Topics (1, 2, 3, 4... Basics to Advanced)
    cursor.execute("""
    CREATE TABLE IF NOT EXISTS lms_tool_topics (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        tool_id TEXT NOT NULL REFERENCES lms_tools(id) ON DELETE CASCADE,
        step_number INTEGER NOT NULL,
        title TEXT NOT NULL,
        level TEXT NOT NULL, -- 'Basics', 'Intermediate', 'Advanced'
        summary TEXT,
        note_path TEXT,
        status TEXT DEFAULT 'pending'
    )
    """)

    # 5. Daily Classes
    cursor.execute("""
    CREATE TABLE IF NOT EXISTS lms_daily_classes (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        day_number INTEGER NOT NULL,
        day_tag TEXT NOT NULL,
        title TEXT NOT NULL,
        subtitle TEXT,
        note_path TEXT,
        icon TEXT DEFAULT '🚀',
        is_active INTEGER DEFAULT 0
    )
    """)

    # 6. Study Labs Questions Bank (MCQs)
    cursor.execute("""
    CREATE TABLE IF NOT EXISTS lms_study_questions (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        tool_id TEXT NOT NULL,
        topic_title TEXT,
        question TEXT NOT NULL,
        options_json TEXT NOT NULL,
        correct_index INTEGER NOT NULL,
        explanation TEXT NOT NULL
    )
    """)

    # 7. Hands-on Tasks (Distinct from MCQs)
    cursor.execute("""
    CREATE TABLE IF NOT EXISTS lms_tasks (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        tool_id TEXT NOT NULL,
        title TEXT NOT NULL,
        description TEXT NOT NULL,
        steps_json TEXT NOT NULL,
        difficulty TEXT NOT NULL,
        xp INTEGER DEFAULT 100
    )
    """)

    # 8. Knowledge Web Links (Weights and Reasons)
    cursor.execute("""
    CREATE TABLE IF NOT EXISTS lms_knowledge_links (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        source_id TEXT NOT NULL,
        target_id TEXT NOT NULL,
        strength TEXT NOT NULL, -- 'High', 'Medium', 'Strong'
        weight INTEGER DEFAULT 80,
        why_linked TEXT NOT NULL,
        data_flow TEXT NOT NULL
    )
    """)

    conn.commit()
    conn.close()
    print("LMS tables created successfully.")

def seed_lms_data():
    conn = get_db_connection()
    cursor = conn.cursor()

    vault_base = Path(r"D:\o.vault\temp_unzip\PM2-Obsidian-Vault - Copy")
    def find_note(name: str) -> str:
        candidates = list(vault_base.rglob(f"*{name}*"))
        return str(candidates[0]) if candidates else ""

    day4_note = find_note("Session-04-APIs-Integrations.md")
    sqlite_note = find_note("Session-07-Databases-Analytics.md") or find_note("SQL-Basics.md")
    claud_note = find_note("Cloud-Computing.md") or find_note("Client-Server-Architecture.md")
    pomeli_note = find_note("Tracking-Plan.md") or find_note("Decision-Trees.md")
    gravity_note = find_note("Build-vs-Buy.md") or find_note("Technical-Debt.md")
    obsidian_note = find_note("Home.md") or find_note("MOC-Architecture.md")

    # 1. Seed Settings (Default Khushi)
    cursor.execute("SELECT COUNT(*) as count FROM lms_settings WHERE id = 1")
    if cursor.fetchone()["count"] == 0:
        cursor.execute("""
        INSERT INTO lms_settings (id, user_name, avatar_url, theme, streak_pct, streak_days, total_studied, mcqs_attempted, mcqs_correct)
        VALUES (1, 'Khushi', 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=100&auto=format&fit=crop&q=80', 'dark', 10, 4, 18, 42, 38)
        """)

    # 2. Seed Activity Logs (Times of day)
    cursor.execute("DELETE FROM lms_activity_log")
    logs = [
        ("Morning Study Session", "Completed Anti-Gravity Step 1: Agentic Foundations", "2026-10-17 09:15 AM", "Morning (09:15 AM)"),
        ("Afternoon Hands-On Lab", "Executed Postman Day 4 API Testing Sandbox", "2026-10-17 01:45 PM", "Afternoon (01:45 PM)"),
        ("Evening Concept Exploration", "Reviewed Claud Design Architecture Specs & State Contracts", "2026-10-17 05:30 PM", "Evening (05:30 PM)"),
        ("Night MCQ Practice", "Practiced 15 SQLite Query and Normalization Questions", "2026-10-17 08:15 PM", "Night (08:15 PM)")
    ]
    for action, details, ts, tod in logs:
        cursor.execute("INSERT INTO lms_activity_log (action, details, timestamp, time_of_day) VALUES (?, ?, ?, ?)", (action, details, ts, tod))

    # 3. Seed Tools (Anti-Gravity, Postman, Obsidian, Pomeli AI, Claud Design, SQLite)
    tools = [
        ("anti-gravity", "Anti-Gravity", "Prioritization", "delta", "cyan", 70, 1, "Anti-Gravity & Prioritization Frameworks", gravity_note, 0, 1),
        ("postman", "Postman", "API Testing", "rocket", "orange", 0, 1, "Postman & API Testing Automation", day4_note, 0, 2),
        ("obsidian", "Obsidian", "Knowledge Management", "gem", "purple", 0, 1, "Obsidian Vault & Knowledge Management", obsidian_note, 0, 3),
        ("pomeli-ai", "Pomeli AI", "Product Research", "f-logo", "purple", 0, 1, "Pomeli AI Copilot & Qualitative Research", pomeli_note, 0, 4),
        ("claud-design", "Claud Design", "System Arch.", "cloud", "blue", 0, 1, "Claud Design & System Architecture", claud_note, 0, 5),
        ("sqlite", "SQLite", "Data Analytics", "database", "blue", 0, 1, "SQLite Foundations & SQL Queries", sqlite_note, 0, 6)
    ]
    cursor.execute("DELETE FROM lms_tools WHERE is_custom = 0")
    for t in tools:
        cursor.execute("""
        INSERT OR REPLACE INTO lms_tools (id, name, category, icon, badge_color, completed_pct, hands_on, note_title, note_path, is_custom, display_order)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """, t)

    # 4. Seed Line-Wise Topics (1, 2, 3, 4... Basics to Advanced)
    cursor.execute("DELETE FROM lms_tool_topics")
    topics_data = {
        "anti-gravity": [
            (1, "Introduction to Anti-Gravity & Agentic Foundations", "Basics", "Core autonomous workflow principles, tool registration, and pairing models.", gravity_note, "completed"),
            (2, "Workspace Initialization & Toolset Discovery", "Basics", "Navigating local repository bounds, filesystem reads, and command sandboxing.", gravity_note, "completed"),
            (3, "Task Delegation & Subagent Orchestration", "Intermediate", "Spawning targeted research agents, message passing, and lifecycle hooks.", gravity_note, "completed"),
            (4, "Dual-Loop Prioritization (Eisenhower x Value-Effort)", "Intermediate", "Balancing immediate blockers with long-term architectural stability.", gravity_note, "completed"),
            (5, "Managing Background Commands & Reactive Messaging", "Intermediate", "Asynchronous task listeners, logs monitoring, and non-blocking timers.", gravity_note, "in-progress"),
            (6, "Custom Prompts, Rules & Context Compaction", "Advanced", "Controlling token usage, conversation memory pruning, and persistent rules.", gravity_note, "pending"),
            (7, "Advanced System Integration & Production Vibe Coding", "Advanced", "Full-stack agentic execution with CI/CD and production verification.", gravity_note, "pending")
        ],
        "postman": [
            (1, "Fundamentals of HTTP Requests (GET, POST, PUT, DELETE)", "Basics", "Anatomy of HTTP calls, methods, URI anatomy, and server communication.", day4_note, "completed"),
            (2, "Request Headers, Body Formats (JSON, Form-Data) & Query Params", "Basics", "Configuring Content-Type, query strings, and payload payloads.", day4_note, "completed"),
            (3, "Understanding Status Codes (2xx Success, 4xx Client, 5xx Server)", "Basics", "200, 201, 400, 401, 403, 404, 409, 500 error diagnosis cheat sheet.", day4_note, "in-progress"),
            (4, "Authentication Workflows (API Keys, Bearer Tokens, OAuth 2.0, JWT)", "Intermediate", "Securing API endpoints, token expiration, and secret storage.", day4_note, "pending"),
            (5, "Environment Variables & Dynamic Parameterization", "Intermediate", "Switching environments (Local, Staging, Production) without rewriting URLs.", day4_note, "pending"),
            (6, "Writing Pre-request Scripts & Test Assertions in JavaScript", "Intermediate", "Validating schema structures, JSON values, and response latency.", day4_note, "pending"),
            (7, "Collections, Folder Organization & Postman Runners", "Advanced", "Batch executing endpoint test suites with CSV/JSON test datasets.", day4_note, "pending"),
            (8, "Mock Servers & Automated Monitoring", "Advanced", "Simulating third-party APIs before backend completion.", day4_note, "pending"),
            (9, "CI/CD Pipeline Integration via Newman CLI", "Advanced", "Triggering automated API validation on every git push.", day4_note, "pending")
        ],
        "obsidian": [
            (1, "Vault Creation, Folder Structure & Markdown Foundations", "Basics", "Structuring local folders, headings, codeblocks, and lists.", obsidian_note, "completed"),
            (2, "Bi-directional Linking ([[Topic Name]]) & Aliases", "Basics", "Connecting concepts effortlessly with double brackets and alternative names.", obsidian_note, "completed"),
            (3, "Graph View Navigation & Local Graph Clustering", "Basics", "Visualizing knowledge density, orphan notes, and topic hubs.", obsidian_note, "completed"),
            (4, "YAML Frontmatter & Metadata Tags", "Intermediate", "Adding structured key-value attributes (tags, status, module, author).", obsidian_note, "in-progress"),
            (5, "Maps of Content (MOC) Architecture", "Intermediate", "Creating index hubs that aggregate related concept notes dynamically.", obsidian_note, "pending"),
            (6, "Canvas Visual Thinking & Architecture Flowcharts", "Intermediate", "Arranging notes, cards, and diagrams on an infinite 2D canvas.", obsidian_note, "pending"),
            (7, "Dataview Plugin for Dynamic SQL Queries", "Advanced", "Querying notes like a database with TABLE, LIST, and TASK syntax.", obsidian_note, "pending"),
            (8, "Mermaid.js Diagram Integration in Vault Notes", "Advanced", "Embedding sequence diagrams, state charts, and ER models inside markdown.", obsidian_note, "pending"),
            (9, "Vault Synchronization & Antigravity Indexing", "Advanced", "Linking local Obsidian notes directly into learning management systems.", obsidian_note, "pending")
        ],
        "pomeli-ai": [
            (1, "Getting Started with Pomeli AI for Product Research", "Basics", "Overview of AI-assisted user interview analysis and synthesis.", pomeli_note, "completed"),
            (2, "User Interview Synthesis & Qualitative Analysis", "Basics", "Extracting user pain points, quotes, and behavioral trends automatically.", pomeli_note, "completed"),
            (3, "Automated Feature Opportunity Mapping", "Intermediate", "Scoring feature requests against user value and friction metrics.", pomeli_note, "in-progress"),
            (4, "Competitive Intelligence & Market Signals", "Intermediate", "Analyzing competitor teardowns and identifying differentiation angles.", pomeli_note, "pending"),
            (5, "PRD & User Story Co-generation with Pomeli AI", "Advanced", "Transforming raw user feedback into structured product requirement docs.", pomeli_note, "pending"),
            (6, "Wireframe Validation & Usability Feedback Loops", "Advanced", "Simulating persona testing on proposed UX mockups.", pomeli_note, "pending")
        ],
        "claud-design": [
            (1, "Claud Design Fundamentals & Architecture Principles", "Basics", "Core principles of AI-driven system design, modularity, and clean boundaries.", claud_note, "completed"),
            (2, "Prompt Structuring for Clean Architectural Outputs", "Basics", "Designing precise prompts for generating robust, testable software specs.", claud_note, "completed"),
            (3, "System Decomposition & Interface Specifications", "Intermediate", "Dividing monolithic systems into decoupled microservices and event queues.", claud_note, "in-progress"),
            (4, "Event-Driven Architectural Blueprints", "Intermediate", "Designing asynchronous message brokers, publish-subscribe topologies, and webhooks.", claud_note, "pending"),
            (5, "Component Boundary Definition & State Contracts", "Advanced", "Establishing immutable API schemas, idempotent operations, and data contracts.", claud_note, "pending"),
            (6, "Failure Domain Isolation & High Availability Design", "Advanced", "Circuit breakers, graceful degradation, and distributed redundancy.", claud_note, "pending")
        ],
        "sqlite": [
            (1, "Relational Database Concepts & SQLite Architecture", "Basics", "Single-file serverless storage, ACID compliance, and concurrency model.", sqlite_note, "completed"),
            (2, "DDL: Tables, Data Types, Constraints & Primary Keys", "Basics", "INTEGER, TEXT, REAL, BLOB, NOT NULL, UNIQUE, and AUTOINCREMENT.", sqlite_note, "completed"),
            (3, "DML Operations: SELECT, INSERT, UPDATE, DELETE", "Basics", "CRUD operations, transaction safety, and parameter substitution.", sqlite_note, "completed"),
            (4, "Filtering, Sorting & Pagination (WHERE, LIKE, ORDER BY, LIMIT)", "Basics", "Writing precise queries with wildcard searches and offset pagination.", sqlite_note, "in-progress"),
            (5, "Aggregate Functions & GROUP BY Analysis", "Intermediate", "COUNT, SUM, AVG, MIN, MAX and grouping metrics with HAVING clauses.", sqlite_note, "pending"),
            (6, "Table Joins (INNER, LEFT, CROSS) & Relational Integrity", "Intermediate", "Foreign key constraints, multi-table relationships, and cascading deletes.", sqlite_note, "pending"),
            (7, "Database Normalization (1NF, 2NF, 3NF)", "Intermediate", "Eliminating redundancy, data anomalies, and separating concerns.", sqlite_note, "pending"),
            (8, "Indexes, Performance & EXPLAIN QUERY PLAN", "Advanced", "B-tree index optimization, query execution plans, and avoiding table scans.", sqlite_note, "pending"),
            (9, "Python SQLite Integration & Connection Pooling", "Advanced", "sqlite3 context managers, row_factory, and connection safety in FastAPI.", sqlite_note, "pending")
        ]
    }

    for tool_id, steps in topics_data.items():
        for step_num, title, level, summary, note_p, status in steps:
            cursor.execute("""
            INSERT INTO lms_tool_topics (tool_id, step_number, title, level, summary, note_path, status)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """, (tool_id, step_num, title, level, summary, note_p, status))

    # 5. Seed Daily Classes
    cursor.execute("DELETE FROM lms_daily_classes")
    daily_classes = [
        (1, "DAY 1", "DAY 1: Tech Landscape & Modern Stacks", "Foundations of modern cloud, client-server models, and technical architectures.", find_note("Session-01"), "🌐", 0),
        (2, "DAY 2", "DAY 2: Frontend & Backend Architecture", "Deep dive into rendering strategies (SSR, CSR, SSG) and API contracts.", find_note("Session-02"), "⚙️", 0),
        (3, "DAY 3", "DAY 3: State Management & Client Reliability", "Managing state complexity, caching layers, and client-side error states.", find_note("Session-03"), "🔄", 0),
        (4, "DAY 4", "DAY 4: API Testing with Postman", "Master request headers, status codes, JWT authentication, and automated integration pipelines.", day4_note, "🚀", 1),
        (5, "DAY 5", "DAY 5: Advanced APIs & GraphQL Topologies", "Webhooks, GraphQL schemas, rate limiting, and event-driven endpoints.", find_note("Session-05"), "⚡", 0),
        (6, "DAY 6", "DAY 6: Database Foundations & Data Modeling", "Relational modeling, ER diagrams, normalization, and ACID properties.", find_note("Session-06"), "💾", 0),
        (7, "DAY 7", "DAY 7: Database Analytics & SQLite Performance", "Complex queries, aggregation pipelines, analytics indexing, and query plans.", sqlite_note, "📊", 0)
    ]
    for dc in daily_classes:
        cursor.execute("""
        INSERT INTO lms_daily_classes (day_number, day_tag, title, subtitle, note_path, icon, is_active)
        VALUES (?, ?, ?, ?, ?, ?, ?)
        """, dc)

    # 6. Seed Knowledge Web Links with Link Weights & Why Linked
    cursor.execute("DELETE FROM lms_knowledge_links")
    links = [
        ("anti-gravity", "obsidian", "High", 95, "Anti-Gravity directly accesses and parses your local Obsidian vault notes to track study velocity and extract learning topics.", "Local filesystem read & bi-directional wikilink indexer."),
        ("anti-gravity", "pomeli-ai", "High", 90, "Prioritizes user pain points and feature backlogs synthesized by Pomeli AI using Eisenhower matrix and impact scores.", "Research insight feed -> Prioritization queue."),
        ("pomeli-ai", "claud-design", "High", 85, "Qualitative user research specs from Pomeli AI inform architectural decomposition and interface boundaries in Claud Design.", "PRD specs -> Architecture blueprint."),
        ("claud-design", "sqlite", "High", 85, "Architectural data contracts modeled in Claud Design are translated directly into normalized relational schemas and DDL in SQLite.", "Entity design -> Schema definitions."),
        ("postman", "sqlite", "Medium", 70, "Postman automated test runners execute against API endpoints that persist and query data in SQLite databases.", "API test suites -> Database assertions."),
        ("obsidian", "claud-design", "Medium", 75, "Markdown diagrams and Maps of Content (MOCs) in Obsidian serve as context groundings for Claud Design system prompt specs.", "Vault documentation -> System prompts."),
        ("sqlite", "obsidian", "Medium", 65, "SQLite indices store metadata, tags, and link relationships extracted from Obsidian notes for sub-millisecond graph queries.", "Graph cache -> Note retrieval.")
    ]
    for s_id, t_id, strength, weight, why, flow in links:
        cursor.execute("""
        INSERT INTO lms_knowledge_links (source_id, target_id, strength, weight, why_linked, data_flow)
        VALUES (?, ?, ?, ?, ?, ?)
        """, (s_id, t_id, strength, weight, why, flow))

    # 7. Seed 20 Comprehensive MCQs for Each Tool (Study Labs Question Bank)
    cursor.execute("DELETE FROM lms_study_questions")
    
    # Anti-Gravity Questions (20 questions)
    ag_q = [
        ("What is the primary role of Anti-Gravity in an agentic coding environment?", ["Autonomous task execution with developer-in-the-loop pairing", "A basic text editor with syntax highlighting", "A git cloud hosting provider", "A container orchestration cluster"], 0, "Anti-Gravity pairs with the developer to analyze codebases, run commands, and build systems with full context."),
        ("How does Anti-Gravity manage long-running terminal processes?", ["By hanging the UI until the process terminates", "Running them as background tasks with reactive notification hooks", "Killing all commands after 5 seconds", "Delegating them to an external web browser"], 1, "Long-running tasks execute in the background and notify the agent asynchronously upon completion."),
        ("What is the benefit of the dual-loop prioritization model in Anti-Gravity?", ["It minimizes battery usage on laptops", "It balances immediate task blockers with long-term architectural stability", "It converts all functions into Python lambdas", "It forces all tasks to be completed in alphabetical order"], 1, "Dual-loop prioritization ensures quick wins do not compromise fundamental system design."),
        ("When Anti-Gravity defines a subagent, what capability is isolated?", ["The subagent gets its own focused conversation context and specific tool permissions", "The subagent replaces the operating system kernel", "The subagent deletes all git branches", "The subagent cannot write code"], 0, "Subagents isolate context clutter while executing specialized research or coding jobs."),
        ("What is context compaction in agentic sessions?", ["Summarizing conversation history to preserve available token budget", "Compressing files into .zip archives on disk", "Minifying HTML and CSS files", "Deleting user instructions permanently"], 0, "Compaction keeps the active context relevant and below model token limits."),
        ("Why should an agent avoid busy-wait polling loops?", ["Polling wastes tokens and CPU; reactive wakeup resumes execution on event arrival", "Polling is blocked by the Windows firewall", "Loops are not supported in Python", "Polling crashes the SQLite database"], 0, "Reactive wakeups resume execution automatically when messages or task completions occur."),
        ("What tool is used in Anti-Gravity to edit files without replacing entire files?", ["replace_file_content", "format_drive", "delete_file", "git_rebase"], 0, "replace_file_content makes surgical contiguous diff edits without expensive full rewrites."),
        ("How does Anti-Gravity discover project dependencies?", ["By inspecting requirements.txt, package.json, or environment manifests", "By guessing randomly", "By prompting the user for every library", "By scanning the Windows registry"], 0, "Inspection of workspace manifests provides accurate dependency context."),
        ("What is an artifact in Anti-Gravity?", ["A persistent structured markdown document created in the conversation artifact directory", "A compiled binary executable", "A browser cookie", "A database backup file"], 0, "Artifacts present structured summaries, task plans, and walkthroughs clearly."),
        ("When should a slash command like /plan be recommended?", ["When a task is complex and requires careful architectural alignment before code changes", "When rebooting Windows", "When committing git tags", "When shutting down the monitor"], 0, "/plan guides the development of step-by-step roadmaps before implementation."),
        ("What role does the user play in Anti-Gravity pair programming?", ["The user provides requirements, validates progress, and steers design decisions", "The user is completely passive and cannot intervene", "The user must write all assembly code", "The user only manages billing"], 0, "Pair programming maintains user intent at the center of all actions."),
        ("Why does Anti-Gravity prefer read-only vault access by default?", ["To protect the user's primary notes from unintentional corruption or reorganization", "Because Obsidian does not support writes", "Because markdown is read-only", "To avoid generating git diffs"], 0, "Safe read-only vault access ensures the user's existing knowledge base is respected."),
        ("What is the purpose of Mermaid diagrams in Anti-Gravity lessons?", ["Rendering visual flowcharts, sequence diagrams, and architecture models inside markdown", "Encrypting database passwords", "Exporting notes to PDF", "Speeding up network latency"], 0, "Mermaid renders native, crisp architectural visuals from text-based diagrams."),
        ("How does Anti-Gravity resolve Obsidian wikilinks like [[Concept]]?", ["By matching note filenames and frontmatter aliases across vault directories", "By querying Google Search", "By creating new empty files on Desktop", "By generating HTML anchors only"], 0, "Wikilink resolution maps note titles and aliases to local file paths."),
        ("What is an idempotency check in background automation?", ["Ensuring repeated execution yields the same result without unintended duplicate side effects", "Running a script in an infinite loop", "Recompiling Python binaries", "Deleting logs every 5 minutes"], 0, "Idempotency prevents duplicate database records and corrupted state."),
        ("What is the primary indicator of task completion in Anti-Gravity?", ["Automated verification tests and visual UI confirmation", "Stopping when a timer expires", "Guessing based on file size", "Leaving tasks unverified"], 0, "Automated tests and browser verification confirm success."),
        ("How are scratch scripts used in Anti-Gravity?", ["Temporary test scripts stored in the conversation scratch directory to debug problems", "Permanent production libraries", "System drivers", "Browser extensions"], 0, "Scratch files allow experimentation without polluting user workspaces."),
        ("What does Hands-On Mode indicate in the Early Learn toolkit?", ["The tool includes an interactive simulated sandbox or terminal environment for active practice", "The note is strictly read-only text", "The user cannot edit anything", "The server is offline"], 0, "Hands-On Mode lets learners practice concepts interactively."),
        ("Why are clear file basenames preferred over full Windows paths in UI links?", ["Basenames improve readability and maintain responsive card styling", "Full paths are prohibited by HTML5", "Paths cannot be clicked", "Windows does not support slashes"], 0, "Basenames keep UI links clean and readable."),
        ("What is the overarching mission of Early Learn?", ["Transforming an Obsidian vault into a structured, interactive, interconnected learning system", "Building a generic video course marketplace", "Creating a simple text editor", "Replacing the operating system"], 0, "Early Learn turns your personal notes into an engaging, multi-modal LMS.")
    ]

    for q_text, options, c_idx, expl in ag_q:
        cursor.execute("""
        INSERT INTO lms_study_questions (tool_id, topic_title, question, options_json, correct_index, explanation)
        VALUES (?, ?, ?, ?, ?, ?)
        """, ("anti-gravity", "Anti-Gravity Fundamentals", q_text, json.dumps(options), c_idx, expl))

    # Postman Questions (20 questions)
    pm_q = [
        ("What does HTTP status code 201 Created signify?", ["A new resource was successfully created on the server", "The request failed validation", "The user is unauthorized", "The server timed out"], 0, "201 Created indicates successful creation of a new database or system resource."),
        ("Which HTTP method is designed to be idempotent for replacing a resource entirely?", ["PUT", "POST", "PATCH", "CONNECT"], 0, "PUT replaces the resource completely and calling it multiple times results in the same state."),
        ("What is the primary difference between PUT and PATCH?", ["PATCH updates partial fields, whereas PUT replaces the entire resource entity", "PATCH is only for deleting records", "PUT is non-idempotent", "There is no difference"], 0, "PATCH performs partial modifications; PUT replaces the target entity."),
        ("Where are query parameters appended in an HTTP request?", ["At the end of the URL path following a '?' symbol", "In the request Authorization header", "Inside the encrypted TLS certificate", "Inside the multipart file body"], 0, "Query parameters follow the '?' in the request URI (e.g. ?category=tech&page=2)."),
        ("What HTTP header specifies that the client expects JSON in the response?", ["Accept: application/json", "Content-Type: text/html", "Host: localhost", "Cache-Control: no-cache"], 0, "The Accept header informs the server which MIME types the client understands."),
        ("What does HTTP status code 401 Unauthorized mean?", ["Authentication credentials are missing, invalid, or expired", "The user lacks permission to access an authenticated resource", "The requested URL does not exist", "The database crashed"], 0, "401 means unauthenticated (identity not verified)."),
        ("How does HTTP 403 Forbidden differ from 401 Unauthorized?", ["403 means the identity is known but lacks required authorization permissions", "403 means server error", "403 means network disconnected", "There is no difference"], 0, "401 is authentication; 403 is authorization."),
        ("In JWT authentication, what are the three parts separated by dots?", ["Header, Payload, Signature", "Username, Password, Salt", "Domain, Port, Route", "Key, Value, Timestamp"], 0, "JWT consists of Header (algorithm), Payload (claims), and cryptographic Signature."),
        ("Why are environment variables used in Postman collections?", ["To dynamically switch base URLs and API keys across dev, staging, and production", "To slow down request times", "To encrypt user hard drives", "To replace JavaScript"], 0, "Environment variables enable seamless switching between environments without editing requests."),
        ("What JavaScript function in Postman tests checks the response status code?", ["pm.response.to.have.status(200)", "assert.status == 200", "http.check(200)", "verify.ok()"], 0, "Postman provides the pm.test and pm.response assertion library."),
        ("What is a pre-request script in Postman used for?", ["Generating dynamic timestamps, nonces, or signing request payloads before sending", "Rendering HTML in the browser", "Compiling C++ binaries", "Closing network sockets"], 0, "Pre-request scripts run before the request is dispatched to prepare headers or payloads."),
        ("What is a Postman Collection Runner?", ["A built-in tool that executes multiple requests in sequence with optional iteration data", "A database backup daemon", "A web browser plugin", "A load balancer"], 0, "Collection Runners automate test suites across parameter datasets."),
        ("What CLI tool allows running Postman collections inside continuous integration pipelines?", ["Newman", "Curl", "Wget", "Nginx"], 0, "Newman is Postman's official command-line collection runner."),
        ("What is an API Mock Server in Postman?", ["A simulated server that returns predefined response payloads without a live backend", "A fraudulent phishing proxy", "A database replica", "A DNS resolver"], 0, "Mock servers enable frontend development before backend endpoints are built."),
        ("What is the purpose of the Content-Type header in a POST request?", ["Informs the server how to parse the request body (e.g. application/json)", "Specifies the client's screen resolution", "Controls the browser cookies", "Sets the TCP window size"], 0, "Content-Type declares the MIME type of the payload."),
        ("What HTTP status code indicates a conflict, such as a duplicate unique email?", ["409 Conflict", "400 Bad Request", "404 Not Found", "502 Bad Gateway"], 0, "409 Conflict is returned when an insert violates a unique constraint."),
        ("What is rate limiting in modern REST APIs?", ["Restricting the number of requests a client can send within a specific time window", "Limiting the upload bandwidth to 56kbps", "Shutting down the server at midnight", "Charging money per byte"], 0, "Rate limiting prevents abuse and ensures service stability."),
        ("What is CORS (Cross-Origin Resource Sharing)?", ["A browser security mechanism that restricts requests made to a different domain/port", "A database replication protocol", "An encryption algorithm", "A CSS layout rule"], 0, "CORS regulates browser requests across different origins."),
        ("Which HTTP status code represents an internal unhandled server exception?", ["500 Internal Server Error", "404 Not Found", "301 Moved Permanently", "204 No Content"], 0, "500 denotes an unexpected failure on the server side."),
        ("What is the benefit of automated API regression testing in Postman?", ["Detecting breaking changes and contract violations before deploying to users", "Making the server run 10x faster", "Eliminating the need for databases", "Replacing frontend JavaScript"], 0, "Automated tests prevent regressions and maintain API reliability.")
    ]
    for q_text, options, c_idx, expl in pm_q:
        cursor.execute("""
        INSERT INTO lms_study_questions (tool_id, topic_title, question, options_json, correct_index, explanation)
        VALUES (?, ?, ?, ?, ?, ?)
        """, ("postman", "Postman & API Testing", q_text, json.dumps(options), c_idx, expl))

    # SQLite Questions (20 questions)
    sq_q = [
        ("What kind of architecture does SQLite use?", ["A serverless, self-contained, transactional database stored in a single disk file", "A multi-server distributed cluster with master-slave nodes", "An in-memory only cache with no disk persistence", "A cloud-only proprietary SaaS"], 0, "SQLite writes directly to a single regular disk file with zero server setup."),
        ("What does ACID stand for in database management?", ["Atomicity, Consistency, Isolation, Durability", "Accuracy, Control, Integration, Design", "Array, Class, Inheritance, Delegation", "Access, Cache, Index, Dispatch"], 0, "ACID properties guarantee reliable database transactions."),
        ("Which SQL clause is used to filter records based on specific conditions?", ["WHERE", "ORDER BY", "GROUP BY", "LIMIT"], 0, "The WHERE clause filters rows satisfying defined boolean predicates."),
        ("How do you find all records where a title starts with 'Session' in SQLite?", ["WHERE title LIKE 'Session%'", "WHERE title == Session*", "WHERE title IN ('Session')", "WHERE title CONTAINS 'Session'"], 0, "LIKE with '%' wildcard matches string prefixes in SQLite."),
        ("What does an INNER JOIN return?", ["Rows that have matching values in both joined tables", "All rows from the left table regardless of matches", "All rows from the right table regardless of matches", "A cartesian product without filters"], 0, "INNER JOIN selects records with matching keys in both tables."),
        ("How does a LEFT JOIN differ from an INNER JOIN?", ["LEFT JOIN keeps all rows from the left table and fills missing right columns with NULL", "LEFT JOIN deletes unmatching rows", "LEFT JOIN only works on primary keys", "There is no difference"], 0, "LEFT JOIN preserves all rows from the primary table."),
        ("What is the purpose of Database Normalization (1NF, 2NF, 3NF)?", ["To minimize data redundancy and eliminate insert/update/delete anomalies", "To compress image files", "To encrypt user passwords", "To speed up CSS rendering"], 0, "Normalization organizes columns and tables to ensure dependencies make sense."),
        ("What does Third Normal Form (3NF) require?", ["The table must be in 2NF and have no transitive dependencies on non-key columns", "Every column must be an integer", "All tables must be merged into one", "The database must use foreign keys only"], 0, "3NF requires all non-key fields to depend directly on the primary key alone."),
        ("What is a Foreign Key constraint in SQLite?", ["A field that links to the Primary Key of another table to maintain referential integrity", "A password used by remote users", "An external file path on disk", "A temporary variable in SQL"], 0, "Foreign keys ensure related records exist and maintain data integrity."),
        ("How do you enforce Foreign Key checks in an SQLite connection?", ["PRAGMA foreign_keys = ON;", "SET FOREIGN_KEYS = 1;", "ENABLE CONSTRAINTS;", "ALTER DATABASE STRICT;"], 0, "SQLite requires PRAGMA foreign_keys = ON per connection."),
        ("What is an SQLite index (B-tree) used for?", ["To dramatically speed up row lookups on frequently queried columns", "To format numbers with commas", "To backup data to the cloud", "To hide column names"], 0, "Indexes allow logarithmic search time rather than full table scans."),
        ("Which command displays the execution plan and index usage of a query in SQLite?", ["EXPLAIN QUERY PLAN SELECT ...", "DESCRIBE SELECT ...", "ANALYZE QUERY ...", "PROFILE SELECT ..."], 0, "EXPLAIN QUERY PLAN reveals whether an index or a SCAN is performed."),
        ("What is the purpose of aggregate functions like COUNT(), SUM(), AVG()?", ["Performing mathematical calculations across groups of rows", "Renaming database files", "Encrypting text strings", "Parsing JSON arrays"], 0, "Aggregates summarize multiple rows into a single scalar value."),
        ("What SQL clause filters grouped results produced by GROUP BY?", ["HAVING", "WHERE", "ORDER BY", "LIMIT"], 0, "HAVING filters aggregate values after GROUP BY is applied."),
        ("In SQLite, what happens when a column is defined as INTEGER PRIMARY KEY AUTOINCREMENT?", ["It automatically assigns a monotonic unique integer ID to each new inserted row", "It sets the column to random strings", "It converts the database to MySQL", "It makes the column read-only"], 0, "INTEGER PRIMARY KEY AUTOINCREMENT manages unique row identifiers."),
        ("What is SQL Injection, and how is it prevented in Python SQLite?", ["A security vulnerability prevented by using parameterized '?' placeholders instead of string formatting", "A performance boost", "A database migration tool", "A method for inserting images"], 0, "Parameterized queries prevent malicious SQL injection attacks."),
        ("What does cursor.fetchone() return in Python sqlite3?", ["A single matching row as a tuple or sqlite3.Row object, or None if empty", "All rows at once", "The total count of rows", "The database file size"], 0, "fetchone() retrieves the next row in the result set."),
        ("Why is conn.commit() necessary after INSERT or UPDATE in SQLite?", ["To persist and write transaction changes to disk", "To close the connection", "To clear the memory cache", "To delete temporary tables"], 0, "commit() finalizes and flushes transaction writes to storage."),
        ("What is connection pooling or single-thread connection safety in SQLite?", ["Managing shared database connections properly to avoid database lock contention", "Allowing 1000 simultaneous writes to a single file", "Disabling transactions", "Bypassing file permissions"], 0, "SQLite locks the file during writes, requiring clean connection management."),
        ("What is the file extension typically used for SQLite databases?", [".db, .sqlite, or .sqlite3", ".docx", ".xlsx", ".json"], 0, "SQLite files typically use .db, .sqlite, or .sqlite3.")
    ]
    for q_text, options, c_idx, expl in sq_q:
        cursor.execute("""
        INSERT INTO lms_study_questions (tool_id, topic_title, question, options_json, correct_index, explanation)
        VALUES (?, ?, ?, ?, ?, ?)
        """, ("sqlite", "SQLite & Relational Foundations", q_text, json.dumps(options), c_idx, expl))

    # Obsidian, Claud Design, Pomeli AI Questions
    extra_tools_q = [
        ("obsidian", "Obsidian Knowledge Management", "What syntax does Obsidian use for internal wikilinks?", ["[[Note Name]]", "{{Note Name}}", "((Note Name))", "<<Note Name>>"], 0, "[[Note Name]] creates bi-directional links between markdown files."),
        ("obsidian", "Obsidian Knowledge Management", "What is an MOC (Map of Content) in personal knowledge management?", ["An index note that curates and clusters related notes around a topic", "A GPS map embedded in Obsidian", "A compiled executable", "A database table"], 0, "MOCs structure ideas top-down and provide central navigation hubs."),
        ("obsidian", "Obsidian Knowledge Management", "Where is metadata defined at the top of an Obsidian markdown note?", ["In YAML frontmatter bounded by triple dashes (---)", "In HTML footer tags", "In the filename only", "In Windows file properties"], 0, "YAML frontmatter at the top of notes stores tags, aliases, and custom attributes."),
        ("obsidian", "Obsidian Knowledge Management", "How does the Obsidian Graph View assist conceptual understanding?", ["It reveals knowledge clusters, link bridges, and isolated orphan notes visually", "It checks for spelling errors", "It automatically writes code", "It measures CPU performance"], 0, "Graph view provides visual feedback on knowledge connectivity."),
        ("claud-design", "Claud Design & System Arch", "What is the core principle of Claud Design in system architecture?", ["Clear module boundaries, explicit contracts, and high availability design", "Monolithic coupling with no interfaces", "Writing everything in a single file", "Eliminating database constraints"], 0, "Claud Design focuses on modularity, predictable interfaces, and resilient fault domains."),
        ("claud-design", "Claud Design & System Arch", "Why are data contracts essential between microservices in Claud Design?", ["They ensure services can evolve independently without breaking API expectations", "They reduce internet bills", "They encrypt local cookies", "They eliminate backend servers"], 0, "Data contracts guarantee stability across distributed components."),
        ("claud-design", "Claud Design & System Arch", "What is a circuit breaker pattern in distributed systems?", ["A mechanism to prevent cascading failures by failing fast when a dependency is down", "An electrical switch on a server rack", "A database deletion trigger", "A CSS animation"], 0, "Circuit breakers protect upstream callers from hanging on failing services."),
        ("pomeli-ai", "Pomeli AI & Product Research", "What is Pomeli AI primarily used for in the product lifecycle?", ["Synthesizing user research, feedback loops, and opportunity mapping", "Compiling Linux kernels", "Designing graphic 3D video games", "Managing DNS servers"], 0, "Pomeli AI accelerates qualitative research synthesis and PRD drafting."),
        ("pomeli-ai", "Pomeli AI & Product Research", "How does Pomeli AI help product managers prioritize features?", ["By scoring qualitative pain points against impact and development feasibility", "By rolling dice randomly", "By sorting by shortest name", "By choosing the oldest feature"], 0, "Opportunity mapping correlates customer evidence with strategic business goals.")
    ]
    for t_id, top_title, q_text, options, c_idx, expl in extra_tools_q:
        cursor.execute("""
        INSERT INTO lms_study_questions (tool_id, topic_title, question, options_json, correct_index, explanation)
        VALUES (?, ?, ?, ?, ?, ?)
        """, (t_id, top_title, q_text, json.dumps(options), c_idx, expl))

    # 8. Seed Hands-on Tasks (Distinct from MCQs)
    cursor.execute("DELETE FROM lms_tasks")
    tasks_data = [
        ("anti-gravity", "Orchestrate a Subagent Pair Workflow", "Set up an autonomous pair programming task with background compilation.", json.dumps(["Initialize workspace context", "Spawn research subagent for API specification", "Review generated artifacts and verify"]), "Intermediate", 150),
        ("postman", "Build an Automated Collection Runner with Assertions", "Construct a 5-request authentication and data retrieval pipeline in Postman.", json.dumps(["Define environment variables for baseUrl and token", "Configure POST /authenticate with JSON body", "Write test script validating pm.response.code === 200", "Execute batch collection runner with Newman CLI"]), "Intermediate", 200),
        ("obsidian", "Construct a 10-Note Knowledge MOC Cluster", "Create bi-directional links and an architecture Map of Content.", json.dumps(["Create index MOC note", "Link 5 concept notes using [[wikilinks]]", "Embed a Mermaid sequence diagram", "Inspect local graph view clustering"]), "Basics", 120),
        ("claud-design", "Author a Resilient Microservice Data Contract", "Design an immutable JSON schema and error specification for an event bus.", json.dumps(["Define JSON schema definitions", "Document idempotent retry semantics", "Model failure state circuit breaker parameters"]), "Advanced", 250),
        ("pomeli-ai", "Synthesize 5 User Interviews into an Opportunity Tree", "Extract actionable product backlog items from raw qualitative transcripts.", json.dumps(["Import raw transcript notes", "Tag user friction points and frequency", "Generate top 3 PRD feature requirements"]), "Intermediate", 180),
        ("sqlite", "Design and Query a 3NF Relational Schema", "Write DDL, insert mock entities, and write multi-table JOIN analytics queries.", json.dumps(["Create students, courses, and enrollments tables with foreign keys", "Insert sample data across all tables", "Write an INNER JOIN query with GROUP BY and HAVING clauses", "Run EXPLAIN QUERY PLAN to verify index usage"]), "Basics", 150)
    ]
    for tool_id, title, desc, steps_j, diff, xp in tasks_data:
        cursor.execute("""
        INSERT INTO lms_tasks (tool_id, title, description, steps_json, difficulty, xp)
        VALUES (?, ?, ?, ?, ?, ?)
        """, (tool_id, title, desc, steps_j, diff, xp))

    conn.commit()
    conn.close()
    print("LMS database successfully seeded with all tools, line-wise topics, questions, tasks, and settings.")

if __name__ == "__main__":
    init_lms_tables()
    seed_lms_data()
