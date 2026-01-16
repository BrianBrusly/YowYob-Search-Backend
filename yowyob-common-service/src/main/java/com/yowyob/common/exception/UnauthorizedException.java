package com.yowyob.common.exception;

import com.yowyob.common.constant.ErrorConstants;
import org.springframework.http.HttpStatus;

/**
 * Exception levée lors d'échec d'authentification
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 * Retourne un status HTTP 401 UNAUTHORIZED
 * Utilisée quand l'utilisateur n'est pas authentifié ou token invalide
 */
public class UnauthorizedException extends AppException {

    public UnauthorizedException(String message) {
        super(message, ErrorConstants.AUTH_INVALID_CREDENTIALS, HttpStatus.UNAUTHORIZED);
    }

    public UnauthorizedException(String message, String errorCode) {
        super(message, errorCode, HttpStatus.UNAUTHORIZED);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause, ErrorConstants.AUTH_INVALID_CREDENTIALS, HttpStatus.UNAUTHORIZED);
    }

    public static UnauthorizedException invalidToken() {
        return new UnauthorizedException(
                "Token d'authentification invalide",
                ErrorConstants.AUTH_INVALID_TOKEN
        );
    }

    public static UnauthorizedException expiredToken() {
        return new UnauthorizedException(
                "Token d'authentification expiré",
                ErrorConstants.AUTH_EXPIRED_TOKEN
        );
    }

    public static UnauthorizedException missingToken() {
        return new UnauthorizedException(
                "Token d'authentification manquant",
                ErrorConstants.AUTH_MISSING_TOKEN
        );
    }
}