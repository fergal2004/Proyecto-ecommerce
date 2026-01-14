package com.ecommerce.ads.keycloakAdapter.exception;

import org.springframework.http.HttpStatus;

public class BussinesRuleException extends Exception {
    private String code;
    private HttpStatus status;

    public BussinesRuleException(String code, String message, HttpStatus status) {
        super(message);
        this.code = code;
        this.status = status;
    }

    public String getCode() { return code; }
    public HttpStatus getStatus() { return status; }
}