---
title: "MOC — Frontend & Backend Development"
tags:
  - MOC
  - frontend
  - backend
  - development
---

# Frontend & Backend Development

This Map of Content covers how modern web applications are built on both the client side and the server side. Understanding frontend frameworks, rendering strategies, and backend languages helps PMs make informed decisions about user experience, performance, hiring, and technical architecture.

---

## Session Notes

- [[Session-02-Frontend-Backend]] — Frontend vs backend, rendering strategies, frontend frameworks, CSS frameworks
- [[Session-03-Frontend-Backend-Contd]] — Backend languages, state management, full-stack architecture patterns

---

## Frontend Concepts

### [[Frontend-Frameworks]]
The libraries and frameworks used to build user interfaces. The choice of frontend framework affects developer productivity, hiring pool, performance, and component reusability. React dominates the market, but Vue and Angular have strong niches.

### [[Rendering-Strategies]]
How and where HTML is generated: Client-Side Rendering (CSR), Server-Side Rendering (SSR), Static Site Generation (SSG), and Incremental Static Regeneration (ISR). Each strategy trades off between initial load speed, SEO, interactivity, and server cost.

### [[CSS-Frameworks]]
Tools for styling user interfaces consistently and efficiently. From utility-first (Tailwind CSS) to component libraries (Bootstrap, Material UI), CSS frameworks accelerate development and enforce design consistency.

### [[State-Management]]
How applications track and synchronize data across components. From local component state to global stores (Redux, Zustand), state management determines how responsive and predictable the user experience feels.

---

## Backend Concepts

### [[Backend-Languages]]
The server-side languages that power business logic, API endpoints, and data processing. Each language has tradeoffs in performance, ecosystem, hiring availability, and use case fit.

---

## Frontend Framework Comparison

| Framework | Language | Learning Curve | Ecosystem | Best For | Market Share |
|-----------|----------|---------------|-----------|----------|-------------|
| **React** | JavaScript/TypeScript | Moderate | Massive (Next.js, React Native) | SPAs, complex UIs, mobile apps | ~40% of web apps |
| **Vue** | JavaScript/TypeScript | Gentle | Growing (Nuxt.js) | Progressive enhancement, startups | ~18% |
| **Angular** | TypeScript (required) | Steep | Complete (built-in routing, forms) | Enterprise apps, large teams | ~17% |
| **Svelte** | JavaScript/TypeScript | Gentle | Young (SvelteKit) | Performance-critical, smaller apps | ~3% |

**PM decision guide:**
- **Default choice for most teams**: React (largest talent pool, richest ecosystem)
- **Enterprise with strong typing needs**: Angular (opinionated, batteries-included)
- **Small team, fast iteration**: Vue or Svelte (lower learning curve)
- **Mobile + web from one codebase**: React (via React Native)

---

## How These Concepts Connect

```
User Request
    |
    v
[[Rendering-Strategies]] decides where HTML is built
    |
    +---> CSR: [[Frontend-Frameworks]] render in browser
    +---> SSR: [[Backend-Languages]] render on server
    +---> SSG: Pre-built at deploy time
    |
    v
[[CSS-Frameworks]] style the UI
    |
    v
[[State-Management]] keeps data in sync across components
```

---

## Key PM Takeaways

1. **Rendering strategy affects SEO and performance** — Content sites need SSR/SSG for search indexing. Dashboards can use CSR.
2. **Framework choice is a hiring decision** — The talent pool for React is 3-5x larger than Angular or Vue in most markets.
3. **State complexity grows with product complexity** — Simple CRUD apps need minimal state management. Real-time collaborative features need sophisticated solutions.
4. **Backend language choice follows the problem domain** — Python for data/ML, Node.js for real-time, Java for enterprise scale, Go for high-throughput microservices.
