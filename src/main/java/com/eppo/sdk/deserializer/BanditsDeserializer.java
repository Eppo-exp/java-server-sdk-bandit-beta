package com.eppo.sdk.deserializer;

import com.eppo.sdk.dto.*;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

/**
 * Eppo Value Deserializer Class
 */
public class BanditsDeserializer extends StdDeserializer<Map<String, BanditParameters>> {

    // TODO: do I need these unused constructors?

    public BanditsDeserializer() {
        this((Class<?>) null);
    }

    protected BanditsDeserializer(Class<?> vc) {
        super(vc);
    }

    protected BanditsDeserializer(JavaType valueType) {
        super(valueType);
    }

    protected BanditsDeserializer(StdDeserializer<?> src) {
        super(src);
    }

    @Override
    public Map<String, BanditParameters> deserialize(
            JsonParser jsonParser,
            DeserializationContext deserializationContext
    ) throws IOException {
        // TODO: clean up
        JsonNode banditsNode = jsonParser.getCodec().readTree(jsonParser);
        Map<String, BanditParameters> bandits = new HashMap<>();
        banditsNode.iterator().forEachRemaining(banditNode -> {
            String banditKey = banditNode.get("banditKey").asText();
            String updatedAtStr = banditNode.get("updatedAt").asText();
            Instant instant = Instant.parse(updatedAtStr);
            Date updatedAt = Date.from(instant);
            String model = banditNode.get("model").asText();
            String modelVersion = banditNode.get("modelVersion").asText();

            BanditParameters parameters = new BanditParameters();
            parameters.setBanditKey(banditKey);
            parameters.setUpdatedAt(updatedAt);
            parameters.setModel(model);
            parameters.setModelVersion(modelVersion);

            BanditModelData modelData = new BanditModelData();
            JsonNode modelDataNode = banditNode.get("modelData");
            double gamma = modelDataNode.get("gamma").asDouble();
            modelData.setGamma(gamma);
            JsonNode modelsCoefficientsNode = modelDataNode.get("actionCoefficients");

            Map<String, BanditActionCoefficients> actionCoefficients = new HashMap<>();
            modelsCoefficientsNode.iterator().forEachRemaining(modelsCoefficientNode -> {
                String actionKey = modelsCoefficientNode.get("actionKey").asText();
                Double intercept = modelsCoefficientNode.get("intercept").asDouble();

                Map<String, BanditNumericAttributeCoefficients> numericAttributeCoefficients = new HashMap<>();
                JsonNode numericAttributeCoefficientsNode = modelsCoefficientNode.get("numericAttributeCoefficients");
                numericAttributeCoefficientsNode.iterator().forEachRemaining(numericAttributeCoefficientNode -> {
                    String attributeKey = numericAttributeCoefficientNode.get("attributeKey").asText();
                    Double coefficient = numericAttributeCoefficientNode.get("coefficient").asDouble();
                    Double missingValueCoefficient = numericAttributeCoefficientNode.get("missingValueCoefficient").asDouble();

                    BanditNumericAttributeCoefficients numericAttributeCoefficient = new BanditNumericAttributeCoefficients();
                    numericAttributeCoefficient.setAttributeKey(attributeKey);
                    numericAttributeCoefficient.setCoefficient(coefficient);
                    numericAttributeCoefficient.setMissingValueCoefficient(missingValueCoefficient);

                    numericAttributeCoefficients.put(attributeKey, numericAttributeCoefficient);
                });

                Map<String, BanditCategoryAttributeCoefficients> categoryAttributeCoefficients = new HashMap<>();
                JsonNode categoryAttributeCoefficientsNode = modelsCoefficientNode.get("categoryAttributeCoefficients");
                categoryAttributeCoefficientsNode.iterator().forEachRemaining(categoryAttributeCoefficientNode -> {
                    String attributeKey = categoryAttributeCoefficientNode.get("attributeKey").asText();
                    Double missingValueCoefficient = categoryAttributeCoefficientNode.get("missingValueCoefficient").asDouble();
                    Map<String, Double> valueCoefficients = new HashMap<>();
                    JsonNode valuesNode = categoryAttributeCoefficientNode.get("values");
                    valuesNode.iterator().forEachRemaining(valueNode -> {
                        String value = valueNode.get("value").asText();
                        Double coefficient = valueNode.get("coefficient").asDouble();
                        valueCoefficients.put(value, coefficient);
                    });

                    BanditCategoryAttributeCoefficients coefficients = new BanditCategoryAttributeCoefficients();
                    coefficients.setAttributeKey(attributeKey);
                    coefficients.setValueCoefficients(valueCoefficients);
                    coefficients.setMissingValueCoefficient(missingValueCoefficient);

                    categoryAttributeCoefficients.put(attributeKey, coefficients);
                });

                BanditActionCoefficients coefficients = new BanditActionCoefficients();
                coefficients.setActionKey(actionKey);
                coefficients.setIntercept(intercept);
                coefficients.setNumericAttributeCoefficients(numericAttributeCoefficients);
                coefficients.setCategoryAttributeCoefficients(categoryAttributeCoefficients);

                actionCoefficients.put(actionKey, coefficients);
            });

            modelData.setActionCoefficients(actionCoefficients);
            parameters.setModelData(modelData);

            bandits.put(banditKey, parameters);
        });
        return bandits;
    }
}
