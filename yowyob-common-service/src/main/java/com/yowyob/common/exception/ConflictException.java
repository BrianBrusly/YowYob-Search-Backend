package com.yowyob.common.exception;

import com.yowyob.common.constant.ErrorConstants;
import org.springframework.http.HttpStatus;

/**
 * Exception levée lors d'un conflit de données
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 * Retourne un status HTTP 409 CONFLICT
 * Utilisée pour les duplications, violations de contraintes, etc.
 */
public class ConflictException extends AppException {

    public ConflictException(String message) {
        super(message, ErrorConstants.RESOURCE_CONFLICT, HttpStatus.CONFLICT);
    }

    public ConflictException(String message, Throwable cause) {
        super(message, cause, ErrorConstants.RESOURCE_CONFLICT, HttpStatus.CONFLICT);
    }

    public ConflictException(String resourceName, String field, Object value) {
        super(
                String.format("%s avec %s='%s' existe déjà", resourceName, field, value),
                ErrorConstants.RESOURCE_ALREADY_EXISTS,
                HttpStatus.CONFLICT
        );
        this.withDetail("resourceName", resourceName);
        this.withDetail("field", field);
        this.withDetail("value", value);
    }
}