---
title: "Session 04 — APIs & Integrations"
day: 4
module: 1
tags:
  - pm2
  - apis
  - integrations
  - rest
  - graphql
  - webhooks
  - authentication
topics:
  - API fundamentals and analogy
  - 5 parts of an API call
  - API error codes cheat sheet
  - Authentication (API keys, OAuth, JWT)
  - REST API principles
  - GraphQL introduction
  - Webhooks vs polling
  - Webhook safety rules
  - Real-world API stories
  - India context (Razorpay, ONDC, Aadhaar)
date: YYYY-MM-DD
---

# Session 04 — APIs & Integrations

> **Core Question:** Your product does not exist in isolation. It talks to payment gateways, maps, analytics, authentication providers, and partner systems. How do these conversations work, and what can go wrong? #pm-implication

---

## What Is an API?

### The Restaurant Analogy

An **API** (Application Programming Interface) is the waiter in a restaurant: #definition

| Restaurant | Technology |
|---|---|
| **You** (the customer) | Your application (the client) |
| **The menu** | API documentation (what you can request) |
| **Your order** | API request (what you are asking for) |
| **The waiter** | The API itself (carries your request, brings the response) |
| **The kitchen** | The server / backend (processes the request, prepares the response) |
| **Your food** | The API response (the data you receive) |

You never walk into the kitchen. You never see how the food is prepared. You interact only through the waiter (API), using the menu (documentation) to make valid requests.

### Why APIs Matter for PMs

APIs are the **connective tissue** of modern products. Consider what a single Swiggy order involves: #india-context

1. **Swiggy's own API** — Fetches restaurant menus, manages cart, places order
2. **Razorpay/Juspay API** — Processes the payment
3. **Google Maps API** — Shows restaurant location, calculates delivery route and ETA
4. **Firebase/APNS API** — Sends push notifications ("Your order is being prepared")
5. **SMS gateway API (MSG91/Twilio)** — Sends OTP for verification
6. **Restaurant POS API** — Pushes the order to the restaurant's system

A PM who does not understand APIs cannot effectively manage integrations, evaluate partners, or debug issues that span system boundaries.

---

## The 5 Parts of an API Call

Every API call has five essential components. Learn these and you can read any API documentation. #framework

### 1. Endpoint (URL)

The **address** where the API lives. It follows a predictable pattern:

```
https://api.razorpay.com/v1/payments
  |        |              |    |
  |        |              |    └── Resource (what you want)
  |        |              └── Version (v1, v2, etc.)
  |        └── Domain (whose API)
  └── Protocol (always HTTPS for production APIs)
```

**Common URL patterns:**
- `GET /v1/orders` — List all orders
- `GET /v1/orders/ord_12345` — Get a specific order
- `POST /v1/orders` — Create a new order
- `PUT /v1/orders/ord_12345` — Update an order
- `DELETE /v1/orders/ord_12345` — Cancel an order

### 2. HTTP Method (Verb)

The **action** you want to perform. See [[HTTP-Protocol]] for full details.

| Method | Purpose | Analogy | Idempotent? |
|---|---|---|---|
| **GET** | Read / retrieve data | Looking at the menu | Yes (same request = same result) |
| **POST** | Create a new resource | Placing a new order | No (each call creates a new resource) |
| **PUT** | Replace an entire resource | Rewriting your entire order | Yes |
| **PATCH** | Update part of a resource | Changing one item in your order | Yes |
| **DELETE** | Remove a resource | Cancelling your order | Yes |

**Idempotent** means calling it multiple times has the same effect as calling it once. This matters for PMs because: if a payment POST request times out and retries, you could charge the user twice. This is why payment APIs use **idempotency keys**. #pm-implication

### 3. Authentication Header

**Proves who you are** to the API. Sent in the HTTP headers (metadata that accompanies the request). See [[API-Authentication]] for the deep dive.

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
```

or

```
Authorization: Basic cnpwX3Rlc3RfaWQ6cnpwX3Rlc3Rfc2VjcmV0
```

### 4. Request Body

The **data** you are sending. Used with POST, PUT, and PATCH requests. Typically formatted as JSON:

```json
{
  "amount": 50000,
  "currency": "INR",
  "receipt": "order_receipt_42",
  "notes": {
    "customer_name": "Priya Sharma",
    "delivery_address": "Koramangala, Bangalore"
  }
}
```

### 5. Response

What the server sends back. Includes:
- **Status code** (200, 201, 400, 500, etc.)
- **Response headers** (metadata about the response)
- **Response body** (the actual data, usually JSON)

```json
{
  "id": "order_DBJOWzybf0sJbb",
  "entity": "order",
  "amount": 50000,
  "currency": "INR",
  "status": "created",
  "created_at": 1672492800
}
```

### The Complete API Request-Response Flow

```mermaid
sequenceDiagram
    participant Client as Your App<br>(Client)
    participant Auth as Auth Layer
    participant API as API Server
    participant DB as Database

    Client->>API: POST /v1/orders<br>Headers: Authorization: Bearer xxx<br>Body: { amount: 50000, currency: "INR" }
    API->>Auth: Validate token
    Auth-->>API: Token valid, user: merchant_123

    API->>API: Validate request body<br>(amount > 0, currency valid)
    
    alt Validation fails
        API-->>Client: 400 Bad Request<br>{ "error": "amount must be positive" }
    else Validation passes
        API->>DB: INSERT INTO orders (...)
        DB-->>API: Order created: order_DBJOWzybf0sJbb
        API-->>Client: 201 Created<br>{ "id": "order_DBJOWzybf0sJbb", "status": "created" }
    end
```

---

## API Error Codes Cheat Sheet

Every PM should have this table bookmarked. When something breaks in an integration, the error code tells you where to start looking. #framework

| Code | Name | What It Means | PM Action |
|---|---|---|---|
| **200** | OK | Request succeeded | Normal operation |
| **201** | Created | Resource successfully created (e.g., order placed) | Normal for POST requests |
| **204** | No Content | Success, but nothing to return (e.g., after DELETE) | Normal for DELETE requests |
| **301** | Moved Permanently | The API endpoint URL has changed permanently | Update your integration to the new URL |
| **400** | Bad Request | The request data is malformed or invalid | Frontend bug — check what data your app is sending |
| **401** | Unauthorized | Authentication failed (bad/expired/missing token) | Check API keys; JWT may have expired. See [[JWT-Authentication]] |
| **403** | Forbidden | Authenticated but not authorized for this action | Check user permissions or API plan tier |
| **404** | Not Found | The requested resource does not exist | Check the URL; the resource may have been deleted |
| **409** | Conflict | Request conflicts with current state (e.g., duplicate) | Idempotency issue; likely a retry created a duplicate |
| **422** | Unprocessable Entity | Data is well-formed but semantically invalid | Business logic error (e.g., insufficient balance) |
| **429** | Too Many Requests | Rate limit exceeded | You are calling the API too frequently; implement backoff or upgrade plan |
| **500** | Internal Server Error | The API server crashed | Not your fault; contact the API provider. Monitor for recurrence |
| **502** | Bad Gateway | Upstream server returned an invalid response | Infrastructure issue at the API provider |
| **503** | Service Unavailable | Server is temporarily overloaded or under maintenance | Implement retry with exponential backoff |

### The PM's Error Triage Framework

```mermaid
flowchart TD
    A[API call failed] --> B{What is the<br>status code?}
    B -->|4xx| C[Client error<br>Something WE did wrong]
    B -->|5xx| D[Server error<br>Something THEY did wrong]
    B -->|No response| E[Network error<br>Connection failed]
    
    C -->|400| F[Check request body<br>and parameters]
    C -->|401| G[Check API key<br>or token expiry]
    C -->|403| H[Check permissions<br>or plan tier]
    C -->|404| I[Check URL and<br>resource existence]
    C -->|429| J[Implement rate limiting<br>and backoff]
    
    D -->|500| K[Log and alert<br>Contact API provider]
    D -->|502/503| L[Retry with exponential<br>backoff. Check status page]
    
    E --> M[Check network,<br>DNS, firewall,<br>SSL certificates]

    style C fill:#FFB347,stroke:#CC8400,color:#000
    style D fill:#E74C3C,stroke:#C0392B,color:#fff
    style E fill:#9B59B6,stroke:#7D3C98,color:#fff
```

---

## API Authentication: Proving Who You Are

[[API-Authentication]] is how APIs verify the identity of the caller. There are three main approaches, each with different security profiles. #framework

### 1. API Keys

The simplest form. A unique string (like a password) assigned to your application. #definition

```
GET /v1/weather?city=mumbai
Headers:
  X-API-Key: sk_live_abcdef123456789
```

**Pros:** Simple to implement and use
**Cons:** If leaked, anyone can use it. No user-level granularity. Cannot be scoped to specific permissions easily.

**Real-world example:** Google Maps API key — one key per project, billed per usage. #case-study

### 2. OAuth 2.0

The industry standard for **delegated authorization**. It lets users grant your app access to their data on another service without sharing their password. #definition

**The analogy:** You give a valet a special key that can only drive your car (not open the trunk or glove box). You can revoke this key at any time.

**The OAuth dance (simplified):**

```mermaid
sequenceDiagram
    participant User
    participant YourApp as Your App
    participant Google as Google (OAuth Provider)

    User->>YourApp: "Sign in with Google"
    YourApp->>Google: Redirect user to Google login
    Google->>User: "YourApp wants access to your email and profile. Allow?"
    User->>Google: "Yes, I allow"
    Google->>YourApp: Authorization code
    YourApp->>Google: Exchange code for access token (server-to-server)
    Google-->>YourApp: Access token + refresh token
    YourApp->>Google: GET /userinfo (with access token)
    Google-->>YourApp: { name: "Priya", email: "priya@gmail.com" }
```

**Key OAuth terms:**
- **Authorization Code** — Short-lived code exchanged for tokens (most secure flow)
- **Access Token** — Short-lived token (15 min to 1 hour) used to make API calls
- **Refresh Token** — Long-lived token used to get new access tokens without re-login
- **Scopes** — Permissions the app requests (e.g., `read:email`, `write:calendar`)

**PM relevance:** When your product adds "Sign in with Google" or "Connect your Slack workspace," you are implementing OAuth. Key decisions: which scopes to request (ask for minimal), how to handle token expiry, and what happens when a user revokes access. #pm-implication

### 3. JWT (JSON Web Tokens)

[[JWT-Authentication]] tokens are self-contained — they carry the user's identity and permissions within the token itself, digitally signed to prevent tampering. #definition

**Structure:**
```
eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoxMjMsInJvbGUiOiJhZG1pbiJ9.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
  [Header]                 [Payload]                                         [Signature]
```

The payload (middle part) decodes to:
```json
{
  "user_id": 123,
  "role": "admin",
  "exp": 1672531200
}
```

**Pros:** Stateless (no server lookup needed), works across microservices, scales well
**Cons:** Cannot be revoked before expiry (unless you maintain a blacklist), token theft grants full access

### Authentication Method Comparison

| Method | Security | Complexity | Use Case |
|---|---|---|---|
| **API Key** | Low-Medium | Simple | Server-to-server calls, public APIs with rate limits |
| **OAuth 2.0** | High | Complex | User-facing integrations, third-party access |
| **JWT** | Medium-High | Medium | Authenticated sessions, microservice-to-microservice |

---

## REST API Principles

[[REST-API]] (Representational State Transfer) is the dominant architectural style for APIs. We introduced the six constraints in Session 03. Here we focus on practical design principles. #framework

### REST Design Best Practices

1. **Use nouns, not verbs in URLs**
   - Good: `GET /api/v1/products`
   - Bad: `GET /api/v1/getProducts`

2. **Use plural resource names**
   - Good: `/api/v1/orders/42`
   - Bad: `/api/v1/order/42`

3. **Version your APIs**
   - Good: `/api/v1/products` (version in URL)
   - Also good: `Accept: application/vnd.myapp.v1+json` (version in header)
   - Why: Lets you evolve the API without breaking existing clients

4. **Use query parameters for filtering, sorting, and pagination**
   - `GET /api/v1/products?category=electronics&sort=price&page=2&limit=20`

5. **Use proper HTTP status codes** (see cheat sheet above)

6. **Return consistent error response format**
   ```json
   {
     "error": {
       "code": "INSUFFICIENT_BALANCE",
       "message": "Account balance is below the required amount",
       "details": { "required": 5000, "available": 3200 }
     }
   }
   ```

7. **Support pagination for list endpoints**
   ```json
   {
     "data": [...],
     "pagination": {
       "page": 2,
       "per_page": 20,
       "total": 156,
       "total_pages": 8
     }
   }
   ```

#pm-implication — When reviewing API designs with your engineering team, check for these patterns. A well-designed API reduces integration bugs, improves developer experience for partners, and is easier to evolve as the product grows.

---

## Introduction to GraphQL

[[GraphQL]] is an alternative to REST, created by Facebook in 2015. Instead of multiple endpoints that return fixed data structures, GraphQL has **one endpoint** where clients specify exactly what data they need. #definition

### REST vs GraphQL — The Problem

**With REST**, getting a user's profile with their recent orders requires multiple calls:
1. `GET /api/v1/users/42` — Returns user profile (including fields you do not need)
2. `GET /api/v1/users/42/orders?limit=5` — Returns recent orders (including fields you do not need)

**Problems:**
- **Over-fetching** — Each endpoint returns all fields, even if you only need two
- **Under-fetching** — You need multiple requests to assemble one view
- **N+1 problem** — Fetching a list, then fetching details for each item

**With GraphQL**, one request gets exactly what you need:
```graphql
query {
  user(id: 42) {
    name
    email
    recentOrders(limit: 5) {
      id
      total
      status
    }
  }
}
```

### When to Choose GraphQL vs REST

| Factor | REST | GraphQL |
|---|---|---|
| **Simplicity** | Simpler to learn and implement | More complex, requires schema definition |
| **Mobile optimization** | Multiple calls, more bandwidth | Single call, minimal data transfer |
| **Caching** | HTTP caching works naturally (GET requests) | Complex (POST-based, needs custom caching) |
| **File uploads** | Straightforward (multipart/form-data) | Requires workarounds |
| **Real-time** | Requires WebSockets separately | Built-in subscriptions |
| **Tooling** | Postman, curl, any HTTP client | Apollo Client, Relay, GraphiQL |
| **API evolution** | Versioning required (v1, v2) | No versioning needed; add fields without breaking |
| **Error handling** | HTTP status codes | Always returns 200; errors in response body |

**PM Decision:** Use REST when your APIs are straightforward CRUD operations with predictable data needs. Consider GraphQL when you have a mobile-first product with complex, nested data relationships and bandwidth matters (especially for Tier 2/3 India where connections are slower). #pm-implication #india-context

> **Case Study — Shopify's GraphQL Adoption:** Shopify moved their Admin API to GraphQL because merchants needed to fetch deeply nested data (products with variants, each variant with images, each image with alt text). With REST, a single storefront page required 5-8 API calls. With GraphQL, it became one. For their Storefront API (public-facing, simpler data), they maintain REST alongside GraphQL. #case-study

---

## Webhooks vs Polling: Push vs Pull

### The Problem: Knowing When Something Changes

Your app integrates with Razorpay for payments. After a user initiates a payment, you need to know when it succeeds or fails. There are two approaches: #framework

### Polling (Pull Model)

Your app **repeatedly asks** the API: "Did the payment complete yet?" #definition

```
Your App: "Is payment done?" → Razorpay: "No"
Your App: "Is payment done?" → Razorpay: "No"
Your App: "Is payment done?" → Razorpay: "No"
Your App: "Is payment done?" → Razorpay: "Yes! Paid."
```

**Problems:**
- Wastes resources (most requests return "no change")
- Adds latency (you only learn about the change on your next poll)
- Can hit rate limits if you poll too frequently

### Webhooks (Push Model)

You give Razorpay a URL. When the payment completes, **Razorpay calls your URL** with the event data. #definition

```
[Payment completes]
Razorpay → Your App: POST /webhooks/razorpay
Body: { "event": "payment.captured", "payment_id": "pay_abc", "amount": 50000 }
```

### Webhook vs Polling Comparison

```mermaid
sequenceDiagram
    participant App as Your App
    participant API as Razorpay API

    rect rgb(255, 220, 220)
        Note over App,API: Polling Approach (Pull)
        loop Every 5 seconds
            App->>API: GET /payments/pay_abc/status
            API-->>App: { status: "processing" }
        end
        App->>API: GET /payments/pay_abc/status
        API-->>App: { status: "captured" }
        Note over App: Finally learned about payment!<br>Wasted 5+ API calls
    end

    rect rgb(220, 255, 220)
        Note over App,API: Webhook Approach (Push)
        Note over App: Waiting (no API calls)
        API->>App: POST /webhooks/razorpay<br>{ event: "payment.captured" }
        App-->>API: 200 OK (acknowledged)
        Note over App: Instantly notified!<br>Zero wasted calls
    end
```

### The 3 Webhook Safety Rules

When building webhook receivers, follow these three non-negotiable rules: #framework

#### Rule 1: Verify the Signature

Every legitimate webhook provider signs their payloads with a secret key. **Always verify the signature** before processing the webhook. Without this, anyone can send fake events to your webhook URL.

```
// Razorpay sends a signature in the header
X-Razorpay-Signature: sha256=abcdef123456...

// Your server must verify it using your webhook secret
expected = HMAC-SHA256(webhook_secret, request_body)
if received_signature != expected_signature:
    reject with 401
```

#### Rule 2: Handle Idempotently

Webhook providers may send the same event **multiple times** (network retry, provider-side retry on timeout). Your handler must be idempotent — processing the same event twice should not create duplicate side effects.

**Bad:** Crediting a user's wallet every time you receive a `payment.captured` event
**Good:** Checking if this `payment_id` was already processed before crediting

#### Rule 3: Respond Quickly, Process Asynchronously

Return a `200 OK` immediately upon receiving the webhook. Do the heavy processing (updating the database, sending emails, triggering workflows) asynchronously in a background job.

**Why:** Most webhook providers have a timeout (e.g., 30 seconds). If you do not respond in time, they assume delivery failed and retry — potentially causing duplicate processing (see Rule 2).

#pm-implication — When integrating with any partner API that uses webhooks, ask your engineering team: "Are we verifying signatures, handling duplicates, and processing asynchronously?" These three checks prevent the most common webhook bugs.

---

## Real Stories: APIs in the Wild

### Stripe API — The Gold Standard #case-study

Stripe's API is widely considered the best-designed API in the industry. Key lessons:
- **Versioning by date** (`Stripe-Version: 2023-10-16`) — Each API version is frozen; breaking changes go in new versions
- **Idempotency keys** — Every POST request accepts an `Idempotency-Key` header to prevent duplicate charges
- **Comprehensive documentation** — Interactive examples, copy-paste code, clear error messages
- **Generous rate limits** with clear 429 responses and `Retry-After` headers

**PM Lesson:** When evaluating API partners, compare their docs to Stripe's. The quality of API documentation reflects the quality of the engineering organization.

### Google Maps Pricing Change (2018) #case-study

In 2018, Google increased Maps API pricing by up to **1,400%** for some use cases and required all API keys to be linked to a billing account. Startups that built their entire location experience on Google Maps faced sudden, massive cost increases.

**PM Lesson:** API pricing is a business risk. Before building a deep dependency on any API:
1. Understand the pricing model (per-call, tiered, flat)
2. Estimate costs at 10x your current volume
3. Evaluate alternatives (e.g., OpenStreetMap, Mapbox)
4. Build an abstraction layer so you can switch providers

### Twitter/X API Changes (2023) #case-study

Twitter (now X) eliminated free API access in February 2023, replacing it with paid tiers starting at $100/month. Thousands of bots, research tools, and integrations built over a decade broke overnight.

**PM Lesson:** Free APIs are not guaranteed to stay free. Any critical integration should have a contingency plan, and your product should degrade gracefully if an API becomes unavailable or prohibitively expensive.

---

## India Context: APIs Powering the Indian Digital Stack

### Razorpay API #india-context

[[Razorpay]] is India's most popular payment gateway for startups. Key PM-relevant API features:
- **Payment Links API** — Create shareable payment links without a full checkout integration
- **Subscriptions API** — Manage recurring payments with automatic retry for failed charges
- **Route API** — Split payments between marketplace sellers and the platform
- **Webhook events** — `payment.captured`, `payment.failed`, `refund.processed`, `subscription.charged`

**PM consideration:** Razorpay charges ~2% per transaction. At scale, this is a significant cost. Evaluate Razorpay vs. Cashfree vs. Juspay based on your transaction volume and use case.

### ONDC (Open Network for Digital Commerce) #india-context

ONDC is India's government-backed open protocol for e-commerce — an "API layer for all of Indian commerce." Instead of platforms like Amazon or Flipkart owning the entire experience, ONDC separates:
- **Buyer Apps** (e.g., Paytm, Magicpin) — where users browse and buy
- **Seller Apps** (e.g., Mystore, GoFrugal) — where merchants list products
- **Gateway** — ONDC's central registry that connects buyer and seller apps

**PM relevance:** ONDC means any buyer app can access any seller. A PM building in the ONDC ecosystem must design for:
- Interoperability with multiple seller platforms
- Standardized API contracts (Beckn protocol)
- Variable response times (different sellers have different tech capabilities)

### Aadhaar eKYC API #india-context

The Aadhaar system provides APIs for digital identity verification:
- **eKYC API** — Verify identity using Aadhaar number + OTP or biometrics
- **eSign API** — Digital signatures using Aadhaar authentication
- **DigiLocker API** — Access government-issued documents digitally

**PM consideration:** Aadhaar APIs are regulated by UIDAI with strict guidelines on data storage, consent, and usage. A PM building fintech or lending products in India must understand:
- **Consent requirements** — Users must explicitly consent to identity verification
- **Data minimization** — Store only what you need, encrypt at rest
- **Audit requirements** — Every API call must be logged for regulatory compliance

---

## PM Framework: 5 Questions Before Integrating a Partner API

Before committing to any API integration, PMs should ask these five questions: #framework #pm-implication

### 1. What happens when this API goes down?

- Does your product show an error page, or does it degrade gracefully?
- Do you have a fallback (cached data, alternative provider)?
- What is the SLA (Service Level Agreement) for uptime?

### 2. What does this cost at 10x our current scale?

- Is pricing per-call, per-user, per-feature, or tiered?
- Are there overage charges or automatic throttling?
- Is there a committed-use discount for volume?

### 3. Can we switch providers if we need to?

- Is there an abstraction layer between our code and the API?
- Are we using provider-specific features that lock us in?
- How much effort would a migration require?

### 4. What are the rate limits and how do we handle them?

- What is the rate limit (requests per second/minute)?
- What happens when we hit it (429 error, queuing, degradation)?
- Can we get higher limits with a paid plan?

### 5. Who owns the data and what are the privacy implications?

- What data does the API provider see and store?
- Is data processed in India or abroad? (Relevant for data localization requirements) #india-context
- What happens to our data if we stop using the service?
- Does the integration require user consent under DPDP Act 2023?

> **Exercise:** Apply these 5 questions to a real integration. For example: your product uses Google Maps for location search. Walk through each question and identify the risks and mitigation strategies.

---

## Key Takeaways for Day 4

1. APIs are the waiter between systems — learn the 5 parts of every API call (endpoint, method, auth, body, response)
2. HTTP status codes are diagnostic: 4xx means your side broke something, 5xx means their side broke
3. [[API-Authentication]] has three levels: API keys (simple), OAuth (delegated access), [[JWT-Authentication]] (stateless sessions)
4. [[REST-API]] is the dominant style; [[GraphQL]] is the alternative when data needs are complex and bandwidth-sensitive
5. [[Webhooks]] (push) are almost always better than polling (pull) for event-driven integrations
6. The 3 webhook safety rules: verify signatures, handle idempotently, respond fast and process async
7. API pricing, uptime, and vendor lock-in are business risks PMs must evaluate before integrating
8. India's digital stack (Razorpay, ONDC, Aadhaar eKYC) is API-first — understanding these APIs is essential for PMs building in India

---

## Further Reading

- [[REST-API]] — RESTful design principles and best practices
- [[GraphQL]] — Query language for APIs
- [[Webhooks]] — Event-driven integrations
- [[API-Authentication]] — API keys, OAuth, JWT compared
- [[JWT-Authentication]] — JSON Web Tokens deep dive
- [[HTTP-Protocol]] — HTTP methods, headers, and status codes
- [[Razorpay]] — India's payment gateway ecosystem
- [[ONDC]] — Open Network for Digital Commerce
- [[Aadhaar-eKYC]] — Digital identity verification in India
