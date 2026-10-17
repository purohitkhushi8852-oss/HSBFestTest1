---
title: "MOC — Security & Compliance"
tags:
  - MOC
  - security
  - compliance
  - privacy
---

# Security & Compliance

This Map of Content covers how products protect user data, authenticate identities, prevent attacks, and comply with privacy regulations. Security is not a feature to be added later — it is a foundational requirement that affects architecture, user trust, and legal liability. PMs who understand security can anticipate risks, prioritize the right investments, and avoid costly incidents.

---

## Session Notes

- [[Session-11-Security-Compliance]] — Authentication, authorization, OWASP Top 10, secure coding practices
- [[Session-12-Security-Privacy]] — GDPR, DPDP Act, data privacy, consent management, privacy by design

---

## Identity & Access Concepts

### [[Authentication]]
Verifying *who* a user is. Authentication answers the question "Are you who you claim to be?" through credentials (password), possession (phone for 2FA), or biometrics (fingerprint). Multi-factor authentication (MFA) combines two or more of these.

### [[Authorization]]
Determining *what* an authenticated user is allowed to do. Authorization answers "Now that we know who you are, what can you access?" Common models include Role-Based Access Control (RBAC), Attribute-Based Access Control (ABAC), and the principle of least privilege.

### [[JWT-Authentication]]
JSON Web Tokens — the technical mechanism for stateless authentication in modern web applications. JWTs encode identity claims in a signed token, enabling secure API communication without server-side session storage.

---

## Application Security

### [[OWASP-Top-10]]
The Open Web Application Security Project's list of the ten most critical web application security risks. From injection attacks to broken access control, these vulnerabilities represent the minimum security baseline every product team must address.

---

## Privacy & Regulation

### [[GDPR]]
The EU's General Data Protection Regulation — the gold standard of data privacy law. GDPR grants individuals rights over their data (access, deletion, portability) and imposes strict obligations on organizations that process personal data, with fines up to 4% of global revenue.

### [[DPDP-Act]]
India's Digital Personal Data Protection Act — India's comprehensive data protection framework modeled after GDPR but adapted for the Indian context. Understanding both GDPR and DPDP is essential for any product with users in Europe or India.

---

## Security Checklist for PMs

**Before launch:**
- [ ] Authentication: Multi-factor authentication available for all users?
- [ ] Authorization: Least-privilege access model implemented?
- [ ] OWASP: Top 10 vulnerabilities addressed in security review?
- [ ] Data encryption: At rest (AES-256) and in transit (TLS 1.2+)?
- [ ] Input validation: All user inputs sanitized against injection attacks?
- [ ] Rate limiting: APIs protected against brute-force and abuse?
- [ ] Secrets management: No API keys or passwords in source code?
- [ ] Logging and monitoring: Security events logged and alerting configured?

**Compliance:**
- [ ] Privacy policy updated and accessible?
- [ ] [[GDPR]] compliance: Consent mechanism, data deletion flow, DPA with processors?
- [ ] [[DPDP-Act]] compliance (if serving Indian users): Notice and consent, data fiduciary obligations?
- [ ] Data retention policy defined and enforced?
- [ ] Third-party vendor security review completed?
- [ ] Incident response plan documented and tested?

**Ongoing:**
- [ ] Dependency scanning for known vulnerabilities (Dependabot, Snyk)?
- [ ] Penetration testing scheduled (at least annually)?
- [ ] Security training for the development team?
- [ ] Access reviews for internal tools (quarterly)?

---

## How These Concepts Connect

```
User arrives at product
    |
    v
[[Authentication]] — "Who are you?" (login, MFA)
    |
    v
[[JWT-Authentication]] — Token issued for session
    |
    v
[[Authorization]] — "What can you do?" (RBAC, permissions)
    |
    v
[[OWASP-Top-10]] — Application defends against attacks
    |
    v
[[GDPR]] / [[DPDP-Act]] — Data handled per regulations
```

---

## Key PM Takeaways

1. **Security breaches destroy trust instantly** — It takes years to build user trust and one breach to lose it. Security investment is brand insurance.
2. **Authentication and authorization are different** — Logging in (authentication) does not mean you can access everything (authorization). Both must be designed deliberately.
3. **OWASP Top 10 is the minimum bar** — If your product has not been reviewed against OWASP, it is not ready for production.
4. **Privacy regulation is accelerating globally** — GDPR, DPDP, and similar laws are becoming the norm, not the exception. Design for privacy from day one ("privacy by design").
5. **Security is a continuous process** — A one-time audit is not enough. Dependencies gain vulnerabilities, new attack vectors emerge, and team members change. Automate what you can.
