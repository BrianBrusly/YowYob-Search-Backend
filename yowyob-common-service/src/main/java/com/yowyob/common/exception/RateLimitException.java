package com.yowyob.common.exception;

import com.yowyob.common.constant.ErrorConstants;
import org.springframework.http.HttpStatus;

/**
 * Exception levée quand la limite de taux est dépassée
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 * Retourne un status HTTP 429 TOO_MANY_REQUESTS
 * Utilisée par le système de rate limiting
 */
public class RateLimitException extends AppException {

    public RateLimitException(String message) {
        super(message, ErrorConstants.RATE_LIMIT_EXCEEDED, HttpStatus.TOO_MANY_REQUESTS);
    }

    public RateLimitException(int limit, long retryAfterSeconds) {
        super(
                String.format("Limite de %d requêtes dépassée. Réessayez dans %d secondes", limit, retryAfterSeconds),
                ErrorConstants.RATE_LIMIT_EXCEEDED,
                HttpStatus.TOO_MANY_REQUESTS
        );
        this.withDetail("limit", limit);
        this.withDetail("retryAfterSeconds", retryAfterSeconds);
    }
}