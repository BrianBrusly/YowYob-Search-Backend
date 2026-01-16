package com.yowyob.common.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Validateur pour l'annotation ValidateEnum
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 * Vérifie qu'une valeur correspond à une constante enum
 */
public class EnumValidator implements ConstraintValidator<ValidateEnum, String> {

    private Set<String> acceptedValues;
    private boolean ignoreCase;

    @Override
    public void initialize(ValidateEnum annotation) {
        Class<? extends Enum<?>> enumClass = annotation.enumClass();
        this.ignoreCase = annotation.ignoreCase();

        this.acceptedValues = Arrays.stream(enumClass.getEnumConstants())
                .map(Enum::name)
                .map(name -> ignoreCase ? name.toLowerCase() : name)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        String valueToCheck = ignoreCase ? value.toLowerCase() : value;
        return acceptedValues.contains(valueToCheck);
    }
}