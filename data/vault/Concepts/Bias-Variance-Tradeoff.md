---
title: Bias-Variance Tradeoff
aliases:
  - Bias Variance
  - Underfitting vs Overfitting
  - Model Complexity Tradeoff
tags:
  - ml
  - algorithms
  - PM2
category: Machine Learning
---

## Definition

The bias-variance tradeoff is the fundamental tension in [[Machine-Learning]] between two sources of prediction error. **Bias** is the error from oversimplified assumptions — the model misses real patterns (underfitting). **Variance** is the error from excessive sensitivity to training data — the model memorizes noise (overfitting). Every ML model sits somewhere on this spectrum, and the goal is to find the sweet spot that minimizes total error.

## How It Works

| Property | High Bias (Underfitting) | High Variance (Overfitting) |
|----------|------------------------|-----------------------------|
| **Model complexity** | Too simple | Too complex |
| **Training performance** | Poor | Excellent |
| **Test performance** | Poor | Poor |
| **Symptom** | Misses obvious patterns | Memorizes noise in training data |
| **Example model** | Linear regression on nonlinear data | Very deep [[Decision-Trees]] with no pruning |
| **Fix** | Add features, use more complex model | Regularization, more data, simpler model |

Total prediction error = Bias^2 + Variance + Irreducible Noise

```
Error
  |  \                        /
  |   \  Total Error         /
  |    \       ___          /
  |     \     /   \        /
  |      \___/     \______/
  |       Sweet Spot
  |  Bias ↘          ↙ Variance
  |_________________________ Model Complexity
  Simple                    Complex
```

Key mechanisms:
- **Regularization**: Penalizes model complexity to reduce variance (L1/Lasso, L2/Ridge)
- **Cross-validation**: Splits data into multiple train/test folds to estimate true performance
- **Ensemble methods**: [[Random-Forests]] and [[Ensemble-Methods]] reduce variance by averaging many models
- **Early stopping**: Halt training before the model starts memorizing noise
- **Pruning**: Remove branches from [[Decision-Trees]] that do not improve generalization

## PM Implications

- **Accuracy on training data is misleading**: A model with 99% training accuracy may be overfitting. Always ask about test set performance.
- **More data reduces variance**: If the model overfits, collecting more training data often helps more than tuning the algorithm. PMs control data strategy.
- **Simple models first**: Start with interpretable models (decision trees, logistic regression). If they underfit, incrementally add complexity. Do not jump to deep learning by default.
- **Business context defines acceptable error**: A spam filter with 95% accuracy (5% missed spam) may be fine. A medical diagnosis model at 95% accuracy (5% missed cancers) is dangerous. The PM decides tolerance.
- **Overfitting to past behavior**: Models trained on historical data may not generalize to changing user behavior. This connects to [[Model-Drift]].

## Example

An e-commerce PM builds a model to predict which users will make a purchase. Version 1 uses only two features (page views, time on site) — it underfits, predicting everyone has a 12% purchase probability (high bias). Version 2 uses 200 features including exact URLs visited and browser window size — it overfits, achieving 97% accuracy on training data but only 61% on new users (high variance). Version 3 uses 15 carefully engineered features from [[Feature-Engineering]] with regularization — it achieves 82% accuracy on both training and test data (the sweet spot).

## Related Concepts

- [[Machine-Learning]] — The bias-variance tradeoff is a core ML concept
- [[Decision-Trees]] — Prone to high variance without pruning
- [[Random-Forests]] — Reduce variance by averaging many decision trees
- [[Ensemble-Methods]] — Bagging reduces variance; boosting reduces bias
- [[Feature-Engineering]] — Feature count directly affects model complexity
- [[ML-Evaluation-Metrics]] — Cross-validation helps detect the tradeoff
- [[Model-Drift]] — Overfitting to past data accelerates model degradation
