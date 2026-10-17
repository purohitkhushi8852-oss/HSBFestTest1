---
title: GDPR
aliases:
  - General Data Protection Regulation
  - EU Data Protection
  - EU Privacy Law
tags:
  - privacy
  - compliance
  - regulation
  - PM2
category: Privacy & Compliance
---

## Definition

The General Data Protection Regulation (GDPR) is a comprehensive data protection law enacted by the European Union in 2018. It governs how organizations collect, process, store, and share personal data of EU residents — regardless of where the organization is based. Non-compliance can result in fines up to 4% of global annual revenue or 20 million euros, whichever is higher.

## How It Works

GDPR is built on seven core principles:

1. **Lawfulness, fairness, transparency** — Clear legal basis for processing data
2. **Purpose limitation** — Collect data only for specified, legitimate purposes
3. **Data minimization** — Collect only what is necessary
4. **Accuracy** — Keep personal data accurate and up to date
5. **Storage limitation** — Do not retain data longer than needed
6. **Integrity and confidentiality** — Protect data with appropriate security
7. **Accountability** — Demonstrate compliance proactively

Key user rights under GDPR:

| Right | What It Means | PM Action |
|-------|--------------|-----------|
| **Right to access** | Users can request all data held about them | Build data export feature |
| **Right to rectification** | Users can correct inaccurate data | Allow profile editing |
| **Right to erasure** ("right to be forgotten") | Users can request deletion of their data | Build account deletion flow |
| **Right to portability** | Users can download their data in machine-readable format | Build data download (JSON/CSV) |
| **Right to object** | Users can opt out of processing (e.g., marketing) | Build consent management |
| **Right to restrict processing** | Users can limit how their data is used | Granular privacy controls |

## PM Implications

- **Consent design**: Cookie banners, privacy settings, and opt-in flows are product features that PMs must design carefully. "Pre-checked boxes" are illegal.
- **Data mapping**: PMs should know what personal data each feature collects and where it is stored — critical for responding to data subject requests.
- **Feature constraints**: Features like "recommend friends from contacts" require explicit consent and careful implementation.
- **Vendor compliance**: Every third-party tool that processes user data ([[Tracking-Plan]], analytics, CRM) must be GDPR-compliant. PMs own vendor selection.
- **Data retention policies**: PMs must define how long each type of data is kept and ensure automated deletion when the retention period expires.
- **Privacy by design**: GDPR requires privacy considerations to be built into products from the start, not bolted on after.
- **Cross-border data transfers**: Storing EU user data on [[AWS]] servers in the US requires additional legal frameworks (Standard Contractual Clauses).

## Example

In January 2022, the French data protection authority (CNIL) fined Google 150 million euros and Facebook 60 million euros for making it difficult to reject cookies. Their cookie banners offered a one-click "Accept All" button but required multiple clicks to reject cookies — violating the principle that consent must be as easy to withdraw as to give. Both companies had to redesign their cookie consent flows for EU users within three months.

## Related Concepts

- [[DPDP-Act]] — India's equivalent data protection law, inspired partly by GDPR
- [[Authentication]] — Login data is personal data under GDPR
- [[Tracking-Plan]] — Analytics tracking must comply with GDPR consent requirements
- [[OWASP-Top-10]] — Security measures required by GDPR's integrity principle
- [[Authorization]] — Access controls needed to protect personal data
- [[Environment-Variables]] — Data residency requirements affect infrastructure configuration
