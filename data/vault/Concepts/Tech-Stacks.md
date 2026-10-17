---
title: Tech Stacks
aliases:
  - Technology Stack
  - Software Stack
  - Development Stack
  - Full Stack
tags:
  - architecture
  - engineering
  - PM2
category: Architecture
---

## Definition

A tech stack is the combination of programming languages, frameworks, databases, and infrastructure tools used to build and run a software product. It is typically described in layers: **frontend** (what users see) + **backend** (server logic) + **database** (data storage) + **infrastructure** (hosting and deployment). Stack selection is one of the most consequential early decisions a PM and engineering team make — it determines hiring options, development speed, scalability ceiling, and long-term maintenance cost.

## How It Works

Layers of a tech stack:

| Layer | Purpose | Common Choices |
|-------|---------|----------------|
| **Frontend** | User interface in the browser or app | [[Frontend-Frameworks]]: React, Vue, Angular, Svelte |
| **Backend** | Business logic, API, data processing | [[Backend-Languages]]: Node.js, Python, Java, Go, Ruby |
| **Database** | Data storage and retrieval | [[SQL-Basics|PostgreSQL]], [[NoSQL-Databases|MongoDB]], Redis |
| **Infrastructure** | Hosting, scaling, deployment | [[AWS]], GCP, Azure, Vercel, Railway |
| **DevOps** | Build, test, deploy automation | [[CI-CD|GitHub Actions]], Docker, Kubernetes, Terraform |

Popular named stacks:

| Stack | Components | Best For |
|-------|-----------|----------|
| **MERN** | MongoDB, Express, React, Node.js | Startups, rapid prototyping, JavaScript everywhere |
| **MEAN** | MongoDB, Express, Angular, Node.js | Enterprise SPAs with TypeScript |
| **LAMP** | Linux, Apache, MySQL, PHP | Traditional web apps, WordPress |
| **JAMstack** | JavaScript, APIs, Markup (static) | Content sites, blogs, marketing pages |
| **Django + React** | Python/Django backend, React frontend | Data-heavy apps, ML integration |
| **Spring + Angular** | Java/Spring backend, Angular frontend | Large enterprise applications |

Stack selection criteria:
1. **Team expertise**: Use what your team knows. A brilliant stack your team cannot use is worse than a boring stack they are productive in.
2. **Hiring pool**: Can you recruit developers for this stack in your market?
3. **Ecosystem maturity**: Libraries, documentation, community support, Stack Overflow answers
4. **Performance requirements**: Real-time (Go, Rust), data processing (Python, Scala), web apps (Node, Ruby)
5. **Scalability ceiling**: Will this stack handle 10x growth without a rewrite?
6. **Vendor and community support**: Is the framework actively maintained? Is a single company a risk?

## PM Implications

- **Stack decisions are hard to reverse**: Migrating a tech stack is one of the most expensive engineering projects. The PM should ensure the initial decision is deliberate, not accidental.
- **PMs do not choose the stack, but they shape constraints**: "We need to hire 5 developers in Bangalore within 3 months" is a constraint that eliminates niche stacks. "We need ML integration" favors Python in the backend.
- **Polyglot is normal**: Modern products rarely use a single stack. The frontend may be React, the API in Go, the ML pipeline in Python, and the data warehouse in SQL. This is fine as long as the team can maintain it.
- **Stack decisions signal culture**: A startup choosing Java/Spring signals enterprise ambitions; choosing Node/React signals speed-first. Both are valid — they attract different engineers.
- **[[Technical-Debt]] starts with stack choice**: Choosing an obscure framework to be cutting-edge creates hiring debt. Choosing an aging framework creates upgrade debt.
- **[[Build-vs-Buy]] intersects with stack choice**: Managed services (Firebase, Supabase, Vercel) let small teams skip infrastructure work but create vendor dependencies.

## Example

Airbnb's stack evolution reflects strategic decisions. They started with a Ruby on Rails monolith (fast to build, Rails' convention-over-configuration matched their early "move fast" phase). As they scaled, they migrated performance-critical backend services to Java (better performance, strong typing for reliability). Their frontend evolved from Rails ERB templates to React (component reusability across web and mobile). Their data infrastructure uses Apache Spark and Hive (Python/Scala ecosystem for data science). Each transition was a deliberate [[Build-vs-Buy]] and [[Technical-Debt]] decision driven by the PM and engineering org together.

## Related Concepts

- [[Frontend-Frameworks]] — The frontend layer of the stack
- [[Backend-Languages]] — The backend layer of the stack
- [[SQL-Basics]] — Relational databases in the data layer
- [[NoSQL-Databases]] — Non-relational alternatives in the data layer
- [[Cloud-Computing]] — Infrastructure layer of the stack
- [[Build-vs-Buy]] — Stack selection is a series of build-vs-buy decisions
- [[Technical-Debt]] — Stack choices create long-term maintenance obligations
