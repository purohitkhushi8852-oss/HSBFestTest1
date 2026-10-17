---
title: ML Ethics
aliases:
  - AI Ethics
  - Responsible AI
  - Algorithmic Fairness
  - ML Bias
tags:
  - ml
  - ethics
  - PM2
category: Machine Learning
---

## Definition

ML ethics encompasses the principles and practices for building [[Machine-Learning]] systems that are fair, accountable, and transparent. Because ML models learn patterns from historical data, they can inherit and amplify existing societal biases — discriminating at scale, automatically, and with a veneer of objectivity. A PM shipping an ML feature is responsible for ensuring the model does not cause harm to users or protected groups.

## How It Works

**How bias enters ML systems:**

| Stage | Bias Source | Example |
|-------|-----------|---------|
| **Data collection** | Historical bias in training data | Hiring model trained on past decisions where women were underrepresented |
| **Feature selection** | Proxy variables | Zip code as a proxy for race in lending models |
| **Labeling** | Human annotator bias | Content moderation labels reflecting cultural biases |
| **Model design** | Optimization target | Engagement optimization amplifying outrage and misinformation |
| **Deployment** | Feedback loops | Predictive policing: more policing leads to more arrests, reinforcing the model |

**Fairness metrics** (no single definition of "fair"):
- **Demographic parity**: Positive predictions are equally distributed across groups
- **Equal opportunity**: True positive rates are equal across groups
- **Equalized odds**: Both true positive and false positive rates are equal across groups
- **Individual fairness**: Similar individuals receive similar predictions
- **Disparate impact ratio**: Selection rate of protected group / selection rate of majority group (must exceed 0.8 under the "four-fifths rule")

**Mitigation techniques:**
1. **Pre-processing**: Rebalance training data, remove proxy features
2. **In-processing**: Add fairness constraints during model training
3. **Post-processing**: Adjust prediction thresholds per group to equalize outcomes
4. **Auditing**: Regular bias audits using tools like [[SHAP-LIME]], AI Fairness 360 (IBM), or Fairlearn (Microsoft)

Responsible AI frameworks:
- **Model cards**: Standardized documentation of a model's intended use, limitations, and bias evaluations
- **Datasheets for datasets**: Document how training data was collected, who is represented, and known limitations
- **Human-in-the-loop**: Keep humans in the decision chain for high-stakes predictions

## PM Implications

- **You own the ethical risk**: When an ML feature causes harm, the PM — not the data scientist — faces the user impact, press scrutiny, and regulatory response.
- **Define fairness before building**: The team must agree on which fairness metric applies before training begins. This is a product and policy decision, not a technical one.
- **Audit before launch, and regularly after**: Test model performance across demographic groups. A model that is 92% accurate overall but 71% accurate for a specific group is discriminatory.
- **Transparency builds trust**: Explain to users how ML affects their experience. [[SHAP-LIME]] tools help generate these explanations.
- **Feedback loops are dangerous**: If your model affects the data it is later trained on (recommendations shaping what people watch, which then shapes training data), bias compounds over time.
- **Regulation is accelerating**: The EU AI Act, India's [[DPDP-Act]], and sector-specific regulations increasingly require impact assessments for high-risk AI systems.

## Example

Amazon built an internal hiring tool that used ML to screen resumes. The model was trained on 10 years of hiring data — a period during which Amazon (and the tech industry) predominantly hired men. The model learned to penalize resumes containing the word "women's" (e.g., "women's chess club captain") and downgrade graduates of all-women's colleges. Amazon scrapped the tool in 2018. The lesson: historical data reflects historical bias, and without explicit fairness constraints, ML will reproduce and scale that bias.

## Related Concepts

- [[Machine-Learning]] — ML ethics governs the responsible use of ML systems
- [[SHAP-LIME]] — Interpretability tools for detecting and explaining bias
- [[ML-Evaluation-Metrics]] — Fairness metrics extend standard evaluation
- [[GDPR]] — Data protection regulations intersect with ML ethics (right to explanation)
- [[DPDP-Act]] — India's data protection framework applies to ML systems
- [[Feature-Engineering]] — Feature selection can introduce or prevent proxy discrimination
