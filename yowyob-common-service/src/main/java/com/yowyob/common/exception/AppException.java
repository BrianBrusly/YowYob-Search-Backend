package com.yowyob.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * Exception de base pour toutes les exceptions métier
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 * Classe abstraite dont héritent toutes les exceptions personnalisées
 * Permet d'enrichir les exceptions avec code erreur et détails
 */
@Getter
public abstract class AppException extends RuntimeException {

    private final String errorCode;
    private final HttpStatus httpStatus;
    private final Map<String, Object> details;

    protected AppException(String message, String errorCode, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.details = new HashMap<>();
    }

    protected AppException(String message, Throwable cause, String errorCode, HttpStatus httpStatus) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.details = new HashMap<>();
    }

    public AppException withDetail(String key, Object value) {
        this.details.put(key, value);
        return this;
    }

    public AppException withDetails(Map<String, Object> details) {
        this.details.putAll(details);
        return this;
    }
}