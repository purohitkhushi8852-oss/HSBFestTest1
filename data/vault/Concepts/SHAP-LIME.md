---
title: SHAP & LIME
aliases:
  - SHAP
  - LIME
  - Model Interpretability
  - Explainable AI
  - XAI
tags:
  - ml
  - ethics
  - PM2
category: Machine Learning
---

## Definition

SHAP (SHapley Additive exPlanations) and LIME (Local Interpretable Model-agnostic Explanations) are tools for explaining why a [[Machine-Learning]] model made a specific prediction. As ML models grow more complex (deep learning, large ensembles), they become "black boxes" — they work, but no one can explain why. SHAP and LIME open the black box by quantifying each feature's contribution to individual predictions, making models auditable, debuggable, and trustworthy.

## How It Works

**LIME** works by:
1. Taking a single prediction to explain
2. Generating perturbed (slightly modified) versions of that input
3. Observing how the model's prediction changes
4. Fitting a simple, interpretable model (like linear regression) to these local perturbations
5. The simple model's coefficients explain which features mattered most for that specific prediction

**SHAP** works by:
1. Borrowing from cooperative game theory (Shapley values)
2. Treating each feature as a "player" contributing to the prediction
3. Computing each feature's marginal contribution across all possible feature combinations
4. Guaranteeing mathematically consistent, fair attribution of importance

| Property | LIME | SHAP |
|----------|------|------|
| **Approach** | Local perturbation + surrogate model | Game-theoretic attribution |
| **Scope** | Explains one prediction at a time | Individual + global explanations |
| **Speed** | Fast | Slower (exact), fast (approximate) |
| **Consistency** | Approximation, may vary between runs | Mathematically guaranteed properties |
| **Output** | Feature weights for one prediction | Shapley values for each feature |

SHAP visualization types:
- **Waterfall plot**: Shows how each feature pushed a single prediction above or below the baseline
- **Beeswarm plot**: Shows global feature importance and direction of effects across many predictions
- **Dependence plot**: Shows how one feature's values affect predictions, colored by interaction

## PM Implications

- **Regulatory compliance**: Finance (ECOA, FCRA), healthcare, and insurance regulations often require that decisions be explainable. "The algorithm said so" is not a valid justification for denying a loan.
- **Debugging models**: When a model makes a surprising prediction, SHAP/LIME reveal why. A PM can ask: "Why was this low-risk customer flagged?" and get a feature-by-feature answer.
- **Building user trust**: Showing users why a recommendation was made ("Because you watched X and Y") increases engagement and trust.
- **Detecting bias**: If "zip code" is the top SHAP feature for loan decisions, it may be acting as a proxy for race. SHAP makes hidden bias visible. See [[ML-Ethics]].
- **Feature prioritization**: Global SHAP values show which features matter most, guiding [[Feature-Engineering]] investment and data collection priorities.

## Example

A health insurance company uses a [[Random-Forests]] model to flag claims for fraud review. A specific claim for $8,400 is flagged. The claims adjuster needs to know why before calling the provider. SHAP analysis reveals: "provider submitted 3x more claims than average" (+0.32 contribution), "claim amount is 2 standard deviations above the mean for this procedure" (+0.21), "patient had no prior history of this condition" (+0.15). The adjuster now has specific, defensible reasons to investigate rather than a generic "the model flagged it."

## Related Concepts

- [[Machine-Learning]] — SHAP and LIME explain ML model predictions
- [[Decision-Trees]] — Inherently interpretable, unlike models that need SHAP/LIME
- [[Random-Forests]] — Often used with SHAP because the ensemble itself is hard to interpret
- [[ML-Ethics]] — Interpretability is essential for detecting and mitigating bias
- [[ML-Evaluation-Metrics]] — Interpretability complements quantitative performance metrics
- [[Feature-Engineering]] — SHAP reveals which engineered features actually matter
