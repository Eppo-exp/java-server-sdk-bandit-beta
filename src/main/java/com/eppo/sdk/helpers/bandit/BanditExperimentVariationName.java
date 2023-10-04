package com.eppo.sdk.helpers.bandit;

import java.util.Arrays;

public enum BanditExperimentVariationName {
    BANDIT, CONTROL;

    public static BanditExperimentVariationName of(String input) {
        return Arrays.stream(BanditExperimentVariationName.values())
                .filter(n -> input.equalsIgnoreCase(n.name()))
                .findFirst()
                .orElse(null);
    }
}
