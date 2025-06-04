package com.stepanov.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class PaymentConfirmationController {

    @GetMapping("/payment/confirmation")
    public String redirectToConfirmationPage() {
        return "redirect:/payment-confirmation.html";
    }
}
