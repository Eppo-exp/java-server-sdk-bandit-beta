package com.eppo.sdk.dto;

import lombok.Data;

import java.util.Map;

@Data
public class BanditCategoryAttributeCoefficients {
  private String attributeKey;
  private Double missingValueCoefficient;
  private Map<String, Double> valueCoefficients;
}
