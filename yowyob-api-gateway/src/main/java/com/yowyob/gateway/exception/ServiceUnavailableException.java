package com.yowyob.gateway.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception pour les services indisponibles
 * 
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 */
public class ServiceUnavailableException extends GatewayException {

    private final int retryAfterSeconds;

    public ServiceUnavailableException(String service, String route) {
        super(
                HttpStatus.SERVICE_UNAVAILABLE,
                "SERVICE_UNAVAILABLE",
                String.format("Service '%s' is temporarily unavailable", service),
                route,
                service);
        this.retryAfterSeconds = 30; // Valeur par défaut
    }

    public ServiceUnavailableException(String service, String route, int retryAfterSeconds) {
        super(
                HttpStatus.SERVICE_UNAVAILABLE,
                "SERVICE_UNAVAILABLE",
                String.format("Service '%s' is temporarily unavailable. Please retry after %d seconds",
                        service, retryAfterSeconds),
                route,
                service);
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public ServiceUnavailableException(String service, String route, Throwable cause) {
        super(
                HttpStatus.SERVICE_UNAVAILABLE,
                "SERVICE_UNAVAILABLE",
                String.format("Service '%s' is temporarily unavailable", service),
                route,
                service,
                cause);
        this.retryAfterSeconds = 30;
    }

    public int getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}