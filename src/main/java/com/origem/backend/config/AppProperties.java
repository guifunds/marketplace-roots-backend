package com.origem.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "app")
public record AppProperties(Payments payments, FoundingFee foundingFee, RateLimit rateLimit,
                             List<String> corsAllowedOrigin) {

    public record Payments(boolean demoMode) {
    }

    public record FoundingFee(long amountCentsBrl, long amountCentsUsd) {
    }

    public record RateLimit(int signupsPerMinute) {
    }
}
