package com.eppo.sdk.helpers.bandit;

import com.eppo.sdk.dto.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class FalconBanditModel implements BanditModel {

    public Map<String, Double> weighActions(BanditParameters parameters, Map<String, EppoAttributes> actions, EppoAttributes subjectAttributes) {

        // For each action we need to compute its score using the model coefficients
        Map<String, Double> actionScores = actions.entrySet().stream().collect(Collectors.toMap(
          Map.Entry::getKey,
          e -> {
            double actionScore = 0.0;

            // get all coefficients known to the model
            BanditCoefficients banditCoefficients = parameters.getModelData().getCoefficients().get(e.getKey());

            if (banditCoefficients == null) {
              // Unknown action
              return actionScore;
            }

            for( BanditNumericAttributeCoefficients actionNumericCoefficients : banditCoefficients.getActionNumericCoefficients().values()) {
              EppoValue actionContextValue = e.getValue().get(actionNumericCoefficients.getAttributeKey());
              double attributeScore = actionContextValue != null && actionContextValue.isNumeric()
                ? actionContextValue.doubleValue() * actionNumericCoefficients.getCoefficient()
                : actionNumericCoefficients.getMissingValueCoefficient();

              actionScore += attributeScore;
            }

            for( BanditCategoricalAttributeCoefficients actionCategoricalCoefficients : banditCoefficients.getActionCategoricalCoefficients().values()) {
              EppoValue actionContextValue = e.getValue().get(actionCategoricalCoefficients.getAttributeKey());
              boolean validStringContextValue = actionContextValue != null && actionContextValue.stringValue() != null && !actionContextValue.stringValue().trim().isEmpty();
              double attributeScore = validStringContextValue
                ? actionCategoricalCoefficients.getValueCoefficients().get(actionContextValue.stringValue().trim())
                : actionCategoricalCoefficients.getMissingValueCoefficient();

              actionScore += attributeScore;
            }

            for( BanditNumericAttributeCoefficients subjectNumericCoefficients : banditCoefficients.getSubjectNumericCoefficients().values()) {
              EppoValue actionContextValue = e.getValue().get(subjectNumericCoefficients.getAttributeKey());
              double attributeScore = actionContextValue != null && actionContextValue.isNumeric()
                ? actionContextValue.doubleValue() * subjectNumericCoefficients.getCoefficient()
                : subjectNumericCoefficients.getMissingValueCoefficient();

              actionScore += attributeScore;
            }

            for( BanditCategoricalAttributeCoefficients subjectCategoricalCoefficients : banditCoefficients.getSubjectCategoricalCoefficients().values()) {
              EppoValue actionContextValue = e.getValue().get(subjectCategoricalCoefficients.getAttributeKey());
              boolean validStringContextValue = actionContextValue != null && actionContextValue.stringValue() != null && !actionContextValue.stringValue().trim().isEmpty();
              double attributeScore = validStringContextValue
                ? subjectCategoricalCoefficients.getValueCoefficients().get(actionContextValue.stringValue().trim())
                : subjectCategoricalCoefficients.getMissingValueCoefficient();

              actionScore += attributeScore;
            }

            return actionScore;
          }
        ));

        // Find the action with the highest score
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
        Double gamma = 1.0; // hard-coded for now
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
