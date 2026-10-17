---
title: "MOC — Web Architecture & Networking"
tags:
  - MOC
  - architecture
  - networking
---

# Web Architecture & Networking

This Map of Content covers the foundational technology concepts that underpin every web product: how clients talk to servers, how requests travel the internet, and how content reaches users efficiently. These concepts form the bedrock of technical PM fluency — understanding them lets you have meaningful conversations with engineers about performance, scalability, and system design.

---

## Session Notes

- [[Session-01-Tech-Landscape]] — The technology landscape: why PMs need tech fluency, the full request lifecycle from browser to server and back

---

## Core Concepts

### [[Client-Server-Architecture]]
The fundamental model of the modern web. Every product your users interact with follows this pattern: a **client** (browser, mobile app) sends requests to a **server**, which processes them, queries a database, and returns responses. Understanding the three-tier architecture (presentation, application, data) helps PMs reason about where performance bottlenecks and security boundaries live.

### [[HTTP-Protocol]]
The language clients and servers speak. HTTP defines request methods (GET, POST, PUT, DELETE), status codes (200 OK, 404 Not Found, 500 Server Error), headers, and the request-response cycle. Every API call, page load, and form submission uses HTTP. Knowing status codes alone helps PMs read error logs and debug issues in conversations with engineers.

### [[DNS]]
The internet's phone book. When a user types "example.com," DNS translates that human-readable name into the IP address of the server that hosts it. DNS resolution adds latency to the first request, and DNS configuration affects uptime, security (DNSSEC), and global routing.

### [[CDN]]
Content Delivery Networks solve the physics problem of distance. By caching static assets (images, CSS, JavaScript) on servers distributed worldwide, CDNs reduce latency for users far from the origin server. CDNs also absorb traffic spikes and provide DDoS protection. Understanding CDN behavior helps PMs reason about page load times and global user experience.

---

## How These Concepts Connect

```
User types URL
    |
    v
[[DNS]] resolves domain --> IP address
    |
    v
[[HTTP-Protocol]] request sent
    |
    v
[[CDN]] serves cached static assets (if available)
    |
    v
[[Client-Server-Architecture]] processes dynamic requests
    |
    v
Response returned to browser
```

---

## Key PM Takeaways

1. **Every millisecond of latency matters** — Amazon found that every 100ms of latency cost them 1% of sales. DNS, CDN, and server architecture all contribute to total latency.
2. **The request lifecycle is your debugging map** — When something is slow or broken, trace the path: DNS? Network? Server? Database? Rendering?
3. **Architecture decisions constrain product decisions** — A client-server split determines what works offline, what needs real-time sync, and where business logic lives.
