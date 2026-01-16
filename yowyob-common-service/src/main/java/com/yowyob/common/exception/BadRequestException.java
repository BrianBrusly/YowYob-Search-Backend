package com.yowyob.common.exception;

import com.yowyob.common.constant.ErrorConstants;
import org.springframework.http.HttpStatus;

/**
 * Exception levée pour une requête invalide
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 * Retourne un status HTTP 400 BAD_REQUEST
 * Utilisée pour signaler des paramètres ou données invalides
 */
public class BadRequestException extends AppException {

    public BadRequestException(String message) {
        super(message, ErrorConstants.VALIDATION_FAILED, HttpStatus.BAD_REQUEST);
    }

    public BadRequestException(String message, String errorCode) {
        super(message, errorCode, HttpStatus.BAD_REQUEST);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause, ErrorConstants.VALIDATION_FAILED, HttpStatus.BAD_REQUEST);
    }
}