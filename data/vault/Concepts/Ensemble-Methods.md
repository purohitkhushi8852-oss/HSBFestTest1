---
title: Ensemble Methods
aliases:
  - Bagging
  - Boosting
  - Stacking
  - Model Ensembles
tags:
  - ml
  - algorithms
  - PM2
category: Machine Learning
---

## Definition

Ensemble methods combine multiple [[Machine-Learning]] models to produce predictions that are more accurate and robust than any single model alone. The core insight is the "wisdom of crowds" — individual models make different errors, and averaging or voting across many models cancels out individual mistakes. [[Random-Forests]], gradient boosting (XGBoost, LightGBM), and stacking are the most common ensemble approaches and dominate structured data competitions.

## How It Works

Three main ensemble strategies:

| Strategy | How It Combines Models | Reduces | Key Algorithm |
|----------|----------------------|---------|---------------|
| **Bagging** | Train models on random subsets of data, average predictions | Variance | [[Random-Forests]] |
| **Boosting** | Train models sequentially, each fixing predecessor's errors | Bias | XGBoost, LightGBM, AdaBoost |
| **Stacking** | Use one model's predictions as features for another model | Both | Stacked generalization |

**Bagging (Bootstrap Aggregating):**
1. Create N random subsets of training data (with replacement)
2. Train one model on each subset independently
3. For classification: majority vote. For regression: average predictions
4. Result: lower variance, more stable predictions

**Boosting:**
1. Train a weak model on the full dataset
2. Identify which examples the model got wrong
3. Train the next model with extra weight on those misclassified examples
4. Repeat, combining all models with weighted voting
5. Result: lower bias, progressively better accuracy

**Stacking:**
1. Train several diverse base models (e.g., tree + SVM + neural net)
2. Use their predictions as input features for a "meta-model"
3. The meta-model learns which base model to trust for which type of input

Popular ensemble libraries:
- **XGBoost**: Extreme Gradient Boosting — fast, regularized, the "default" for tabular data
- **LightGBM**: Microsoft's gradient boosting — faster on large datasets
- **CatBoost**: Handles categorical features natively
- **scikit-learn**: Provides BaggingClassifier, GradientBoostingClassifier, StackingClassifier

## PM Implications

- **Ensembles are the default for tabular data**: If your ML feature operates on structured data (user attributes, transaction records, sensor readings), the data science team will almost certainly use an ensemble method. Know the tradeoffs.
- **Accuracy vs. interpretability tradeoff**: Ensembles are harder to explain than a single [[Decision-Trees|decision tree]]. Use [[SHAP-LIME]] to make ensemble predictions interpretable.
- **Diminishing returns**: Going from one model to an ensemble of 100 may improve accuracy from 85% to 91%. Going from 100 to 1,000 models might add only 0.3%. PMs should ask "is the added complexity worth the marginal gain?"
- **Latency and cost**: More models = more computation at prediction time. Real-time features (search ranking, fraud detection) need fast inference. Batch features (weekly churn prediction) can tolerate slower ensembles.
- **Kaggle vs. production**: Ensemble stacking wins data science competitions but is often too complex for production. The PM should push for the simplest model that meets the performance bar.

## Example

A ride-sharing company predicts trip ETAs using a gradient boosting ensemble (LightGBM). The model combines features like distance, current traffic, time of day, weather, and driver ratings. Individual decision trees in the ensemble capture different patterns: one tree specializes in highway routes, another in urban congestion. The ensemble averages their predictions, achieving a mean absolute error of 2.1 minutes compared to 3.8 minutes for a single decision tree. The PM sets a latency budget of 50ms per prediction, and the engineering team prunes the ensemble from 500 trees to 200 to meet it with only 0.2 minutes accuracy loss.

## Related Concepts

- [[Random-Forests]] — The most well-known bagging ensemble
- [[Decision-Trees]] — The base learner in most ensemble methods
- [[Machine-Learning]] — Ensembles are a core ML technique
- [[Bias-Variance-Tradeoff]] — Bagging reduces variance; boosting reduces bias
- [[ML-Evaluation-Metrics]] — Cross-validation measures ensemble improvement
- [[SHAP-LIME]] — Makes ensemble predictions interpretable
