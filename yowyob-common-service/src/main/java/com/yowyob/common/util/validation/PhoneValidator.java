package com.yowyob.common.util.validation;

import com.yowyob.common.constant.RegexPatterns;
import com.yowyob.common.util.string.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Validateur de numéros de téléphone
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 * Supporte le format international E.164 et formats locaux
 * Spécialisé pour les numéros camerounais
 */
@Slf4j
@Component
public class PhoneValidator {

    public boolean isValid(String phone) {
        if (StringUtils.isEmpty(phone)) {
            return false;
        }

        String normalized = normalize(phone);
        return RegexPatterns.PHONE_PATTERN.matcher(normalized).matches();
    }

    public boolean isValidCameroonPhone(String phone) {
        if (StringUtils.isEmpty(phone)) {
            return false;
        }

        String normalized = normalize(phone);
        return RegexPatterns.CAMEROON_PHONE_PATTERN.matcher(normalized).matches();
    }

    public String normalize(String phone) {
        if (StringUtils.isEmpty(phone)) {
            return phone;
        }

        String normalized = phone.replaceAll("[\\s\\-\\(\\)]", "");

        if (normalized.startsWith("00")) {
            normalized = "+" + normalized.substring(2);
        }

        if (normalized.startsWith("237") && !normalized.startsWith("+")) {
            normalized = "+" + normalized;
        }

        return normalized;
    }

    public String normalizeCameroonPhone(String phone) {
        if (StringUtils.isEmpty(phone)) {
            return phone;
        }

        String normalized = normalize(phone);

        if (normalized.startsWith("+237")) {
            return normalized;
        }

        if (normalized.startsWith("237")) {
            return "+" + normalized;
        }

        if (normalized.startsWith("6") && normalized.length() == 9) {
            return "+237" + normalized;
        }

        return normalized;
    }

    public String formatCameroonPhone(String phone) {
        String normalized = normalizeCameroonPhone(phone);

        if (normalized.startsWith("+237") && normalized.length() == 13) {
            return String.format("+237 %s %s %s %s",
                    normalized.substring(4, 5),
                    normalized.substring(5, 7),
                    normalized.substring(7, 9),
                    normalized.substring(9, 13));
        }

        return normalized;
    }

    public boolean isMobileNumber(String phone) {
        String normalized = normalizeCameroonPhone(phone);

        if (normalized.startsWith("+237")) {
            String prefix = normalized.substring(4, 5);
            return "6".equals(prefix);
        }

        return false;
    }
}