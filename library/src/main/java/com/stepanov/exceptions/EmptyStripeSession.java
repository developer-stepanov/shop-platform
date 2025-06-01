package com.stepanov.exceptions;

public class EmptyStripeSession extends RuntimeException {
    public EmptyStripeSession(String message) {
        super(message);
    }
}
