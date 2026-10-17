---
title: "MOC — APIs & Integrations"
tags:
  - MOC
  - api
  - integrations
---

# APIs & Integrations

This Map of Content covers how software systems communicate with each other. APIs (Application Programming Interfaces) are the contracts that let frontends talk to backends, services talk to services, and companies integrate with partners. For PMs, API design is a product decision — a well-designed API accelerates adoption; a poorly designed one creates friction and support burden.

---

## Session Notes

- [[Session-04-APIs-Integrations]] — API fundamentals, REST design principles, HTTP methods, status codes, Postman
- [[Session-05-APIs-Advanced]] — GraphQL, webhooks, authentication, API security, rate limiting

---

## Core Concepts

### [[REST-API]]
The dominant API style on the web. REST uses standard HTTP methods (GET, POST, PUT, DELETE) on resource-oriented URLs. It is simple, well-understood, and works for most use cases. The tradeoff: REST endpoints return fixed data shapes, which can lead to over-fetching or under-fetching.

### [[GraphQL]]
Facebook's alternative to REST that lets clients request exactly the data they need — no more, no less. A single endpoint serves flexible queries. GraphQL shines when different clients (web, mobile, IoT) need different data from the same backend, but adds query complexity and caching challenges.

### [[Webhooks]]
The "push" complement to REST's "pull" model. Instead of polling an API repeatedly to check for changes, webhooks let a service notify you when an event occurs. Stripe sends a webhook when a payment succeeds; GitHub sends one when a PR is merged. Essential for event-driven integrations.

### [[JWT-Authentication]]
JSON Web Tokens — the standard for stateless authentication in modern APIs. JWTs encode user identity and permissions in a signed token that the client sends with every request, eliminating the need for server-side session storage.

### [[API-Authentication]]
The broader landscape of API security: API keys, OAuth 2.0, JWT, mutual TLS. Each method balances security, complexity, and developer experience. Choosing the right authentication scheme is a product decision that affects adoption and partner integrations.

---

## API Selection Decision Guide

```
What problem are you solving?
    |
    +-- Standard CRUD operations?
    |       --> [[REST-API]] (simple, well-understood, cacheable)
    |
    +-- Multiple clients needing different data shapes?
    |       --> [[GraphQL]] (flexible queries, single endpoint)
    |
    +-- Need to notify external systems of events?
    |       --> [[Webhooks]] (push-based, real-time)
    |
    +-- Real-time bidirectional communication?
    |       --> WebSockets (chat, live updates, gaming)
    |
    +-- Internal microservices communication?
            --> gRPC (high performance, strongly typed)
```

---

## API Authentication Comparison

| Method | Security Level | Complexity | Best For |
|--------|---------------|------------|----------|
| **API Key** | Basic | Low | Public APIs, rate limiting |
| **OAuth 2.0** | High | High | Third-party access ("Login with Google") |
| **[[JWT-Authentication|JWT]]** | High | Medium | Stateless session management |
| **Mutual TLS** | Very High | Very High | Service-to-service, banking |

---

## Key PM Takeaways

1. **APIs are products** — If you expose a public API, it needs documentation, versioning, rate limits, and a developer experience strategy just like a user-facing product.
2. **REST is the safe default** — Use REST unless you have a specific reason for GraphQL or gRPC. Most teams know REST, and the tooling is mature.
3. **Webhooks reduce polling cost** — Instead of clients hitting your API every 5 seconds to check for updates, push the update to them when it happens.
4. **Authentication choice affects adoption** — OAuth 2.0 enables "Login with Google" but is complex. API keys are simple but less secure. Match the method to the use case.
5. **API versioning is non-negotiable** — Breaking changes without versioning destroy partner trust. Plan for v1, v2, v3 from the start.
