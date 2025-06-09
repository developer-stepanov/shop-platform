package com.stepanov.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/payment")
public class PaymentConfirmationController {

    @GetMapping("/confirmation")
    public String redirectToConfirmationPage() {
        return "redirect:/payment-confirmation.html";
    }

    @GetMapping("/failed")
    public String redirectToFailedPage() {
        return "redirect:/payment-failed.html";
    }

}
