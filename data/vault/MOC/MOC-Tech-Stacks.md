---
title: "MOC — Tech Stacks Reference"
tags:
  - MOC
  - architecture
  - stacks
---

# Tech Stacks Reference

This Map of Content serves as a quick-reference guide to technology stacks — the combinations of languages, frameworks, databases, and infrastructure that power modern products. Stack knowledge helps PMs read architecture diagrams, evaluate vendor platforms, make hiring decisions, and participate meaningfully in technical discussions.

---

## Session Notes

- [[Session-15-Build-vs-Buy]] — Tech stacks overview, popular combinations, stack selection criteria, enterprise systems

---

## Core Concepts

### [[Tech-Stacks]]
The full combination of frontend, backend, database, and infrastructure technologies. Stack selection is one of the most consequential early decisions, affecting hiring, development speed, scalability, and maintenance cost.

### [[Backend-Languages]]
The server-side programming languages: Node.js (JavaScript everywhere), Python (data/ML), Java (enterprise scale), Go (high performance), Ruby (developer happiness), C#/.NET (Microsoft ecosystem). Each has distinct strengths and hiring pools.

### [[Frontend-Frameworks]]
The client-side frameworks: React (dominant, flexible), Vue (gentle learning curve), Angular (enterprise, opinionated), Svelte (performance-focused). Framework choice determines component architecture, ecosystem access, and available talent.

---

## Popular Stacks Comparison

### Startup / Rapid Prototyping Stacks

| Stack | Frontend | Backend | Database | Infrastructure | Best For |
|-------|----------|---------|----------|---------------|----------|
| **MERN** | React | Node.js/Express | MongoDB | AWS / Vercel | JavaScript-everywhere startups |
| **Next.js + Supabase** | React (Next.js) | Next.js API routes | PostgreSQL (Supabase) | Vercel | Full-stack with minimal backend |
| **Django + React** | React | Python/Django | PostgreSQL | AWS / GCP | Data-heavy apps, ML integration |
| **Rails + React** | React | Ruby on Rails | PostgreSQL | Heroku / AWS | Rapid prototyping, convention over config |

### Enterprise / Scale Stacks

| Stack | Frontend | Backend | Database | Infrastructure | Best For |
|-------|----------|---------|----------|---------------|----------|
| **Spring + Angular** | Angular | Java/Spring Boot | PostgreSQL / Oracle | AWS / Azure | Large enterprise, strong typing |
| **Go microservices** | React | Go | PostgreSQL + Redis | Kubernetes / GCP | High-throughput, microservices |
| **.NET + Blazor** | Blazor / React | C#/.NET | SQL Server | Azure | Microsoft enterprise ecosystem |
| **FastAPI + Vue** | Vue.js | Python/FastAPI | PostgreSQL | AWS / GCP | API-first, ML-ready |

### Content / Marketing Stacks

| Stack | Frontend | Backend | Database | Infrastructure | Best For |
|-------|----------|---------|----------|---------------|----------|
| **JAMstack** | Static HTML/React | Headless CMS API | Headless CMS | Netlify / Vercel | Blogs, marketing sites, docs |
| **WordPress** | PHP templates | PHP | MySQL | Any hosting | Content-heavy sites, non-technical editors |
| **Gatsby + Contentful** | React (Gatsby) | Contentful API | Contentful | Netlify | Performance-focused content sites |

---

## Stack Selection Decision Guide

```
What stage is the company?
    |
    +-- Early startup (< 10 engineers)?
    |       --> Optimize for speed and hiring
    |       --> MERN, Next.js + Supabase, or Django + React
    |       --> Use PaaS (Vercel, Railway, Heroku)
    |
    +-- Growth stage (10-100 engineers)?
    |       --> Optimize for scalability and team structure
    |       --> Microservices migration, polyglot where needed
    |       --> Move critical services to Go/Java if needed
    |
    +-- Enterprise (100+ engineers)?
            --> Optimize for reliability, compliance, team autonomy
            --> Spring/Java, .NET, or Go microservices
            --> Platform engineering team manages shared infrastructure
```

### Key Stack Selection Criteria

| Criterion | Question to Ask | Why It Matters |
|-----------|----------------|---------------|
| **Team expertise** | What does the team already know? | Productivity > theoretical superiority |
| **Hiring pool** | Can we recruit for this in our market? | A brilliant stack nobody knows is useless |
| **Ecosystem** | Libraries, tools, community support? | Mature ecosystems solve problems faster |
| **Performance needs** | Real-time? Data-heavy? I/O-bound? | Mismatch causes costly rewrites |
| **Scale ceiling** | Will this handle 10x growth? | Premature optimization wastes time, but hitting a ceiling stops growth |
| **Vendor risk** | Single company dependency? | React (Meta), Angular (Google) — evaluate health of maintainer |

---

## Language-Database Pairings

| Backend Language | Natural Database Pair | ORM / Query Builder | Reason |
|-----------------|----------------------|--------------------|---------| 
| **Node.js** | MongoDB or PostgreSQL | Prisma, Mongoose | JSON-native, async I/O |
| **Python** | PostgreSQL | SQLAlchemy, Django ORM | Data science ecosystem, strong ORM |
| **Java** | PostgreSQL / MySQL / Oracle | Hibernate, JPA | Enterprise maturity, JDBC |
| **Go** | PostgreSQL | GORM, sqlx | Performance, type safety |
| **Ruby** | PostgreSQL | ActiveRecord (Rails) | Convention over configuration |

---

## Key PM Takeaways

1. **Stack choice = hiring choice** — The most important factor is whether you can hire and retain engineers who know it. React + Node.js has the largest talent pool globally.
2. **Boring technology wins** — Choose proven, well-documented stacks over bleeding-edge. Innovation should be in the product, not the infrastructure.
3. **Polyglot is normal at scale** — Mature products use multiple languages. The frontend in TypeScript, the API in Go, the ML pipeline in Python, and the data warehouse in SQL is a standard pattern.
4. **PaaS for startups, IaaS for scale** — Start with Vercel/Railway/Heroku. Move to AWS/GCP when you need control over infrastructure.
5. **Stack migrations are expensive** — A wrong choice at 5 engineers becomes a multi-year migration project at 50 engineers. Invest in the initial decision.
6. **Every stack creates [[Technical-Debt]]** — No stack is perfect. The question is whether the debt is manageable, not whether it exists.
