---
title: Build vs Buy
aliases:
  - Build or Buy
  - Make vs Buy
  - Build vs Buy Decision
tags:
  - strategy
  - architecture
  - PM2
category: Strategy
---

## Definition

The build-vs-buy decision is the strategic choice between developing a capability in-house (build) or purchasing/licensing an existing solution (buy). The core principle is: **build your secret sauce, buy your plumbing**. Companies that build everything waste engineering resources on commodity problems; companies that buy everything have no differentiation. The PM's job is to draw the line between core (build) and context (buy).

## How It Works

The decision framework evaluates six dimensions:

| Dimension | Favors Build | Favors Buy |
|-----------|-------------|------------|
| **Strategic differentiation** | Core to your competitive advantage | Commodity functionality (auth, payments, email) |
| **Customization needs** | Highly specific to your domain | Standard workflow that many companies share |
| **Total cost of ownership** | Long-term cost is lower with in-house team | Build cost exceeds 3-5 years of licensing fees |
| **Time to market** | You have runway to invest 6-12 months | You need the capability in weeks |
| **Team capability** | You have (or can hire) the expertise | Specialized domain outside your core competency |
| **Control and risk** | Vendor lock-in is unacceptable | Vendor is stable, API is standard, switching is feasible |

**The "secret sauce" test**: Ask, "If a competitor uses the exact same vendor solution, do we lose our edge?" If yes, build it. If no, buy it.

Decision process:
1. **Define the capability precisely** — What exactly do you need? Break it into sub-components.
2. **Assess strategic importance** — Is this core or context?
3. **Evaluate the market** — What solutions exist? How mature are they?
4. **Calculate total cost of ownership** — Build cost includes: initial dev, maintenance (20% annually), opportunity cost of engineers not building core features. Buy cost includes: license fees, integration work, customization, migration risk.
5. **Prototype and validate** — For buy: run a proof-of-concept with the top 2 vendors. For build: spike the hardest technical risk.
6. **Plan for evolution** — Requirements change. Can the vendor keep up? Can your team maintain the custom build?

Common buy categories:
- **Authentication**: [[Authentication|Auth0]], Okta, Firebase Auth
- **Payments**: Stripe, Adyen, Razorpay
- **Analytics**: Mixpanel, Amplitude, [[Analytics-Instrumentation|Segment]]
- **Communication**: Twilio (SMS), SendGrid (email)
- **Search**: Algolia, Elasticsearch (managed)

## PM Implications

- **Opportunity cost is the hidden cost of build**: Every engineer building a custom CMS is an engineer not building your core product. PMs must ruthlessly prioritize.
- **"We can build it better" is usually wrong**: Engineers often underestimate maintenance cost and overestimate differentiation value. Challenge this bias with data.
- **Vendor evaluation is a PM skill**: Evaluate vendors on: API quality, documentation, pricing model, SLA, data portability, customer references, and roadmap alignment.
- **[[Technical-Debt]] applies to buy decisions too**: A poorly integrated vendor creates its own debt. Plan integration properly.
- **Hybrid approaches exist**: Use a vendor for 80% of the functionality and build custom only the 20% that differentiates (e.g., Stripe for payments + custom fraud rules).
- **Revisit decisions periodically**: A buy decision made when you had 5 engineers may flip to build when you have 50. A build decision may flip when a strong vendor emerges.

## Example

Slack's build-vs-buy decisions illustrate the framework. They built: the real-time messaging engine (core differentiation — speed and reliability are Slack's identity), the channel/thread model (unique UX), and the search infrastructure (deep integration with their data model). They bought: authentication (using standard OAuth/SAML), payment processing (Stripe), email delivery (SendGrid), and cloud infrastructure ([[AWS]]). By buying plumbing and building their secret sauce, Slack could operate with a relatively small engineering team while competing with Microsoft Teams.

## Related Concepts

- [[Technical-Debt]] — Build decisions create maintenance obligations
- [[Tech-Stacks]] — Stack choices are a series of build-vs-buy decisions
- [[Cloud-Computing]] — IaaS/PaaS is a "buy" decision for infrastructure
- [[IaaS-PaaS-SaaS]] — Different levels of the build-vs-buy spectrum
- [[CI-CD]] — Build tools (Jenkins) vs. buy services (GitHub Actions, CircleCI)
- [[Feature-Flags]] — Can be built or bought (LaunchDarkly, Flagsmith)
