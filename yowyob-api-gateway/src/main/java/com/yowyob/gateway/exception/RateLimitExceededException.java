package com.yowyob.gateway.exception;

import org.springframework.http.HttpStatus;

import java.time.Instant;

/**
 * Exception pour les limites de taux dépassées
 * 
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 */
public class RateLimitExceededException extends GatewayException {

    private final int limit;
    private final int remaining;
    private final Instant resetAt;
    private final int retryAfterSeconds;

    public RateLimitExceededException(String route, String key, int limit, Instant resetAt) {
        super(
                HttpStatus.TOO_MANY_REQUESTS,
                "RATE_LIMIT_EXCEEDED",
                String.format("Rate limit exceeded for key '%s'. Limit: %d requests", key, limit),
                route,
                "api-gateway");
        this.limit = limit;
        this.remaining = 0;
        this.resetAt = resetAt;
        this.retryAfterSeconds = (int) (resetAt.getEpochSecond() - Instant.now().getEpochSecond());
    }

    public RateLimitExceededException(String route, String key, int limit, int remaining, Instant resetAt) {
        super(
                HttpStatus.TOO_MANY_REQUESTS,
                "RATE_LIMIT_EXCEEDED",
                String.format("Rate limit exceeded for key '%s'. Limit: %d requests, Remaining: %d",
                        key, limit, remaining),
                route,
                "api-gateway");
        this.limit = limit;
        this.remaining = remaining;
        this.resetAt = resetAt;
        this.retryAfterSeconds = (int) (resetAt.getEpochSecond() - Instant.now().getEpochSecond());
    }

    public int getLimit() {
        return limit;
    }

    public int getRemaining() {
        return remaining;
    }

    public Instant getResetAt() {
        return resetAt;
    }

    public int getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}