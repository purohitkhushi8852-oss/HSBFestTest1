---
title: Model Drift
aliases:
  - Data Drift
  - Concept Drift
  - Model Degradation
  - Model Staleness
tags:
  - ml
  - data-science
  - PM2
category: Machine Learning
---

## Definition

Model drift is the degradation of a [[Machine-Learning]] model's predictive performance over time because the real world changes after the model is trained. A model trained on pre-pandemic e-commerce data will fail on post-pandemic shopping patterns. Drift is not a bug — it is an inherent property of ML systems operating in dynamic environments. All production models drift; the question is how fast and whether you detect it before it causes business damage.

## How It Works

Two primary types of drift:

| Type | What Changes | Example |
|------|-------------|---------|
| **Data drift** (covariate shift) | The distribution of input features changes | A lending model trained on urban applicants receives rural applicants with different income distributions |
| **Concept drift** | The relationship between features and the target changes | "What makes a user churn" shifts after a competitor launches a new feature |

Additional drift patterns:
- **Sudden drift**: An abrupt change (new regulation, pandemic, product redesign)
- **Gradual drift**: Slow evolution of patterns over months (user demographics shifting)
- **Seasonal drift**: Predictable cyclic changes (holiday shopping, back-to-school)
- **Recurring drift**: Patterns that come and go (weekend vs. weekday behavior)

Detection methods:
1. **Statistical tests**: Compare distributions of incoming data vs. training data (KS test, PSI — Population Stability Index)
2. **Performance monitoring**: Track prediction accuracy against ground truth when labels become available
3. **Feature monitoring**: Alert when input feature distributions shift beyond a threshold
4. **Prediction monitoring**: Alert when the distribution of model outputs changes (e.g., suddenly approving 90% instead of 70%)

Retraining strategies:
- **Scheduled retraining**: Retrain weekly/monthly on fresh data (simplest, not always sufficient)
- **Triggered retraining**: Automatically retrain when drift metrics exceed thresholds
- **Online learning**: Model continuously updates on each new data point (complex but responsive)
- **Ensemble rotation**: Replace the oldest model in an ensemble with a freshly trained one

## PM Implications

- **Plan for drift from day one**: Every ML feature should launch with a monitoring and retraining plan. Ask: "How will we know when this model goes stale?"
- **Ground truth lag**: Sometimes you do not know if the model was right until weeks later (did the user actually churn? did the loan default?). This delay makes drift detection harder.
- **Business events cause drift**: Product changes, pricing updates, marketing campaigns, and competitor moves all change user behavior, invalidating the model. PMs should notify the ML team when major changes launch.
- **Cost of undetected drift**: A recommendation model that silently degrades does not crash — it just slowly reduces engagement. Revenue loss accumulates before anyone notices.
- **Retraining is not free**: Each retraining cycle costs compute, data engineering time, and validation effort. PMs must balance freshness against cost.

## Example

A food delivery app's demand prediction model was trained on 2022 data. In 2023, remote work patterns shifted — lunch orders from residential areas increased 35% while office district orders dropped. The model kept predicting high demand in office areas and low demand in suburbs, leading to driver misallocation. Delivery times in residential areas increased by 12 minutes on average. After implementing drift detection (monitoring order distribution by zip code), the team set up automatic monthly retraining, reducing the prediction error by 28%.

## Related Concepts

- [[Machine-Learning]] — Model drift is an inherent challenge in production ML
- [[MLOps]] — Drift detection and retraining are core MLOps responsibilities
- [[ML-Evaluation-Metrics]] — Monitoring metrics in production reveals drift
- [[Bias-Variance-Tradeoff]] — Overfitting to historical data accelerates drift impact
- [[Feature-Engineering]] — Temporal features help models adapt to changing patterns
- [[Analytics-Instrumentation]] — Tracking systems provide the data for drift detection
