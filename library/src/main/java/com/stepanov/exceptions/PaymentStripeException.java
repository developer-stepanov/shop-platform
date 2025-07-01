package com.stepanov.exceptions;

public class PaymentStripeException extends RuntimeException {
    public PaymentStripeException(String message) {
        super(message);
    }
}
