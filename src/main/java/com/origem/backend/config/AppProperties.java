package com.origem.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppProperties(Payments payments, FoundingFee foundingFee, String corsAllowedOrigin) {

    public record Payments(boolean demoMode) {
    }

    public record FoundingFee(long amountCentsBrl, long amountCentsUsd) {
    }
}
