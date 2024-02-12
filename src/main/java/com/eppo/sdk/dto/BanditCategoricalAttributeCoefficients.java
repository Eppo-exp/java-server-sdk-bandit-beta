package com.eppo.sdk.dto;

import lombok.Data;

import java.util.Map;

@Data
public class BanditCategoricalAttributeCoefficients implements AttributeCoefficients {
  private String attributeKey;
  private Double missingValueCoefficient;
  private Map<String, Double> valueCoefficients;

  public double scoreForAttributeValue(EppoValue attributeValue) {
    Double coefficient = null;
    if (attributeValue != null && !attributeValue.isNumeric()) {
      String valueKey = attributeValue.toString();
      coefficient = valueCoefficients.get(valueKey);
    }

    // Categorical attributes are treated as one-hot booleans, so it's just the coefficient * 1 when present
    return coefficient != null ? coefficient : missingValueCoefficient;
  }
}
