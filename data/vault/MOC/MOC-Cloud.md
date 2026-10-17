---
title: "MOC — Cloud & Deployment"
tags:
  - MOC
  - cloud
  - deployment
  - devops
---

# Cloud & Deployment

This Map of Content covers how software goes from a developer's machine to users around the world. Cloud computing provides the infrastructure, deployment pipelines automate the delivery, and CI/CD ensures quality at every step. Understanding these concepts lets PMs have informed conversations about uptime, cost, release velocity, and infrastructure choices.

---

## Session Notes

- [[Session-09-Cloud-Deployment]] — Cloud computing models, AWS services, IaaS/PaaS/SaaS, cloud cost management
- [[Session-10-Cloud-Going-Live]] — Deployment pipelines, CI/CD, environment management, feature flags, going live

---

## Core Concepts

### [[Cloud-Computing]]
The delivery of computing resources (servers, storage, databases, networking) over the internet on a pay-as-you-go basis. Cloud eliminated the need to buy and maintain physical servers, enabling startups to launch globally with minimal upfront investment.

### [[IaaS-PaaS-SaaS]]
The three service models of cloud computing: Infrastructure as a Service (rent virtual machines), Platform as a Service (rent a managed platform), and Software as a Service (rent finished software). Each level abstracts away more infrastructure management, trading control for convenience.

### [[AWS]]
Amazon Web Services — the dominant cloud platform with 30%+ market share. Key services every PM should know: EC2 (compute), S3 (storage), RDS (databases), Lambda (serverless), CloudFront (CDN). Understanding AWS service names helps PMs read architecture diagrams and cost reports.

### [[Deployment-Pipeline]]
The automated sequence of stages that code passes through on its way from a developer's commit to production: build, test, stage, deploy. A well-designed pipeline catches bugs early and enables multiple deployments per day.

### [[CI-CD]]
Continuous Integration (automatically test every code change) and Continuous Deployment (automatically deploy passing changes to production). CI/CD is the automation backbone that makes frequent, safe releases possible.

### [[Environment-Variables]]
Configuration values (database URLs, API keys, feature flags) that differ between environments (development, staging, production) and are injected at runtime rather than hardcoded. Environment variables keep secrets out of source code and make the same codebase work across environments.

---

## Cloud Platform Selection Guide

| Factor | AWS | Google Cloud (GCP) | Microsoft Azure |
|--------|-----|-------------------|-----------------|
| **Market share** | ~31% (largest) | ~11% | ~24% |
| **Strength** | Broadest service catalog | Data/ML/BigQuery | Enterprise/hybrid, Microsoft integration |
| **Best for** | Most workloads, startups (credits) | Data-heavy, ML-first products | Enterprises already using Microsoft stack |
| **PM consideration** | Most engineers know it | Best BigQuery/Vertex AI | Best Active Directory/Office 365 integration |
| **Free tier** | 12 months + always-free tier | 90-day $300 credit + always-free | 12 months + always-free tier |
| **Managed Kubernetes** | EKS | GKE (best-in-class) | AKS |

**Simplified cloud platform decision:**
- **Default**: AWS — largest ecosystem, most documentation, biggest talent pool
- **Data/ML-heavy product**: GCP — BigQuery and Vertex AI are best-in-class
- **Enterprise selling to Microsoft shops**: Azure — seamless integration with existing Microsoft infrastructure
- **Multi-cloud or cloud-agnostic**: Use Terraform/Pulumi for infrastructure-as-code across providers

---

## Deployment Pipeline Stages

```
Developer commits code
    |
    v
[[CI-CD]] — Build & Test
    |  - Install dependencies
    |  - Run linters
    |  - Run unit tests
    |  - Run integration tests
    |  - Security scan
    |
    v
Staging Environment
    |  - Deploy to staging
    |  - Run smoke tests
    |  - QA verification
    |
    v
Production Deployment
    |  - Blue/green or canary deploy
    |  - [[Feature-Flags]] control visibility
    |  - Monitor error rates
    |
    v
Monitoring & Rollback
       - [[Environment-Variables]] configure per-environment behavior
       - Automated rollback on error spike
```

---

## Key PM Takeaways

1. **Cloud costs scale with usage** — Understand the pricing model. A runaway Lambda function or unoptimized database query can generate surprise bills.
2. **Deployment frequency correlates with team health** — DORA metrics show that elite teams deploy multiple times per day. If your team deploys monthly, the pipeline needs investment.
3. **Environment parity matters** — Bugs that appear only in production are often caused by environment differences. Staging should mirror production as closely as possible.
4. **PaaS for speed, IaaS for control** — Startups should default to PaaS (Heroku, Vercel, Railway) until they need the control that IaaS (EC2, GKE) provides.
5. **Never hardcode secrets** — API keys, database passwords, and third-party credentials belong in [[Environment-Variables]], not in source code.
