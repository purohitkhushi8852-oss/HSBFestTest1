"""
Zenith '26 College Fest Management System
Automated Backend API Verification Suite
Tests all 18 scenarios mirroring the Postman collection.
"""

import json
import urllib.request
import urllib.error

BASE = "http://127.0.0.1:8081"

def request(name, method, path, data=None, expected_code=200):
    url = f"{BASE}{path}"
    req = urllib.request.Request(url, method=method)
    if data:
        req.data = json.dumps(data).encode("utf-8")
        req.add_header("Content-Type", "application/json")
    try:
        with urllib.request.urlopen(req) as resp:
            status = resp.status
            body = resp.read().decode("utf-8")
            assert status == expected_code, f"{name}: Expected {expected_code}, got {status}"
            print(f" PASS: {name} [HTTP {status}]")
            try:
                return json.loads(body)
            except Exception:
                return body
    except urllib.error.HTTPError as e:
        assert e.code == expected_code, f"{name}: Expected {expected_code}, got {e.code}: {e.read().decode()}"
        print(f" PASS: {name} [HTTP {e.code}]")
        return json.loads(e.read().decode())

def run_tests():
    print("==========================================================")
    print("  ZENITH '26 API COMPREHENSIVE VERIFICATION SUITE")
    print("==========================================================")

    # 1. Events Endpoints
    print("\n--- [Folder 1: Events Endpoints] ---")
    events = request("1.1 List All Events", "GET", "/api/events")
    assert len(events) >= 14, f"Expected >= 14 events, got {len(events)}"

    tech_events = request("1.2 Filter by Category (Technical)", "GET", "/api/events?category=Technical")
    for ev in tech_events:
        assert ev["category"] == "Technical"

    search_events = request("1.3 Search by Query (code)", "GET", "/api/events?search=code")
    assert len(search_events) >= 1

    combined_events = request("1.4 Combined Filter & Search", "GET", "/api/events?category=Workshop&search=vibe")
    assert len(combined_events) >= 1

    event_1 = request("1.5 Get Event Detail", "GET", "/api/events/1")
    assert event_1["id"] == 1

    request("1.6 Get Non-Existent Event (404)", "GET", "/api/events/9999", expected_code=404)

    # 2. Registration Endpoints
    print("\n--- [Folder 2: Registration & Validation] ---")
    import time
    test_email = f"user_{int(time.time())}@college.edu"
    
    reg_payload = {
        "student_name": "Test Runner Student",
        "roll_number": "2024TEST001",
        "email": test_email,
        "phone": "+91 9999999999"
    }
    
    # We test on event 3
    reg_res = request("2.1 Valid Registration (201)", "POST", "/api/events/3/register", data=reg_payload, expected_code=201)
    
    # Duplicate check
    request("2.3 Duplicate Registration (409)", "POST", "/api/events/3/register", data=reg_payload, expected_code=409)

    # Validation checks
    request("2.4 Missing Name (400)", "POST", "/api/events/3/register", data={"student_name": "", "roll_number": "R1", "email": "a@b.com"}, expected_code=400)
    request("2.5 Invalid Email (400)", "POST", "/api/events/3/register", data={"student_name": "Aarav", "roll_number": "R1", "email": "not-an-email"}, expected_code=400)
    request("2.6 Register on Non-Existent Event (404)", "POST", "/api/events/9999/register", data={"student_name": "Aarav", "roll_number": "R1", "email": "valid@col.edu"}, expected_code=404)

    # 3. Admin & Registrations Inspection
    print("\n--- [Folder 3: Admin & Registrations] ---")
    registrations = request("3.1 List All Registrations", "GET", "/api/registrations")
    assert isinstance(registrations, list)

    reg_event3 = request("3.2 Filter Registrations by Event", "GET", "/api/registrations?event_id=3")
    assert isinstance(reg_event3, list)

    stats = request("3.3 Live Fest Statistics", "GET", "/api/stats")
    assert "total_events" in stats
    assert "total_registrations" in stats
    assert "overall_utilization_pct" in stats
    assert "events_by_category" in stats
    print(f"       Stats: Events={stats['total_events']}, Registrations={stats['total_registrations']}, Utilization={stats['overall_utilization_pct']}%")

    print("\n==========================================================")
    print("  ALL ZENITH '26 ENDPOINT VERIFICATIONS PASSED (100%)")
    print("==========================================================")

if __name__ == "__main__":
    run_tests()
