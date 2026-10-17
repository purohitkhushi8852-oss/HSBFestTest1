---
title: Technical Debt
aliases:
  - Tech Debt
  - Code Debt
  - Design Debt
tags:
  - strategy
  - devops
  - engineering
  - PM2
category: Strategy
---

## Definition

Technical debt is the accumulated cost of shortcuts, quick fixes, and deferred engineering work that makes future development slower and riskier. Coined by Ward Cunningham, the credit card analogy is apt: taking on debt (shipping fast with shortcuts) gives you something now, but interest accrues (each future change takes longer), and if you never pay it down, the interest eventually consumes all your capacity.

## How It Works

**Fowler's 2x2 Quadrant** classifies technical debt along two axes:

|  | **Deliberate** | **Inadvertent** |
|--|---------------|-----------------|
| **Prudent** | "We know this is a shortcut, but we need to ship by Friday. We will refactor in Sprint 3." | "Now that we have shipped and learned, we realize there was a better architecture." |
| **Reckless** | "We do not have time for unit tests." | "What is a design pattern?" |

Prudent-deliberate debt is a valid business strategy. Reckless-inadvertent debt is a symptom of under-investment in engineering.

Common types of technical debt:
- **Code debt**: Duplicated code, unclear naming, missing abstractions
- **Architecture debt**: Monolith that should be decomposed, wrong [[Tech-Stacks|tech stack]] choice
- **Test debt**: Low test coverage, flaky tests, manual QA bottlenecks
- **Documentation debt**: Undocumented APIs, missing runbooks, tribal knowledge
- **Dependency debt**: Outdated libraries with known vulnerabilities, unsupported frameworks
- **Infrastructure debt**: Manual deployments, no monitoring, missing [[CI-CD]] pipeline

How debt accumulates:
1. **Pressure to ship** — PM pushes for faster delivery, team cuts corners
2. **Changing requirements** — Code built for version 1 does not cleanly support version 5
3. **Team turnover** — New developers do not understand original design intent
4. **Deferred maintenance** — "We will fix it later" becomes never
5. **Technology evolution** — The industry moves on; your stack does not

The **20% rule**: High-performing teams allocate roughly 20% of engineering capacity to paying down technical debt. This is not negotiable "extra work" — it is the maintenance cost of a healthy codebase, like maintaining a building.

Measurement approaches:
- **Velocity trends**: If the team's velocity declines sprint over sprint for the same effort, debt is accumulating
- **Bug rate**: Rising defect rates signal underlying code quality issues
- **Cycle time**: How long does it take from commit to deploy? Increasing cycle time suggests infrastructure debt
- **Developer satisfaction surveys**: Engineers know where the pain is

## PM Implications

- **Tech debt is a PM problem, not just an engineering problem**: PMs who pressure teams to skip quality work are the primary source of reckless-deliberate debt.
- **Make debt visible**: Maintain a tech debt backlog. Quantify debt in terms of "time tax" — "This technical debt adds 2 days to every feature in the payments module."
- **The 20% investment**: Protect engineering time for debt reduction. Teams that defer all maintenance eventually grind to a halt.
- **Strategic debt is a tool**: Sometimes shipping a prototype with known shortcuts is the right call — as long as the team explicitly logs the debt and schedules repayment.
- **Debt compounds**: A small shortcut today makes the next shortcut easier to justify. After 18 months of compounding, a simple feature that should take 2 days takes 3 weeks because it touches 5 poorly documented, tightly coupled modules.
- **Rewrites are rarely the answer**: The temptation to "rewrite from scratch" is strong but almost always fails. Incremental refactoring, guided by [[Feature-Flags]] to safely migrate traffic, is more reliable.

## Example

Twitter's "fail whale" era (2008-2012) is a famous case of technical debt. The original Ruby on Rails monolith could not handle the platform's explosive growth. Rather than a risky full rewrite, Twitter incrementally migrated critical services to Scala/JVM (the timeline service, the tweet delivery pipeline) while keeping the Rails frontend. Each migration was wrapped in feature flags — new and old systems ran in parallel, and traffic was gradually shifted. The multi-year effort eliminated the fail whale while keeping the platform running, demonstrating that disciplined incremental debt repayment beats a big-bang rewrite.

## Related Concepts

- [[Build-vs-Buy]] — Buy decisions avoid creating internal maintenance debt
- [[CI-CD]] — Automated testing and deployment reduce the rate of debt accumulation
- [[Feature-Flags]] — Enable safe, incremental migration away from legacy code
- [[Tech-Stacks]] — Stack choices made today become the architecture debt of tomorrow
- [[Deployment-Pipeline]] — Infrastructure debt slows the deployment pipeline
