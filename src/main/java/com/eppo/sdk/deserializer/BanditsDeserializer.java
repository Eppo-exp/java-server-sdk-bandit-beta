package com.eppo.sdk.deserializer;

import com.eppo.sdk.dto.*;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

/**
 * Eppo Value Deserializer Class
 */
public class BanditsDeserializer extends StdDeserializer<Map<String, BanditParameters>> {

    public BanditsDeserializer() {
        this((Class<?>) null);
    }

    protected BanditsDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Map<String, BanditParameters> deserialize(
            JsonParser jsonParser,
            DeserializationContext deserializationContext
    ) throws IOException {
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
            modelsCoefficientsNode.iterator().forEachRemaining(actionCoefficientsNode -> {
                BanditActionCoefficients coefficients = this.parseActionCoefficientsNode(actionCoefficientsNode);
                actionCoefficients.put(coefficients.getActionKey(), coefficients);
            });

            modelData.setActionCoefficients(actionCoefficients);

            parameters.setModelData(modelData);
            bandits.put(banditKey, parameters);
        });
        return bandits;
    }

    private BanditActionCoefficients parseActionCoefficientsNode(JsonNode actionCoefficientsNode) {
        String actionKey = actionCoefficientsNode.get("actionKey").asText();
        Double intercept = actionCoefficientsNode.get("intercept").asDouble();

        Map<String, BanditNumericAttributeCoefficients> numericAttributeCoefficients = new HashMap<>();
        JsonNode numericAttributeCoefficientsNode = actionCoefficientsNode.get("numericAttributeCoefficients");
        numericAttributeCoefficientsNode.iterator().forEachRemaining(numericAttributeCoefficientNode -> {
            BanditNumericAttributeCoefficients currNnumericAttributeCoefficients = this.parseNumericAttributesNode(numericAttributeCoefficientNode);
            numericAttributeCoefficients.put(currNnumericAttributeCoefficients.getAttributeKey(), currNnumericAttributeCoefficients);
        });

        Map<String, BanditCategoricalAttributeCoefficients> categoricalAttributeCoefficients = new HashMap<>();
        JsonNode categoryAttributeCoefficientsNode = actionCoefficientsNode.get("categoryAttributeCoefficients");
        categoryAttributeCoefficientsNode.iterator().forEachRemaining(categoryAttributeCoefficientNode -> {
            BanditCategoricalAttributeCoefficients coefficients = this.parseCategoricalAttributesNode(categoryAttributeCoefficientNode);
            categoricalAttributeCoefficients.put(coefficients.getAttributeKey(), coefficients);
        });

        BanditActionCoefficients coefficients = new BanditActionCoefficients();
        coefficients.setActionKey(actionKey);
        coefficients.setIntercept(intercept);
        coefficients.setNumericAttributeCoefficients(numericAttributeCoefficients);
        coefficients.setCategoricalAttributeCoefficients(categoricalAttributeCoefficients);
        return coefficients;
    }

    private BanditNumericAttributeCoefficients parseNumericAttributesNode(JsonNode numericAttributeCoefficientNode) {
        String attributeKey = numericAttributeCoefficientNode.get("attributeKey").asText();
        Double coefficient = numericAttributeCoefficientNode.get("coefficient").asDouble();
        Double missingValueCoefficient = numericAttributeCoefficientNode.get("missingValueCoefficient").asDouble();

        BanditNumericAttributeCoefficients coefficients = new BanditNumericAttributeCoefficients();
        coefficients.setAttributeKey(attributeKey);
        coefficients.setCoefficient(coefficient);
        coefficients.setMissingValueCoefficient(missingValueCoefficient);
        return coefficients;
    }

    private BanditCategoricalAttributeCoefficients parseCategoricalAttributesNode(JsonNode categoricalAttributesCoefficientNode) {
        String attributeKey = categoricalAttributesCoefficientNode.get("attributeKey").asText();
        Double missingValueCoefficient = categoricalAttributesCoefficientNode.get("missingValueCoefficient").asDouble();
        Map<String, Double> valueCoefficients = new HashMap<>();
        JsonNode valuesNode = categoricalAttributesCoefficientNode.get("values");
        valuesNode.iterator().forEachRemaining(valueNode -> {
            String value = valueNode.get("value").asText();
            Double coefficient = valueNode.get("coefficient").asDouble();
            valueCoefficients.put(value, coefficient);
        });

        BanditCategoricalAttributeCoefficients coefficients = new BanditCategoricalAttributeCoefficients();
        coefficients.setAttributeKey(attributeKey);
        coefficients.setValueCoefficients(valueCoefficients);
        coefficients.setMissingValueCoefficient(missingValueCoefficient);

        return coefficients;
    }
}
