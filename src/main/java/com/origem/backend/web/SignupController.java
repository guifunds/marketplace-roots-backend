package com.origem.backend.web;

import com.origem.backend.domain.Signup;
import com.origem.backend.dto.PaymentIntentResponse;
import com.origem.backend.dto.SignupRequest;
import com.origem.backend.dto.SignupResponse;
import com.origem.backend.exception.SignupNotFoundException;
import com.origem.backend.service.PaymentService;
import com.origem.backend.service.SignupService;
import com.stripe.exception.StripeException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/signups")
public class SignupController {

    private final SignupService signupService;
    private final PaymentService paymentService;

    public SignupController(SignupService signupService, PaymentService paymentService) {
        this.signupService = signupService;
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<SignupResponse> create(@Valid @RequestBody SignupRequest request) {
        Signup signup = signupService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(SignupResponse.from(signup));
    }

    @GetMapping("/{id}")
    public SignupResponse getById(@PathVariable UUID id, @RequestHeader("X-Access-Token") String accessToken) {
        return SignupResponse.from(signupService.getById(id, parseToken(id, accessToken)));
    }

    @PostMapping("/{id}/pay")
    public SignupResponse simulatePayment(@PathVariable UUID id, @RequestHeader("X-Access-Token") String accessToken) {
        return SignupResponse.from(paymentService.simulatePayment(id, parseToken(id, accessToken)));
    }

    @PostMapping("/{id}/payment-intent")
    public PaymentIntentResponse createPaymentIntent(@PathVariable UUID id,
                                                        @RequestHeader("X-Access-Token") String accessToken) throws StripeException {
        return paymentService.createPaymentIntent(id, parseToken(id, accessToken));
    }

    private static UUID parseToken(UUID signupId, String accessToken) {
        try {
            return UUID.fromString(accessToken);
        } catch (IllegalArgumentException e) {
            throw new SignupNotFoundException(signupId);
        }
    }
}
