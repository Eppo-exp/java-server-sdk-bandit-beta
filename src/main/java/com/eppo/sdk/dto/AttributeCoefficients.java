package com.eppo.sdk.dto;

public interface AttributeCoefficients {

  public String getAttributeKey();
  public double scoreForAttributeValue(EppoValue attributeValue);
}
