package com.eppo.sdk.helpers;

import com.eppo.sdk.constants.Constants;
import com.eppo.sdk.dto.BanditParametersResponse;
import com.eppo.sdk.exception.InvalidApiKeyException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.net.http.HttpResponse;
import java.util.Optional;

// TODO: genericize
@Slf4j
public class BanditParametersRequestor {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private EppoHttpClient eppoHttpClient;

    public BanditParametersRequestor(EppoHttpClient eppoHttpClient) {
        this.eppoHttpClient = eppoHttpClient;
    }
    public Optional<BanditParametersResponse> fetchBanditParameters() {
        BanditParametersResponse parameters = null;
        try {
            HttpResponse<String> response = this.eppoHttpClient.get(Constants.BANDIT_ENDPOINT);
            int statusCode = response.statusCode();
            if (statusCode == 200) {
                parameters = OBJECT_MAPPER.readValue(response.body(), BanditParametersResponse.class);
            }
            if (statusCode == 401) {
                throw new InvalidApiKeyException("Unauthorized: invalid Eppo API key.");
            }
        } catch (InvalidApiKeyException e) {
            throw e;
        } catch (Exception e) {
            log.warn("Unable to Bandit Parameters: " + e.getMessage());
        }

        return Optional.ofNullable(parameters);
    }
}
