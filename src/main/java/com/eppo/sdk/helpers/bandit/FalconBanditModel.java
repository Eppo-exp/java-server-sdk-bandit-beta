package com.eppo.sdk.helpers.bandit;

import com.eppo.sdk.dto.*;

import java.util.Map;
import java.util.stream.Collectors;

public class FalconBanditModel implements BanditModel {

    public Map<String, Float> weighActions(BanditParameters parameters, Map<String, EppoAttributes> actions, EppoAttributes subjectAttributes) {

        // For each action we need to compute its weight using the model coefficients

        Map<String, Double> actionWeights = actions.entrySet().stream().collect(Collectors.toMap(
          Map.Entry::getKey,
          e -> {

            // get all coefficients known to the model
            BanditCoefficients actionCoefficients = parameters.getModelData().getCoefficients().get(e.getKey());

            double actionWeight = 0.0;

            for( BanditNumericAttributeCoefficients actionNumericCoefficients : actionCoefficients.getActionNumericCoefficients().values()) {
              EppoValue actionContextValue = e.getValue().get(actionNumericCoefficients.getAttributeKey());
              double attributeWeight = actionContextValue != null && actionContextValue.isNumeric()
                ? actionContextValue.doubleValue() * actionNumericCoefficients.getCoefficient()
                : actionNumericCoefficients.getMissingValueCoefficient();

              actionWeight += attributeWeight;
            }

            for( BanditCategoricalAttributeCoefficients actionCategoricalCoefficients : actionCoefficients.getActionCategoricalCoefficients().values()) {
              EppoValue actionContextValue = e.getValue().get(actionCategoricalCoefficients.getAttributeKey());
              boolean validStringContextValue = actionContextValue != null && actionContextValue.stringValue() != null && !actionContextValue.stringValue().trim().isEmpty();
              double attributeWeight = validStringContextValue
                ? actionCategoricalCoefficients.getValueCoefficients().get(actionContextValue.stringValue().trim())
                : actionCategoricalCoefficients.getMissingValueCoefficient();

              actionWeight += attributeWeight;
            }

            // TODO: same as above but for subject context

            return actionWeight;
          }
        ));




        final float weightPerAction = 1 / (float)actions.size();
        return actions.keySet().stream().collect(Collectors.toMap(
            key -> key,
            value -> weightPerAction
        ));
    }
}
