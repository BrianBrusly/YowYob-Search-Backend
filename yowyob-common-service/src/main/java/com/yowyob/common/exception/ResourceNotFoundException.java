package com.yowyob.common.exception;

import com.yowyob.common.constant.ErrorConstants;
import org.springframework.http.HttpStatus;

/**
 * Exception levée quand une ressource demandée n'existe pas
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 * Retourne un status HTTP 404 NOT_FOUND
 * Utilisée pour signaler qu'une ressource demandée est introuvable
 */
public class ResourceNotFoundException extends AppException {

    public ResourceNotFoundException(String resourceName, String resourceId) {
        super(
                String.format("%s avec l'id '%s' n'a pas été trouvé", resourceName, resourceId),
                ErrorConstants.RESOURCE_NOT_FOUND,
                HttpStatus.NOT_FOUND
        );
        this.withDetail("resourceName", resourceName);
        this.withDetail("resourceId", resourceId);
    }

    public ResourceNotFoundException(String message) {
        super(message, ErrorConstants.RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause, ErrorConstants.RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND);
    }
}