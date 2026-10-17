---
title: "MOC — Build vs Buy & Technical Debt"
tags:
  - MOC
  - strategy
  - architecture
---

# Build vs Buy & Technical Debt

This Map of Content covers the strategic decisions that determine how engineering resources are allocated: what to build in-house, what to purchase, and how to manage the ongoing maintenance cost of a growing codebase. These decisions compound over time — a build-vs-buy choice made in Year 1 shapes the team's capacity in Year 3. PMs who master this domain become strategic partners to engineering leadership.

---

## Session Notes

- [[Session-15-Build-vs-Buy]] — Build vs buy decision framework, six evaluation dimensions, total cost of ownership, tech stacks
- [[Session-16-Tech-Debt-Wrap-Up]] — Technical debt types, measurement, the 20% rule, feature flags, course synthesis

---

## Core Concepts

### [[Build-vs-Buy]]
The strategic framework for deciding whether to develop a capability in-house or purchase an existing solution. The core principle: build your secret sauce (competitive differentiators), buy your plumbing (commodity functionality). Six dimensions guide the decision: strategic differentiation, customization needs, total cost of ownership, time to market, team capability, and control.

### [[Technical-Debt]]
The accumulated cost of engineering shortcuts that make future development slower and riskier. Fowler's 2x2 classifies debt along deliberate/inadvertent and prudent/reckless axes. The 20% rule: high-performing teams allocate roughly 20% of engineering capacity to paying down debt.

### [[Feature-Flags]]
Conditional switches that decouple deployment from release. Feature flags enable gradual rollouts, A/B testing, kill switches, and safe migration away from legacy code. They are a key tool for managing risk during build-vs-buy transitions and technical debt repayment.

### [[Tech-Stacks]]
The combination of technologies used to build and run the product. Stack selection is itself a series of build-vs-buy decisions (React vs. custom UI framework, PostgreSQL vs. custom database, AWS vs. on-premises).

### [[CI-CD]]
Continuous integration and deployment — the automation that makes frequent, safe releases possible. CI/CD infrastructure is almost always a "buy" decision (GitHub Actions, CircleCI) rather than building a custom pipeline.

---

## Build vs Buy Decision Framework

### Step 1: Classify the Capability

| Category | Action | Examples |
|----------|--------|---------|
| **Core differentiator** | Build | Recommendation engine, pricing algorithm, core UX |
| **Important but not unique** | Buy + customize | CRM integration, analytics dashboard, admin panel |
| **Commodity plumbing** | Buy (SaaS) | Authentication, payments, email, monitoring |

### Step 2: Evaluate Six Dimensions

For each capability being assessed:

| Dimension | Build Score (1-5) | Buy Score (1-5) |
|-----------|-------------------|-----------------|
| Strategic differentiation | ___ | ___ |
| Customization needs | ___ | ___ |
| Total cost of ownership (3-year) | ___ | ___ |
| Time to market | ___ | ___ |
| Team capability | ___ | ___ |
| Control and vendor risk | ___ | ___ |
| **Total** | ___ | ___ |

### Step 3: Calculate True Cost

**Build cost** = Initial development + Annual maintenance (20% of build cost) + Opportunity cost of engineers not on core features

**Buy cost** = License/subscription fees + Integration effort + Customization work + Switching cost risk

### Step 4: Decide and Document

Record the decision, the reasoning, and the conditions under which it should be revisited. A buy decision may flip to build when the team grows from 5 to 50 engineers; a build decision may flip to buy when a strong vendor emerges.

---

## Technical Debt Management Framework

```
Assess
    |  - Track velocity trends, bug rates, cycle time
    |  - Survey developers on pain points
    |  - Identify Fowler quadrant for each debt item
    v
Prioritize
    |  - Quantify "time tax" per feature area
    |  - Focus on debt blocking the next quarter's roadmap
    |  - Distinguish prudent (strategic) from reckless (fix now)
    v
Allocate
    |  - Reserve 20% of sprint capacity for debt repayment
    |  - Include debt items in sprint planning, not as "extra"
    |  - Use [[Feature-Flags]] for safe incremental migration
    v
Track
       - Measure improvement: velocity recovery, bug rate reduction
       - Celebrate debt repayment like feature launches
```

---

## Key PM Takeaways

1. **"We can build it better" is usually wrong** — Engineers overestimate differentiation and underestimate maintenance. Challenge this with the six-dimension framework.
2. **Opportunity cost is the hidden cost of building** — Every engineer building a custom CMS is not building your core product.
3. **Technical debt is a PM problem** — PMs who push for shortcuts without tracking debt create it. PMs who protect the 20% capacity manage it.
4. **Feature flags de-risk everything** — Gradual rollouts, safe migrations, and instant rollbacks make both build and debt-repayment projects less risky.
5. **Revisit decisions periodically** — The right answer changes as the team, market, and vendor landscape evolve. Schedule annual build-vs-buy reviews.
6. **Debt compounds silently** — A 2-day feature that now takes 3 weeks is a symptom of unmanaged debt. Track the trend.
