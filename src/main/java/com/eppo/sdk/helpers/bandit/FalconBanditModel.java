package com.eppo.sdk.helpers.bandit;

import com.eppo.sdk.dto.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class FalconBanditModel implements BanditModel {

  public Map<String, Double> weighActions(BanditParameters parameters, Map<String, EppoAttributes> actions, EppoAttributes subjectAttributes) {

    // For each action we need to compute its score using the model coefficients
    Map<String, Double> actionScores = actions.entrySet().stream().collect(Collectors.toMap(
      Map.Entry::getKey, e -> {
        String actionName = e.getKey();
        EppoAttributes actionAttributes = e.getValue();
        double actionScore = 0.0;

        // get all coefficients known to the model for this action
        BanditCoefficients banditCoefficients = parameters.getModelData().getCoefficients().get(actionName);

        if (banditCoefficients == null) {
          // Unknown action; return default score of 0
          return actionScore;
        }

        actionScore += banditCoefficients.getIntercept();

        actionScore += scoreContextForCoefficients(actionAttributes, banditCoefficients.getActionNumericCoefficients());
        actionScore += scoreContextForCoefficients(actionAttributes, banditCoefficients.getActionCategoricalCoefficients());
        actionScore += scoreContextForCoefficients(subjectAttributes, banditCoefficients.getSubjectNumericCoefficients());
        actionScore += scoreContextForCoefficients(subjectAttributes, banditCoefficients.getSubjectCategoricalCoefficients());

        return actionScore;
      })
    );

    // Convert to scores to weights (probabilities between 0 and 1 that collectively add up to 1.0)
    Map<String, Double> actionWeights = computeActionWeights(actionScores);
    return actionWeights;
  }

  private static double scoreContextForCoefficients(EppoAttributes context, Map<String, ? extends AttributeCoefficients> coefficients) {

    double totalScore = 0.0;

    for (AttributeCoefficients attributeCoefficients : coefficients.values()) {
      EppoValue contextValue = context.get(attributeCoefficients.getAttributeKey());
      double attributeScore = attributeCoefficients.scoreForAttributeValue(contextValue);
      totalScore += attributeScore;
    }

    return totalScore;
  }

  private static Map<String, Double> computeActionWeights(Map<String, Double> actionScores) {
    Double highestScore = null;
    String highestScoredAction = null;
    for (Map.Entry<String, Double> actionScore : actionScores.entrySet()) {
      if (highestScore == null || actionScore.getValue() > highestScore) {
        highestScore = actionScore.getValue();
        highestScoredAction = actionScore.getKey();
      }
    }

    // Weigh all the actions using their score
    Map<String, Double> actionWeights = new HashMap<>();
    double gamma = 1.0; // hard-coded for now
    double totalNonHighestWeight = 0.0;
    for (Map.Entry<String, Double> actionScore : actionScores.entrySet()) {
      if (actionScore.getKey().equals(highestScoredAction)) {
        // The highest scored action is weighed at the end
        continue;
      }

      // Compute weight and round to four decimal places
      double  unroundedProbability = 1 / (actionScores.size() + (gamma * (highestScore - actionScore.getValue())));
      double roundedProbability = Math.round(unroundedProbability * 10000d) / 10000d;
      totalNonHighestWeight += roundedProbability;

      actionWeights.put(actionScore.getKey(), roundedProbability);
    }

    // Weigh the highest scoring action
    double weightForHighestScore = 1 - totalNonHighestWeight;
    actionWeights.put(highestScoredAction, weightForHighestScore);
    return actionWeights;
  }
}
