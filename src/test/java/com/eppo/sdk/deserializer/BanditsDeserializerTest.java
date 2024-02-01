package com.eppo.sdk.deserializer;

import com.eppo.sdk.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BanditsDeserializerTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testDeserializingBandits() throws IOException {
        String jsonString = FileUtils.readFileToString(new File("src/test/resources/bandits/bandit-parameters-1.json"), "UTF8");
        BanditParametersResponse responseObject = this.mapper.readValue(jsonString, BanditParametersResponse.class);

        assertEquals(1, responseObject.getBandits().size());
        BanditParameters parameters = responseObject.getBandits().get("banner");
        assertEquals("banner", parameters.getBanditKey());
        assertEquals("falcon", parameters.getModel());
        assertEquals("v123", parameters.getModelVersion());

        BanditModelData modelData = parameters.getModelData();
        assertEquals(1.0, modelData.getGamma());

        Map<String, BanditActionCoefficients> actionCoefficients = modelData.getActionCoefficients();
        assertEquals(2, actionCoefficients.size());

        // Nike

        BanditActionCoefficients nikeCoefficients = actionCoefficients.get("nike");
        assertEquals("nike", nikeCoefficients.getActionKey());
        assertEquals(1.0, nikeCoefficients.getIntercept());

        Map<String, BanditNumericAttributeCoefficients> nikeNumericCoefficients = nikeCoefficients.getNumericAttributeCoefficients();
        assertEquals(1, nikeNumericCoefficients.size());

        BanditNumericAttributeCoefficients nikeBrandAffinityCoefficient = nikeNumericCoefficients.get("brand_affinity");
        assertEquals("brand_affinity", nikeBrandAffinityCoefficient.getAttributeKey());
        assertEquals(1.0, nikeBrandAffinityCoefficient.getCoefficient());
        assertEquals(-0.1, nikeBrandAffinityCoefficient.getMissingValueCoefficient());

        Map<String, BanditCategoricalAttributeCoefficients> nikeCategoricalCoefficients = nikeCoefficients.getCategoricalAttributeCoefficients();
        assertEquals(1, nikeCategoricalCoefficients.size());

        BanditCategoricalAttributeCoefficients nikeGenderIdentityCoefficient = nikeCategoricalCoefficients.get("gender_identity");
        assertEquals("gender_identity", nikeGenderIdentityCoefficient.getAttributeKey());
        assertEquals(2.3, nikeGenderIdentityCoefficient.getMissingValueCoefficient());
        Map<String, Double> nikeGenderIdentityCoefficientValues = nikeGenderIdentityCoefficient.getValueCoefficients();
        assertEquals(2, nikeGenderIdentityCoefficientValues.size());
        assertEquals(0.5, nikeGenderIdentityCoefficientValues.get("female"));
        assertEquals(-0.5, nikeGenderIdentityCoefficientValues.get("male"));

        // Adidas

        BanditActionCoefficients adidasCoefficients = actionCoefficients.get("adidas");
        assertEquals("adidas", adidasCoefficients.getActionKey());
        assertEquals(1.1, adidasCoefficients.getIntercept());

        Map<String, BanditNumericAttributeCoefficients> adidasNumericCoefficients = adidasCoefficients.getNumericAttributeCoefficients();
        assertEquals(1, nikeNumericCoefficients.size());

        BanditNumericAttributeCoefficients adidasBrandAffinityCoefficient = adidasNumericCoefficients.get("brand_affinity");
        assertEquals("brand_affinity", adidasBrandAffinityCoefficient.getAttributeKey());
        assertEquals(2.0, adidasBrandAffinityCoefficient.getCoefficient());
        assertEquals(1.2, adidasBrandAffinityCoefficient.getMissingValueCoefficient());

        Map<String, BanditCategoricalAttributeCoefficients> adidasCategoricalCoefficients = adidasCoefficients.getCategoricalAttributeCoefficients();
        assertEquals(1, adidasCategoricalCoefficients.size());

        BanditCategoricalAttributeCoefficients adidasGenderIdentityCoefficient = adidasCategoricalCoefficients.get("gender_identity");
        assertEquals("gender_identity", adidasGenderIdentityCoefficient.getAttributeKey());
        assertEquals(0.45, adidasGenderIdentityCoefficient.getMissingValueCoefficient());
        Map<String, Double> adidasGenderIdentityCoefficientValues = adidasGenderIdentityCoefficient.getValueCoefficients();
        assertEquals(2, adidasGenderIdentityCoefficientValues.size());
        assertEquals(0.8, adidasGenderIdentityCoefficientValues.get("female"));
        assertEquals(0.3, adidasGenderIdentityCoefficientValues.get("male"));
    }
}
