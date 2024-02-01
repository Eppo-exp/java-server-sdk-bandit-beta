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

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testDeserializingBandits() throws IOException {
        String jsonString = FileUtils.readFileToString(new File("src/test/resources/bandits/bandit-parameters-1.json"), "UTF8");
        BanditParametersResponse responseObject = mapper.readValue(jsonString, BanditParametersResponse.class);

        assertEquals(1, responseObject.getBandits().size());
        BanditParameters parameters = responseObject.getBandits().get("banner");
        assertEquals("banner", parameters.getBanditKey());
        assertEquals("falcon", parameters.getModel());
        assertEquals("v123", parameters.getModelVersion());

        BanditModelData modelData = parameters.getModelData();
        assertEquals(1.0, modelData.getGamma());

        Map<String, BanditActionCoefficients> actionCoefficients = modelData.getActionCoefficients();
        assertEquals(2, actionCoefficients.size());

        BanditActionCoefficients nikeCoefficients = actionCoefficients.get("nike");
        assertEquals("nike", nikeCoefficients.getActionKey());
        assertEquals(1.0, nikeCoefficients.getIntercept());

        Map<String, BanditNumericAttributeCoefficients> nikeNumericCoefficients = nikeCoefficients.getNumericAttributeCoefficients();
        assertEquals(1, nikeNumericCoefficients.size());

        BanditNumericAttributeCoefficients nikeBrandAffinityCoefficient = nikeNumericCoefficients.get("brand_affinity");
    }
}
