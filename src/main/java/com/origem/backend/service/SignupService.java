package com.origem.backend.service;

import com.origem.backend.config.AppProperties;
import com.origem.backend.domain.Signup;
import com.origem.backend.domain.SignupStatus;
import com.origem.backend.dto.SignupRequest;
import com.origem.backend.exception.InvalidFieldException;
import com.origem.backend.exception.SignupNotFoundException;
import com.origem.backend.repository.SignupRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class SignupService {

    private static final int[] CNPJ_WEIGHTS_12 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
    private static final int[] CNPJ_WEIGHTS_13 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

    private final SignupRepository signupRepository;
    private final AppProperties appProperties;

    public SignupService(SignupRepository signupRepository, AppProperties appProperties) {
        this.signupRepository = signupRepository;
        this.appProperties = appProperties;
    }

    @Transactional
    public Signup create(SignupRequest request) {
        String lang = request.lang() != null ? request.lang() : "pt";
        boolean isPt = "pt".equals(lang);
        String email = request.email().trim().toLowerCase();
        String document = request.document().trim();

        if (isBrazil(request.country()) && !isValidCnpj(document)) {
            throw new InvalidFieldException("document", "CNPJ inválido");
        }

        if (signupRepository.existsByEmailAndProfileType(email, request.profileType())) {
            throw new InvalidFieldException("email", "Já existe um cadastro com esse e-mail para esse tipo de perfil");
        }
        if (signupRepository.existsByDocumentAndProfileType(document, request.profileType())) {
            throw new InvalidFieldException("document", "Já existe um cadastro com esse documento para esse tipo de perfil");
        }

        Signup signup = Signup.builder()
                .profileType(request.profileType())
                .name(request.name())
                .email(email)
                .phone(request.phone())
                .document(document)
                .country(request.country())
                .lang(lang)
                .status(SignupStatus.PENDING)
                .foundingFeeAmountCents(isPt
                        ? appProperties.foundingFee().amountCentsBrl()
                        : appProperties.foundingFee().amountCentsUsd())
                .currency(isPt ? "BRL" : "USD")
                .createdAt(Instant.now())
                .accessToken(UUID.randomUUID())
                .build();

        return signupRepository.save(signup);
    }

    @Transactional(readOnly = true)
    public Signup getById(UUID id, UUID accessToken) {
        return signupRepository.findByIdAndAccessToken(id, accessToken)
                .orElseThrow(() -> new SignupNotFoundException(id));
    }

    private static boolean isBrazil(String country) {
        return country == null || country.isBlank()
                || country.equalsIgnoreCase("Brasil") || country.equalsIgnoreCase("Brazil");
    }

    private static boolean isValidCnpj(String value) {
        String digits = value.replaceAll("\\D", "");
        if (digits.length() != 14 || digits.chars().distinct().count() == 1) {
            return false;
        }
        return cnpjCheckDigit(digits, CNPJ_WEIGHTS_12) == Character.getNumericValue(digits.charAt(12))
                && cnpjCheckDigit(digits, CNPJ_WEIGHTS_13) == Character.getNumericValue(digits.charAt(13));
    }

    private static int cnpjCheckDigit(String digits, int[] weights) {
        int sum = 0;
        for (int i = 0; i < weights.length; i++) {
            sum += weights[i] * Character.getNumericValue(digits.charAt(i));
        }
        int mod = sum % 11;
        return mod < 2 ? 0 : 11 - mod;
    }
}
