package com.origem.backend.service;

import com.origem.backend.config.AppProperties;
import com.origem.backend.config.StripeProperties;
import com.origem.backend.domain.Signup;
import com.origem.backend.domain.SignupStatus;
import com.origem.backend.dto.PaymentIntentResponse;
import com.origem.backend.exception.InvalidPaymentModeException;
import com.origem.backend.exception.SignupNotFoundException;
import com.origem.backend.repository.SignupRepository;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final SignupRepository signupRepository;
    private final AppProperties appProperties;
    private final StripeProperties stripeProperties;
    private final EmailService emailService;

    public PaymentService(SignupRepository signupRepository,
                           AppProperties appProperties,
                           StripeProperties stripeProperties,
                           EmailService emailService) {
        this.signupRepository = signupRepository;
        this.appProperties = appProperties;
        this.stripeProperties = stripeProperties;
        this.emailService = emailService;
    }

    /**
     * Modo demo: reproduz o botão "Simular pagamento" do protótipo, sem Stripe.
     */
    @Transactional
    public Signup simulatePayment(UUID signupId) {
        requireDemoMode(true, "Modo demo desativado — use /payment-intent com Stripe Elements.");

        Signup signup = signupRepository.findById(signupId)
                .orElseThrow(() -> new SignupNotFoundException(signupId));

        signup.setStatus(SignupStatus.PAID);
        signup.setPaidAt(Instant.now());
        signupRepository.save(signup);

        emailService.sendPaymentConfirmation(signup);
        return signup;
    }

    /**
     * Modo real: cria um PaymentIntent no Stripe para o front confirmar via Stripe Elements.
     */
    @Transactional
    public PaymentIntentResponse createPaymentIntent(UUID signupId) throws StripeException {
        requireDemoMode(false, "Modo demo ativo — use /pay para simular o pagamento.");

        Signup signup = signupRepository.findById(signupId)
                .orElseThrow(() -> new SignupNotFoundException(signupId));

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(signup.getFoundingFeeAmountCents())
                .setCurrency(signup.getCurrency().toLowerCase())
                .putMetadata("signupId", signup.getId().toString())
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build())
                .build();

        PaymentIntent intent = PaymentIntent.create(params);
        signup.setStripePaymentIntentId(intent.getId());
        signupRepository.save(signup);

        return new PaymentIntentResponse(intent.getClientSecret(), signup.getFoundingFeeAmountCents(), signup.getCurrency());
    }

    /**
     * Modo real: chamado pelo webhook do Stripe quando o pagamento é confirmado.
     */
    @Transactional
    public void handleWebhookEvent(String payload, String sigHeader) throws SignatureVerificationException {
        Event event = Webhook.constructEvent(payload, sigHeader, stripeProperties.webhookSecret());

        if (!"payment_intent.succeeded".equals(event.getType())) {
            return;
        }

        StripeObject stripeObject = event.getDataObjectDeserializer().getObject().orElse(null);
        if (!(stripeObject instanceof PaymentIntent paymentIntent)) {
            log.warn("Webhook payment_intent.succeeded sem PaymentIntent deserializável");
            return;
        }

        signupRepository.findByStripePaymentIntentId(paymentIntent.getId()).ifPresentOrElse(signup -> {
            if (signup.getStatus() == SignupStatus.PAID) {
                return;
            }
            signup.setStatus(SignupStatus.PAID);
            signup.setPaidAt(Instant.now());
            signupRepository.save(signup);
            emailService.sendPaymentConfirmation(signup);
        }, () -> log.warn("Nenhum signup encontrado para PaymentIntent {}", paymentIntent.getId()));
    }

    private void requireDemoMode(boolean expected, String message) {
        if (appProperties.payments().demoMode() != expected) {
            throw new InvalidPaymentModeException(message);
        }
    }
}
