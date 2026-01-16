package com.yowyob.common.security.password;

import com.yowyob.common.constant.RegexPatterns;
import com.yowyob.common.constant.SecurityConstants;
import com.yowyob.common.dto.ValidationError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Validateur de mots de passe
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 * Vérifie la complexité des mots de passe selon les règles de sécurité
 * Retourne des messages d'erreur détaillés pour chaque règle non respectée
 */
@Slf4j
@Component
public class PasswordValidator {

    private static final Set<String> COMMON_PASSWORDS = Set.of(
            "password", "123456", "12345678", "qwerty", "abc123",
            "monkey", "1234567", "letmein", "trustno1", "dragon",
            "baseball", "111111", "iloveyou", "master", "sunshine"
    );

    public List<ValidationError> validate(String password) {
        List<ValidationError> errors = new ArrayList<>();

        if (password == null || password.isEmpty()) {
            errors.add(ValidationError.of("password", "Le mot de passe est requis"));
            return errors;
        }

        if (password.length() < SecurityConstants.PASSWORD_MIN_LENGTH) {
            errors.add(ValidationError.of(
                    "password",
                    String.format("Le mot de passe doit contenir au moins %d caractères",
                            SecurityConstants.PASSWORD_MIN_LENGTH)
            ));
        }

        if (password.length() > SecurityConstants.PASSWORD_MAX_LENGTH) {
            errors.add(ValidationError.of(
                    "password",
                    String.format("Le mot de passe ne doit pas dépasser %d caractères",
                            SecurityConstants.PASSWORD_MAX_LENGTH)
            ));
        }

        if (SecurityConstants.PASSWORD_REQUIRE_UPPERCASE &&
                !RegexPatterns.PASSWORD_UPPERCASE_PATTERN.matcher(password).matches()) {
            errors.add(ValidationError.of(
                    "password",
                    "Le mot de passe doit contenir au moins une lettre majuscule"
            ));
        }

        if (SecurityConstants.PASSWORD_REQUIRE_LOWERCASE &&
                !RegexPatterns.PASSWORD_LOWERCASE_PATTERN.matcher(password).matches()) {
            errors.add(ValidationError.of(
                    "password",
                    "Le mot de passe doit contenir au moins une lettre minuscule"
            ));
        }

        if (SecurityConstants.PASSWORD_REQUIRE_DIGIT &&
                !RegexPatterns.PASSWORD_DIGIT_PATTERN.matcher(password).matches()) {
            errors.add(ValidationError.of(
                    "password",
                    "Le mot de passe doit contenir au moins un chiffre"
            ));
        }

        if (SecurityConstants.PASSWORD_REQUIRE_SPECIAL_CHAR &&
                !RegexPatterns.PASSWORD_SPECIAL_CHAR_PATTERN.matcher(password).matches()) {
            errors.add(ValidationError.of(
                    "password",
                    "Le mot de passe doit contenir au moins un caractère spécial"
            ));
        }

        if (COMMON_PASSWORDS.contains(password.toLowerCase())) {
            errors.add(ValidationError.of(
                    "password",
                    "Ce mot de passe est trop commun, veuillez en choisir un autre"
            ));
        }

        return errors;
    }

    public boolean isValid(String password) {
        return validate(password).isEmpty();
    }

    public int calculateStrength(String password) {
        if (password == null || password.isEmpty()) {
            return 0;
        }

        int strength = 0;

        if (password.length() >= 8) strength += 20;
        if (password.length() >= 12) strength += 10;
        if (password.length() >= 16) strength += 10;

        if (RegexPatterns.PASSWORD_LOWERCASE_PATTERN.matcher(password).matches()) strength += 15;
        if (RegexPatterns.PASSWORD_UPPERCASE_PATTERN.matcher(password).matches()) strength += 15;
        if (RegexPatterns.PASSWORD_DIGIT_PATTERN.matcher(password).matches()) strength += 15;
        if (RegexPatterns.PASSWORD_SPECIAL_CHAR_PATTERN.matcher(password).matches()) strength += 15;

        return Math.min(strength, 100);
    }
}