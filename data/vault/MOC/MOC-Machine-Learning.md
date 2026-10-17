---
title: "MOC — Machine Learning"
tags:
  - MOC
  - ml
  - data-science
---

# Machine Learning

This Map of Content covers the full ML landscape from a product management perspective: foundational concepts, algorithms, production operations, and ethical responsibilities. ML is one of the most powerful tools available to PMs — it enables personalization, automation, and prediction at scale. But it also carries unique risks: models degrade, data carries bias, and "black box" decisions can cause real harm.

---

## Session Notes

- [[Session-13-ML-Foundations]] — ML fundamentals, supervised vs unsupervised learning, algorithms, bias-variance tradeoff, evaluation metrics
- [[Session-14-Applied-ML]] — Applied ML, MLOps, model drift, interpretability (SHAP/LIME), ML ethics, ensemble methods

---

## Foundational Concepts

### [[Machine-Learning]]
The overarching discipline — computers learning patterns from data to make predictions or decisions without being explicitly programmed. The three paradigms: supervised, unsupervised, and reinforcement learning.

### [[Supervised-Learning]]
Learning from labeled examples (input-output pairs). The model sees examples of correct answers and learns to predict the answer for new inputs. Covers classification (spam/not spam) and regression (predicted price).

### [[Unsupervised-Learning]]
Learning from unlabeled data — finding hidden patterns, clusters, and structures without being told what to look for. Used for customer segmentation, anomaly detection, and dimensionality reduction.

---

## Algorithms

### [[Decision-Trees]]
An interpretable algorithm that makes predictions through a series of if-then rules organized in a tree structure. Easy to explain and debug, but prone to overfitting on its own.

### [[Random-Forests]]
An ensemble of many decision trees, each trained on a random subset of data and features. Combines their predictions by majority vote (classification) or averaging (regression), dramatically reducing overfitting.

### [[Ensemble-Methods]]
The broader family of techniques for combining multiple models: bagging (Random Forests), boosting (XGBoost, LightGBM), and stacking. Ensembles dominate structured data problems in practice.

---

## Data & Features

### [[Feature-Engineering]]
The art and science of creating, selecting, and transforming input variables for ML models. Often the highest-leverage activity in an ML project — domain expertise from PMs is critical here.

### [[Bias-Variance-Tradeoff]]
The fundamental tension between underfitting (too simple, high bias) and overfitting (too complex, high variance). Every model selection and tuning decision navigates this tradeoff.

### [[ML-Evaluation-Metrics]]
How to measure model performance: accuracy, precision, recall, F1, AUC-ROC, and when each one matters. Choosing the right metric is a product decision, not a technical one.

---

## Production & Operations

### [[MLOps]]
Taking ML models from notebook to production — automated training pipelines, model registries, serving infrastructure, monitoring, and retraining workflows. The operational backbone that makes ML reliable at scale.

### [[Model-Drift]]
Why ML models degrade over time: data distributions shift, user behavior evolves, and the real world changes after training. Monitoring for drift and having retraining strategies are essential for any production ML feature.

---

## Interpretability & Ethics

### [[SHAP-LIME]]
Tools for explaining why a model made a specific prediction. SHAP (game-theoretic attribution) and LIME (local surrogate models) open the "black box," enabling debugging, regulatory compliance, and user trust.

### [[ML-Ethics]]
The principles and practices for building fair, accountable, and transparent ML systems. Covers bias in training data, fairness metrics, disparate impact, feedback loops, and responsible AI frameworks.

---

## ML Algorithm Selection Guide

```
What type of problem?
    |
    +-- Predicting a category (yes/no, A/B/C)?
    |   |
    |   +-- Need interpretability? --> [[Decision-Trees]] or Logistic Regression
    |   +-- Need best accuracy? --> [[Ensemble-Methods]] (XGBoost, LightGBM)
    |   +-- Unstructured data (images, text)? --> Deep Learning
    |
    +-- Predicting a number (price, duration)?
    |   |
    |   +-- Linear relationship? --> Linear Regression
    |   +-- Complex relationship? --> [[Random-Forests]] or Gradient Boosting
    |
    +-- Finding patterns in unlabeled data?
    |   |
    |   +-- Group similar items? --> [[Unsupervised-Learning]] (K-Means, DBSCAN)
    |   +-- Reduce dimensions? --> PCA, t-SNE
    |   +-- Detect anomalies? --> Isolation Forest, Autoencoders
    |
    +-- Sequential decisions with feedback?
        --> Reinforcement Learning
```

**PM rule of thumb**: Start simple. Logistic regression or a single decision tree first. Move to ensembles only when you have confirmed the simple model is not good enough. A simpler model that ships beats a complex model stuck in development.

---

## Key PM Takeaways

1. **Data quality > algorithm choice** — The best algorithm cannot rescue bad training data. Invest in [[Feature-Engineering]] and [[Tracking-Plan]] first.
2. **Models are not "set and forget"** — [[Model-Drift]] is inevitable. Budget for [[MLOps]] from the start.
3. **Choose the metric before building** — [[ML-Evaluation-Metrics]] encode business priorities. The PM defines what "good" looks like.
4. **Interpretability is a product requirement** — Use [[SHAP-LIME]] to explain decisions, especially in regulated industries or when user trust is at stake.
5. **Ethics is a PM responsibility** — [[ML-Ethics]] is not optional. Audit for bias, define fairness metrics, and plan for accountability.
6. **Ensembles win on accuracy** — [[Ensemble-Methods]] like XGBoost dominate structured data problems. Know when the accuracy gain justifies the complexity.
