package com.yowyob.common.exception;

import com.yowyob.common.constant.ErrorConstants;
import com.yowyob.common.dto.ValidationError;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Exception levée lors d'erreurs de validation
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 * Retourne un status HTTP 400 BAD_REQUEST
 * Contient la liste des erreurs de validation par champ
 */
@Getter
public class ValidationException extends AppException {

    private final List<ValidationError> validationErrors;

    public ValidationException(String message) {
        super(message, ErrorConstants.VALIDATION_FAILED, HttpStatus.BAD_REQUEST);
        this.validationErrors = new ArrayList<>();
    }

    public ValidationException(String message, List<ValidationError> validationErrors) {
        super(message, ErrorConstants.VALIDATION_FAILED, HttpStatus.BAD_REQUEST);
        this.validationErrors = validationErrors != null ? validationErrors : new ArrayList<>();
    }

    public ValidationException(String field, String message, Object rejectedValue) {
        super(
                String.format("Validation échouée pour le champ '%s': %s", field, message),
                ErrorConstants.VALIDATION_FAILED,
                HttpStatus.BAD_REQUEST
        );
        this.validationErrors = new ArrayList<>();
        this.validationErrors.add(ValidationError.of(field, message, rejectedValue));
    }

    public ValidationException addValidationError(String field, String message) {
        this.validationErrors.add(ValidationError.of(field, message));
        return this;
    }

    public ValidationException addValidationError(String field, String message, Object rejectedValue) {
        this.validationErrors.add(ValidationError.of(field, message, rejectedValue));
        return this;
    }
}