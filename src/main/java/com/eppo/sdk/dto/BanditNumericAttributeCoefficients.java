package com.eppo.sdk.dto;

import lombok.Data;

@Data
public class BanditNumericAttributeCoefficients implements AttributeCoefficients {
  private String attributeKey;
  private Double coefficient;
  private Double missingValueCoefficient;

  public double scoreForAttributeValue(EppoValue attributeValue) {
    return attributeValue != null && attributeValue.isNumeric() ? coefficient * attributeValue.doubleValue() : missingValueCoefficient;
  }
}
