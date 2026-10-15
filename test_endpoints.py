import urllib.request
import urllib.error
import json

BASE = "http://127.0.0.1:8080"

def test(name, method, path, data=None, expected_code=200):
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
            print(f"PASS: {name} (Status {status})")
            try:
                return json.loads(body)
            except Exception:
                return body
    except urllib.error.HTTPError as e:
        assert e.code == expected_code, f"{name}: Expected {expected_code}, got {e.code}: {e.read().decode()}"
        print(f"PASS: {name} (Expected Status {e.code})")
        return json.loads(e.read().decode())


print("--- Running Automated LearnEarly LMS Verification ---")
test("Serve LearnEarly Homepage", "GET", "/")
test("Serve app.js", "GET", "/js/app.js")

topics = test("GET /api/topics", "GET", "/api/topics")
assert len(topics) >= 10, f"Expected at least 10 topics, got {len(topics)}"
print(f"Verified {len(topics)} PM topics loaded.")

api_topics = test("GET /api/topics?search=api", "GET", "/api/topics?search=api")
assert len(api_topics) >= 1

topic_1 = test("GET /api/topics/1", "GET", "/api/topics/1")
assert topic_1["id"] == 1
assert "API" in topic_1["abbreviation"]

test("GET /api/topics/9999 (404 check)", "GET", "/api/topics/9999", expected_code=404)

questions = test("GET /api/questions", "GET", "/api/questions")
assert len(questions) >= 10, f"Expected at least 10 questions, got {len(questions)}"
print(f"Verified {len(questions)} Physics Wallah practice questions loaded.")

# Test quiz submission
quiz_payload = {
    "student_name": "Aarav Sharma (MBA PM)",
    "answers": {
        1: "B",
        2: "C",
        3: "B",
        4: "C"
    }
}
quiz_res = test("POST /api/quiz/submit (201 check)", "POST", "/api/quiz/submit", data=quiz_payload, expected_code=201)
assert quiz_res["score"] == 4
assert quiz_res["total"] == 4
assert quiz_res["passed"] is True
print(f"Quiz evaluated: {quiz_res['score']}/{quiz_res['total']} ({quiz_res['percentage']}%) - Explanations returned: {len(quiz_res['results'])}")

# Validation checks
test("POST missing name (400 check)", "POST", "/api/quiz/submit", data={"student_name": "", "answers": {1: "A"}}, expected_code=400)
test("POST empty answers (400 check)", "POST", "/api/quiz/submit", data={"student_name": "Aarav", "answers": {}}, expected_code=400)

glossary = test("GET /api/glossary", "GET", "/api/glossary")
assert len(glossary) >= 10
print(f"Verified {len(glossary)} glossary abbreviations loaded.")

stats = test("GET /api/stats", "GET", "/api/stats")
assert stats["total_topics"] >= 10
assert stats["total_questions"] >= 10
print(f"LearnEarly Live Stats: Topics={stats['total_topics']}, Questions={stats['total_questions']}, Quiz Attempts={stats['total_quiz_attempts']}")

print("\n>>> ALL LEARNEARLY LMS TESTS PASSED WITH 100% SUCCESS! <<<")
