package com.stepanov.controller;

import com.stepanov.configuration.StripeConfig;
import com.stepanov.service.PaymentService;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@RestController
@RequestMapping("/stripe/webhook")
@RequiredArgsConstructor
@Slf4j
public class StripeWebhookController {

    private static final String STRIPE_SIGNATURE = "Stripe-Signature";

    private static final String CHECKOUT_SESSION_COMPLETED = "checkout.session.completed";

    private final PaymentService paymentService;

    private final StripeConfig config;

    @PostMapping
    public ResponseEntity<Void> webhook(@NonNull HttpServletRequest request) throws IOException {
        final String sig = request.getHeader(STRIPE_SIGNATURE);
        final String body = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);

        try {
            final Event event = Webhook.constructEvent(body, sig, config.getWebhookSecret());
            final Optional<StripeObject> stripeObject = event.getDataObjectDeserializer().getObject();

            if (stripeObject.isEmpty()) {
                log.error("Failed to deserialize Stripe session object from event: {}", event.getId());
                return ResponseEntity.badRequest().build();
            }

            final Session session = (Session) stripeObject.get();

            log.info("Received Stripe webhook event: id [{}], type [{}]", event.getId(), event.getType());

            if (CHECKOUT_SESSION_COMPLETED.equals(event.getType())) {
                log.info("Processing 'checkout.session.completed' for session [{}]", session.getId());
                paymentService.markPaymentSucceeded(session);
            }
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            log.error("Webhook processing error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
