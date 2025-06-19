package com.stepanov.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * MVC controller that handles the final step in a payment flow.
 *
 * <p>After a user successfully finishes the checkout process with the Stripe (Payment Service
 * Provider), the Stripe service calls <code>/payment/confirmation</code>.
 * The controller simply issues a redirect to a static confirmation page
 * (served from <code>resources/static/payment-confirmation.html</code>).</p>
 *
 * <h2>Routing</h2>
 * <ul>
 *   <li><strong>Inbound:</strong> <code>GET&nbsp;/payment/confirmation</code></li>
 *   <li><strong>Outbound:</strong> HTTP&nbsp;302&nbsp;→
 *       <code>/payment-confirmation.html</code></li>
 * </ul>
 *
 * <p>If you customise the Stripe route or move the static file, remember to update
 * the redirect target accordingly.</p>
 *
 * @author  Maxim Stepanov
 */

@Controller
@RequestMapping("/payment")
public class PaymentConfirmationController {

    @GetMapping("/confirmation")
    public String redirectToConfirmationPage() {
        return "redirect:/payment-confirmation.html";
    }

}
