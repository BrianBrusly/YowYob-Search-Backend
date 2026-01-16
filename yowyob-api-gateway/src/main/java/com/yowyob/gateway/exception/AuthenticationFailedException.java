package com.yowyob.gateway.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception pour les échecs d'authentification
 * 
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 */
public class AuthenticationFailedException extends GatewayException {

    private final String reason;

    public AuthenticationFailedException(String route, String reason) {
        super(
                HttpStatus.UNAUTHORIZED,
                "AUTHENTICATION_FAILED",
                String.format("Authentication failed: %s", reason),
                route,
                "api-gateway");
        this.reason = reason;
    }

    public AuthenticationFailedException(String route, String reason, Throwable cause) {
        super(
                HttpStatus.UNAUTHORIZED,
                "AUTHENTICATION_FAILED",
                String.format("Authentication failed: %s", reason),
                route,
                "api-gateway",
                cause);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}