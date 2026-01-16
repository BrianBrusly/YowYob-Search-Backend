package com.yowyob.common.util.validation;

import com.yowyob.common.constant.RegexPatterns;
import com.yowyob.common.util.string.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Validateur avancé d'adresses email
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 * Validation RFC 5322 compliant avec options de blacklist
 * et normalisation des adresses email
 */
@Slf4j
@Component
public class EmailValidator {

    private static final Set<String> DISPOSABLE_EMAIL_DOMAINS = Set.of(
            "tempmail.com", "throwaway.email", "guerrillamail.com",
            "mailinator.com", "10minutemail.com", "maildrop.cc"
    );

    public boolean isValid(String email) {
        if (StringUtils.isEmpty(email)) {
            return false;
        }

        if (!RegexPatterns.EMAIL_PATTERN.matcher(email).matches()) {
            return false;
        }

        String domain = extractDomain(email);
        return !isDisposableEmail(domain);
    }

    public boolean isValidStrict(String email) {
        if (!isValid(email)) {
            return false;
        }

        String[] parts = email.split("@");
        if (parts.length != 2) {
            return false;
        }

        String localPart = parts[0];
        String domain = parts[1];

        if (localPart.length() > 64 || domain.length() > 255) {
            return false;
        }

        if (localPart.startsWith(".") || localPart.endsWith(".") || localPart.contains("..")) {
            return false;
        }

        return true;
    }

    public String normalize(String email) {
        if (StringUtils.isEmpty(email)) {
            return email;
        }

        email = email.trim().toLowerCase();

        String[] parts = email.split("@");
        if (parts.length != 2) {
            return email;
        }

        String localPart = parts[0];
        String domain = parts[1];

        if (domain.equals("gmail.com") || domain.equals("googlemail.com")) {
            localPart = localPart.replace(".", "");
            int plusIndex = localPart.indexOf('+');
            if (plusIndex > 0) {
                localPart = localPart.substring(0, plusIndex);
            }
            domain = "gmail.com";
        }

        return localPart + "@" + domain;
    }

    public String extractDomain(String email) {
        if (StringUtils.isEmpty(email)) {
            return null;
        }

        int atIndex = email.lastIndexOf('@');
        if (atIndex < 0 || atIndex == email.length() - 1) {
            return null;
        }

        return email.substring(atIndex + 1).toLowerCase();
    }

    public boolean isDisposableEmail(String domain) {
        if (StringUtils.isEmpty(domain)) {
            return false;
        }
        return DISPOSABLE_EMAIL_DOMAINS.contains(domain.toLowerCase());
    }

    public boolean isCorporateEmail(String email) {
        String domain = extractDomain(email);
        if (domain == null) {
            return false;
        }

        Set<String> personalDomains = Set.of(
                "gmail.com", "yahoo.com", "hotmail.com", "outlook.com",
                "icloud.com", "aol.com", "mail.com"
        );

        return !personalDomains.contains(domain.toLowerCase());
    }
}