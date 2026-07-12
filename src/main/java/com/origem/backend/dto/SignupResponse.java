package com.origem.backend.dto;

import com.origem.backend.domain.ProfileType;
import com.origem.backend.domain.Signup;
import com.origem.backend.domain.SignupStatus;

import java.time.Instant;
import java.util.UUID;

public record SignupResponse(
        UUID id,
        ProfileType profileType,
        String name,
        String email,
        String phone,
        String document,
        String country,
        String lang,
        SignupStatus status,
        long foundingFeeAmountCents,
        String currency,
        Instant createdAt,
        Instant paidAt,
        UUID accessToken
) {
    public static SignupResponse from(Signup signup) {
        return new SignupResponse(
                signup.getId(),
                signup.getProfileType(),
                signup.getName(),
                signup.getEmail(),
                signup.getPhone(),
                signup.getDocument(),
                signup.getCountry(),
                signup.getLang(),
                signup.getStatus(),
                signup.getFoundingFeeAmountCents(),
                signup.getCurrency(),
                signup.getCreatedAt(),
                signup.getPaidAt(),
                signup.getAccessToken()
        );
    }
}
