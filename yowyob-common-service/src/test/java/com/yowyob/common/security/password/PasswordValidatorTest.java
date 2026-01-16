package com.yowyob.common.security.password;

import com.yowyob.common.dto.ValidationError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests pour PasswordValidator
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 */
@DisplayName("PasswordValidator Tests")
class PasswordValidatorTest {

    private PasswordValidator passwordValidator;

    @BeforeEach
    void setUp() {
        passwordValidator = new PasswordValidator();
    }

    @Test
    @DisplayName("Devrait valider un mot de passe fort")
    void shouldValidateStrongPassword() {
        String password = "MyS3cur3P@ssw0rd";

        List<ValidationError> errors = passwordValidator.validate(password);

        assertThat(errors).isEmpty();
        assertThat(passwordValidator.isValid(password)).isTrue();
    }

    @Test
    @DisplayName("Devrait détecter un mot de passe trop court")
    void shouldDetectTooShortPassword() {
        String password = "Short1!";List<ValidationError> errors = passwordValidator.validate(password);

        assertThat(errors).isNotEmpty();
        assertThat(errors).anyMatch(e -> e.getMessage().contains("au moins"));
    }

    @Test
    @DisplayName("Devrait détecter l'absence de majuscule")
    void shouldDetectMissingUppercase() {
        String password = "mypassw0rd!";

        List<ValidationError> errors = passwordValidator.validate(password);

        assertThat(errors).anyMatch(e -> e.getMessage().contains("majuscule"));
    }

    @Test
    @DisplayName("Devrait détecter l'absence de minuscule")
    void shouldDetectMissingLowercase() {
        String password = "MYPASSW0RD!";

        List<ValidationError> errors = passwordValidator.validate(password);

        assertThat(errors).anyMatch(e -> e.getMessage().contains("minuscule"));
    }

    @Test
    @DisplayName("Devrait détecter l'absence de chiffre")
    void shouldDetectMissingDigit() {
        String password = "MyPassword!";

        List<ValidationError> errors = passwordValidator.validate(password);

        assertThat(errors).anyMatch(e -> e.getMessage().contains("chiffre"));
    }

    @Test
    @DisplayName("Devrait détecter l'absence de caractère spécial")
    void shouldDetectMissingSpecialChar() {
        String password = "MyPassw0rd";

        List<ValidationError> errors = passwordValidator.validate(password);

        assertThat(errors).anyMatch(e -> e.getMessage().contains("caractère spécial"));
    }

    @Test
    @DisplayName("Devrait détecter un mot de passe commun")
    void shouldDetectCommonPassword() {
        String password = "password";

        List<ValidationError> errors = passwordValidator.validate(password);

        assertThat(errors).anyMatch(e -> e.getMessage().contains("trop commun"));
    }

    @Test
    @DisplayName("Devrait calculer la force du mot de passe")
    void shouldCalculatePasswordStrength() {
        String weakPassword = "password";
        String strongPassword = "MyS3cur3P@ssw0rd!2024";

        int weakStrength = passwordValidator.calculateStrength(weakPassword);
        int strongStrength = passwordValidator.calculateStrength(strongPassword);

        assertThat(weakStrength).isLessThan(strongStrength);
        assertThat(strongStrength).isGreaterThan(80);
    }

    @Test
    @DisplayName("Devrait retourner erreur pour mot de passe null")
    void shouldReturnErrorForNullPassword() {
        List<ValidationError> errors = passwordValidator.validate(null);

        assertThat(errors).isNotEmpty();
        assertThat(errors.get(0).getMessage()).contains("requis");
    }
}