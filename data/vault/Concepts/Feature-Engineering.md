---
title: Feature Engineering
aliases:
  - Feature Selection
  - Feature Extraction
  - Feature Transformation
tags:
  - ml
  - data-science
  - PM2
category: Machine Learning
---

## Definition

Feature engineering is the process of selecting, creating, and transforming input variables (features) that a [[Machine-Learning]] model uses to make predictions. It is often the single most impactful step in building an ML system — a mediocre algorithm with great features will outperform a brilliant algorithm with poor features. Feature engineering requires both domain expertise ("what matters in this business?") and data intuition ("how can I represent this numerically?").

## How It Works

Raw data rarely arrives in a form ML algorithms can use directly. Feature engineering bridges the gap:

| Technique | Description | Example |
|-----------|-------------|---------|
| **Selection** | Choosing which raw variables to include | Dropping "user ID" (not predictive), keeping "login frequency" |
| **Creation** | Deriving new features from existing ones | "days since last purchase" from a timestamp |
| **Transformation** | Scaling or encoding variables | Normalizing income to 0-1 range, one-hot encoding categories |
| **Aggregation** | Summarizing groups of data | "average order value in last 90 days" from individual orders |
| **Interaction** | Combining features | "price per square foot" from price and area |

Common transformations:
- **One-hot encoding**: Convert "country = India" to binary columns (is_India=1, is_US=0, ...)
- **Binning**: Convert continuous age (27, 34, 52) into buckets (18-30, 31-45, 46-60)
- **Log transform**: Compress skewed distributions (income, page views)
- **Lag features**: For time-series — "value 7 days ago" as a feature for today's prediction

Feature selection methods:
1. **Domain knowledge** — A PM who understands the business suggests features data scientists may miss
2. **Correlation analysis** — Drop features that do not correlate with the target
3. **Recursive elimination** — Train the model, drop the weakest feature, repeat
4. **Regularization** — Algorithms like Lasso automatically shrink unimportant features to zero

## PM Implications

- **PMs are feature engineering partners**: Your domain knowledge is irreplaceable. Data scientists know algorithms; PMs know which behaviors, events, and attributes actually matter for the business problem.
- **Data availability drives feasibility**: A brilliant feature is useless if the data does not exist. PMs must ensure the [[Tracking-Plan]] captures the events that become ML features.
- **Feature stores accelerate ML**: Reusable feature repositories (Feast, Tecton) let teams share computed features across models, reducing duplicate work.
- **Privacy constraints limit features**: Some high-signal features (race, zip code as a proxy for income) raise ethical concerns. PMs must define which features are off-limits. See [[ML-Ethics]].
- **Fewer features can be better**: Adding too many features causes overfitting and increases computational cost. The [[Bias-Variance-Tradeoff]] applies here directly.

## Example

Spotify's Discover Weekly playlist relies on heavily engineered features. Raw data (play events) is transformed into features like: "ratio of track completions to skips per genre," "time-of-day listening patterns," "artist diversity index," and "tempo preference distribution." These engineered features, not the raw play counts, are what makes the recommendation model effective. A PM's insight — "users who listen to the same song on repeat behave differently from playlist listeners" — becomes a feature that improves predictions.

## Related Concepts

- [[Machine-Learning]] — Feature engineering is a critical step in the ML workflow
- [[Bias-Variance-Tradeoff]] — Too many features risk overfitting (high variance)
- [[ML-Evaluation-Metrics]] — Feature quality is measured by its impact on model metrics
- [[Supervised-Learning]] — Features serve as the input variables for supervised models
- [[Tracking-Plan]] — Analytics events are the raw material for feature engineering
- [[ML-Ethics]] — Some features can introduce or amplify bias
