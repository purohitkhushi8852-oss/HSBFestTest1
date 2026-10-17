---
title: "MOC — Databases & Data"
tags:
  - MOC
  - databases
  - data
  - analytics
---

# Databases & Data

This Map of Content covers the full data lifecycle — from choosing how to store data, to structuring it for reliability and scale, to instrumenting your product for analytics and building data pipelines. Data decisions are among the most durable and impactful choices a PM influences: a wrong database choice or poor data model can constrain the product for years.

---

## Session Notes

- [[Session-06-Databases-Foundations]] — SQL fundamentals, relational model, ER diagrams, normalization
- [[Session-07-Databases-Analytics]] — Analytics instrumentation, tracking plans, data pipelines, event taxonomies
- [[Session-08-Databases-Scale]] — Database scaling strategies, NoSQL, polyglot persistence, migrations

---

## Data Storage Concepts

### [[SQL-Basics]]
The language of relational databases. SQL (Structured Query Language) is used to create, read, update, and delete data in tabular form. Every PM should be able to read a basic SELECT query and understand JOINs — it is the lingua franca of data.

### [[NoSQL-Databases]]
Non-relational databases optimized for specific access patterns: document stores (MongoDB), key-value stores (Redis), column-family stores (Cassandra), and graph databases (Neo4j). NoSQL trades ACID guarantees for flexibility, horizontal scalability, or performance in specific use cases.

### [[ER-Diagrams]]
Entity-Relationship diagrams visually model how data entities relate to each other. They are the blueprint for database design and the most important artifact in aligning PMs and engineers on data requirements.

### [[Normalization]]
The process of organizing database tables to reduce redundancy and protect data integrity. Normalized designs avoid update anomalies but may require more JOINs; denormalized designs are faster to read but harder to keep consistent.

---

## Data at Scale

### [[Database-Scaling]]
Strategies for handling growing data volumes: vertical scaling (bigger machines), horizontal scaling (sharding across machines), read replicas, caching layers, and connection pooling.

### [[Polyglot-Persistence]]
Using different database technologies for different parts of the same application — PostgreSQL for transactions, Redis for caching, Elasticsearch for search, MongoDB for user profiles. Each database type handles what it does best.

### [[Database-Migration]]
The disciplined process of evolving database schemas over time without losing data or breaking the application. Migrations must be reversible, tested in staging, and compatible with zero-downtime deployments.

---

## Analytics & Data Pipeline

### [[Analytics-Instrumentation]]
The practice of embedding tracking events in the product to measure user behavior. What gets measured gets managed — and what does not get tracked does not exist for decision-making.

### [[Tracking-Plan]]
A structured document that defines every analytics event: event name, properties, when it fires, and who owns it. The tracking plan is the contract between PM, engineering, and data teams.

### [[Data-Pipelines]]
The infrastructure that moves data from source systems through transformation stages to analytics destinations — ETL (Extract, Transform, Load) or ELT (Extract, Load, Transform) patterns.

---

## SQL vs NoSQL Quick Decision Guide

| Factor | SQL (PostgreSQL, MySQL) | NoSQL (MongoDB, DynamoDB) |
|--------|------------------------|---------------------------|
| **Data structure** | Well-defined, relational | Flexible, evolving schema |
| **Consistency** | Strong (ACID transactions) | Eventual (configurable) |
| **Query flexibility** | Complex JOINs, aggregations | Simple key-value lookups |
| **Scale pattern** | Vertical first, then read replicas | Horizontal from the start |
| **Best for** | Financial data, e-commerce, CRM | User profiles, IoT, content catalogs |
| **PM rule of thumb** | "I need reliable transactions" | "I need flexible schemas at scale" |

**When in doubt, start with PostgreSQL.** It handles 90% of use cases, supports JSON for semi-structured data, and the ecosystem is mature. Move to NoSQL only when you have a specific need PostgreSQL cannot meet efficiently.

---

## Key PM Takeaways

1. **Data model = product model** — How you structure data determines what features are easy or hard to build. Invest in ER diagrams early.
2. **Track everything from day one** — Retrofitting analytics is painful and you lose historical data. Ship the [[Tracking-Plan]] alongside the feature.
3. **Database choice is long-lived** — Migrating databases is one of the hardest engineering projects. Get it right early with the right constraints.
4. **Scaling is not just technical** — It requires PM decisions about acceptable latency, consistency tradeoffs, and infrastructure budget.
5. **Your data pipeline is your decision-making backbone** — If data does not flow reliably from product to warehouse, every metric dashboard and ML model is unreliable.
