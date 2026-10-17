---
title: ML Evaluation Metrics
aliases:
  - Model Metrics
  - Classification Metrics
  - Confusion Matrix
  - AUC-ROC
tags:
  - ml
  - data-science
  - PM2
category: Machine Learning
---

## Definition

ML evaluation metrics are quantitative measures that tell you how well a [[Machine-Learning]] model performs. Accuracy alone is often misleading — a model that predicts "no fraud" for every transaction achieves 99.8% accuracy on a dataset where only 0.2% of transactions are fraudulent, yet catches zero fraud. PMs must understand which metric matches the business problem.

## How It Works

The **confusion matrix** is the foundation of classification metrics:

|  | Predicted Positive | Predicted Negative |
|--|--------------------|--------------------|
| **Actually Positive** | True Positive (TP) | False Negative (FN) — "missed" |
| **Actually Negative** | False Positive (FP) — "false alarm" | True Negative (TN) |

Key metrics derived from the confusion matrix:

| Metric | Formula | Best When |
|--------|---------|-----------|
| **Accuracy** | (TP + TN) / Total | Classes are balanced |
| **Precision** | TP / (TP + FP) | False alarms are costly (spam filter) |
| **Recall** (Sensitivity) | TP / (TP + FN) | Missing positives is costly (cancer screening) |
| **F1 Score** | 2 * (Precision * Recall) / (Precision + Recall) | You need balance between precision and recall |
| **AUC-ROC** | Area under the ROC curve | Comparing models across all thresholds |

The **precision-recall tradeoff**: Tightening the threshold (requiring higher confidence to predict "positive") increases precision but decreases recall, and vice versa. The PM decides where to set this threshold based on business cost.

For regression models (predicting continuous values):
- **MAE** (Mean Absolute Error): Average magnitude of errors
- **RMSE** (Root Mean Squared Error): Penalizes large errors more heavily
- **R-squared**: Proportion of variance explained by the model (1.0 = perfect)

**AUC-ROC explained**: The ROC curve plots true positive rate vs. false positive rate at every threshold. AUC (Area Under Curve) summarizes this into a single number: 0.5 = random guessing, 1.0 = perfect model, 0.7-0.8 = decent, 0.8-0.9 = good, 0.9+ = excellent.

## PM Implications

- **Choose the metric before building the model**: The metric encodes business priorities. A fraud detection PM who optimizes for accuracy gets a useless model. Optimize for recall (catch all fraud) with a precision floor (limit false alarms).
- **Accuracy is the most dangerous metric**: It hides poor performance on minority classes. Always ask about class imbalance.
- **Precision vs. recall is a product decision**: Should the spam filter let some spam through (high precision, lower recall) or aggressively block everything at the risk of catching real emails (high recall, lower precision)?
- **Set thresholds based on business cost**: If a false negative costs $10,000 (missed fraud) and a false positive costs $5 (extra verification step), the math favors high recall.
- **A/B test the model, not just the metrics**: Offline metrics (AUC, F1) do not always predict real-world impact. [[ML-Evaluation-Metrics]] on held-out data are necessary but not sufficient.

## Example

A fintech PM launches a credit risk model. The data scientist reports 94% accuracy. The PM digs deeper and finds: precision = 91% (9% of approved users default), recall = 62% (38% of risky users are missed). The PM resets priorities: "We need recall above 85% — missing risky borrowers costs us more than declining a few good ones." The team adjusts the decision threshold, achieving 86% recall and 78% precision, reducing default losses by 40%.

## Related Concepts

- [[Machine-Learning]] — Evaluation metrics measure how well ML models perform
- [[Supervised-Learning]] — Classification and regression metrics apply to supervised models
- [[Bias-Variance-Tradeoff]] — Cross-validation metrics reveal underfitting and overfitting
- [[Decision-Trees]] — Feature importance is an evaluation tool beyond accuracy
- [[ML-Ethics]] — Fairness metrics extend evaluation beyond overall performance
- [[SHAP-LIME]] — Interpretability tools complement quantitative metrics
