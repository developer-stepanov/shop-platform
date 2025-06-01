package com.stepanov.controller;

import com.stepanov.configuration.StripeConfig;
import com.stepanov.entity.PaymentEntity;
import com.stepanov.enums.PaymentStatus;
import com.stepanov.repository.PaymentRepository;
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
import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/stripe/webhook")
@RequiredArgsConstructor
public class StripeWebhookController {

    private final PaymentRepository paymentRepository;

    private final StripeConfig stripeConfig;

    @PostMapping
    public ResponseEntity<Void> hook(HttpServletRequest request) throws IOException, StripeException {

        String sig = request.getHeader("Stripe-Signature");
        String body = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);

        Event event = Webhook.constructEvent(body, sig, stripeConfig.getWebhookSecret());

        if ("checkout.session.completed".equals(event.getType())) {

            Session s = (Session) event.getDataObjectDeserializer().getObject().orElseThrow();
            UUID orderId = UUID.fromString(s.getMetadata().get("orderId"));

            paymentRepository.findByStripeSessionId(s.getId()).ifPresent(p -> {
                if (p.getPaymentStatus() == PaymentStatus.SUCCEEDED) return;   // idempotent

                p.setPaymentStatus(PaymentStatus.SUCCEEDED);
                p.setStripePaymentIntent(s.getPaymentIntent());
                p.setUpdatedAt(Instant.now());
                paymentRepository.save(p);

//                kafka.send("payment-result.v1",
//                        orderId.toString(),
//                        new PaymentSucceeded(orderId));
            });
        }
        return ResponseEntity.ok().build();
    }
}
