package com.stepanov.controller;

import com.stepanov.configuration.StripeConfig;
import com.stepanov.service.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/stripe/webhook")
@RequiredArgsConstructor
public class StripeWebhookController {

    private final PaymentService paymentService;

    private final StripeConfig stripeConfig;

    @PostMapping
    public ResponseEntity<Void> webhook(HttpServletRequest request) throws IOException, StripeException {

        String sig = request.getHeader("Stripe-Signature");
        String body = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);

        Event event = Webhook.constructEvent(body, sig, stripeConfig.getWebhookSecret());

        Session session = (Session) event.getDataObjectDeserializer().getObject().orElseThrow();

        if ("checkout.session.completed".equals(event.getType())) {
            paymentService.markPaymentSucceeded(session);
        }
        return ResponseEntity.ok().build();
    }
}
