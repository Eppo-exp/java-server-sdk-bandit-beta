package com.eppo.sdk.dto;

import java.util.Date;

/**
 * Assignment Log Data Class
 */
public class BanditLogData {
    public String experiment;
    public String bandit;
    public String subject;
    public EppoAttributes subjectAttributes;
    public String action;
    public EppoAttributes actionAttributes;
    public boolean actionSelectedByBandit;
    public Float actionProbability;
    public String modelVersion;
    public Date timestamp;

    public BanditLogData(
            String experiment,
            String bandit,
            String subject,
            EppoAttributes subjectAttributes,
            String action,
            EppoAttributes actionAttributes,
            boolean actionSelectedByBandit,
            Float actionProbability,
            String modelVersion
    ) {
        this.experiment = experiment;
        this.bandit = bandit;
        this.subject = subject;
        this.subjectAttributes = subjectAttributes;
        this.action = action;
        this.actionAttributes = actionAttributes;
        this.actionSelectedByBandit = actionSelectedByBandit;
        this.actionProbability = actionProbability;
        this.modelVersion = modelVersion;
        this.timestamp = new Date();
    }
}
