package com.redhat.service;

public class AuthorizationException extends RuntimeException {

    public AuthorizationException(String message) {
        super(message);
    }
}
