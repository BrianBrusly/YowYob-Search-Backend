package com.yowyob.common.exception;

import com.yowyob.common.constant.ErrorConstants;
import org.springframework.http.HttpStatus;

/**
 * Exception levée quand un service est temporairement indisponible
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 * Retourne un status HTTP 503 SERVICE_UNAVAILABLE
 * Utilisée pour les pannes temporaires de services externes
 */
public class ServiceUnavailableException extends AppException {

    public ServiceUnavailableException(String message) {
        super(message, ErrorConstants.SERVICE_UNAVAILABLE, HttpStatus.SERVICE_UNAVAILABLE);
    }

    public ServiceUnavailableException(String message, Throwable cause) {
        super(message, cause, ErrorConstants.SERVICE_UNAVAILABLE, HttpStatus.SERVICE_UNAVAILABLE);
    }

    public ServiceUnavailableException(String serviceName, String reason) {
        super(
                String.format("Le service '%s' est temporairement indisponible: %s", serviceName, reason),
                ErrorConstants.SERVICE_UNAVAILABLE,
                HttpStatus.SERVICE_UNAVAILABLE
        );
        this.withDetail("serviceName", serviceName);
        this.withDetail("reason", reason);
    }
}