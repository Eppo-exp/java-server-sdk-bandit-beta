package com.eppo.sdk.helpers.bandit;

public class BanditModelFactory {
    public static BanditModel build(String modelName) {
        switch(modelName) {
            case "random":
                return new RandomBanditModel();
            case "falcon":
                return new FalconBanditModel();
            default:
                throw new IllegalArgumentException("Unknown bandit model " + modelName);
        }
    }
}
