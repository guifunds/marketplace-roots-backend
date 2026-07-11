package com.origem.backend.dto;

public record PaymentIntentResponse(String clientSecret, long amountCents, String currency) {
}
