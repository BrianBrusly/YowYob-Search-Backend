package com.yowyob.common.exception;

import com.yowyob.common.constant.ErrorConstants;
import org.springframework.http.HttpStatus;

/**
 * Exception levée lors d'accès non autorisé
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 * Retourne un status HTTP 403 FORBIDDEN
 * Utilisée quand l'utilisateur est authentifié mais n'a pas les permissions
 */
public class ForbiddenException extends AppException {

    public ForbiddenException(String message) {
        super(message, ErrorConstants.AUTH_INSUFFICIENT_PERMISSIONS, HttpStatus.FORBIDDEN);
    }

    public ForbiddenException(String message, Throwable cause) {
        super(message, cause, ErrorConstants.AUTH_INSUFFICIENT_PERMISSIONS, HttpStatus.FORBIDDEN);
    }

    public ForbiddenException(String resource, String action) {
        super(
                String.format("Accès refusé: vous n'avez pas la permission de %s sur %s", action, resource),
                ErrorConstants.AUTH_INSUFFICIENT_PERMISSIONS,
                HttpStatus.FORBIDDEN
        );
        this.withDetail("resource", resource);
        this.withDetail("action", action);
    }
}