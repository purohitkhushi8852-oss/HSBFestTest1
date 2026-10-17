---
title: Feature Flags
aliases:
  - Feature Toggles
  - Feature Switches
  - Feature Gates
  - Dark Launch
tags:
  - devops
  - engineering
  - PM2
category: DevOps
---

## Definition

Feature flags (also called feature toggles) are conditional switches in code that allow teams to enable or disable features without deploying new code. They decouple **deployment** (pushing code to production) from **release** (exposing the feature to users). This means code can be deployed to production in a dormant state and activated later — for specific users, a percentage of traffic, or everyone at once — and turned off instantly if something goes wrong.

## How It Works

At its simplest, a feature flag is an if-statement controlled by a configuration service:

```
if (featureFlags.isEnabled("new-checkout-flow", user)) {
    showNewCheckout();    // New experience
} else {
    showOldCheckout();    // Current experience
}
```

Types of feature flags:

| Type | Lifespan | Purpose | Example |
|------|----------|---------|---------|
| **Release flag** | Days to weeks | Control rollout of a new feature | "Enable new dashboard for 10% of users" |
| **Experiment flag** | Weeks | A/B testing | "Test pricing page variant B" |
| **Ops flag** | Permanent | Kill switch for system protection | "Disable recommendation engine under high load" |
| **Permission flag** | Permanent | Entitlement gating | "Enable premium features for paid users" |

Rollout strategies:
1. **Internal dogfooding**: Enable for employees first
2. **Beta group**: Enable for opted-in beta users
3. **Percentage rollout**: 1% -> 5% -> 25% -> 50% -> 100%, monitoring metrics at each stage
4. **Geographic rollout**: Launch in one market, then expand
5. **Kill switch**: Instantly disable if error rates spike

Feature flag management platforms:
- **LaunchDarkly**: Enterprise-grade, real-time flag management
- **Flagsmith**: Open-source alternative
- **Split.io**: Feature flags + experimentation
- **Unleash**: Open-source, self-hosted option
- **ConfigCat**: Simple, lightweight flag service

Technical implementation:
- Flags are stored in a centralized service, not hardcoded
- SDKs evaluate flags locally with cached rules (low latency)
- Changes propagate in seconds without deployment
- Audit logs track who changed what flag and when

## PM Implications

- **Ship faster with less risk**: PMs can push for more ambitious features when "we can turn it off in 30 seconds" is a safety net. This fundamentally changes the risk calculus.
- **PMs can control releases**: With a flag management dashboard, PMs can activate features themselves — no engineering deployment needed.
- **Gradual rollouts protect users**: Instead of exposing 100% of users to a potentially buggy feature, roll out to 1% first and watch error rates, latency, and user feedback.
- **A/B testing becomes trivial**: Feature flags are the mechanism behind A/B tests. Show variant A to 50%, variant B to 50%, measure the difference.
- **Technical debt risk**: Stale flags (features fully rolled out but flag still in code) become [[Technical-Debt]]. Enforce a policy: remove the flag within 2 sprints of full rollout.
- **Flag dependencies create complexity**: When flag A depends on flag B, the combinatorial explosion of states makes testing difficult. Keep flag interactions minimal.
- **Operational safety**: An ops flag that disables non-critical features under load (e.g., turning off recommendations during a traffic spike) keeps the core product running.

## Example

Facebook (Meta) deploys code to production multiple times per day, all behind feature flags. When they launched the "Reactions" feature (beyond just Like), it was developed and deployed to production over several months — invisible to users. First, it was enabled for internal employees. Then a small percentage of users in select markets. The team monitored engagement metrics, server load, and user sentiment at each stage. Only after weeks of gradual expansion did Reactions reach 100% of users. If any stage had shown negative metrics, one toggle would have reverted billions of users back to Like-only — no code rollback needed.

## Related Concepts

- [[CI-CD]] — Feature flags complement CI/CD by decoupling deploy from release
- [[Deployment-Pipeline]] — Flags are activated after code moves through the pipeline
- [[Technical-Debt]] — Stale feature flags are a common source of debt
- [[Build-vs-Buy]] — Flag management platforms (LaunchDarkly) vs. custom implementation
- [[Environment-Variables]] — Env vars handle environment config; flags handle feature state
