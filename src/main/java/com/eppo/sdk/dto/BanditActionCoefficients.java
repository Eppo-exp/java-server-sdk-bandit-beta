package com.eppo.sdk.dto;

import lombok.Data;

import java.util.Map;

@Data
public class BanditActionCoefficients {
  private String actionKey;
  private Double intercept;
  private Map<String, BanditNumericAttributeCoefficients> numericAttributeCoefficients;
  private Map<String, BanditCategoryAttributeCoefficients> categoryAttributeCoefficients;
}
