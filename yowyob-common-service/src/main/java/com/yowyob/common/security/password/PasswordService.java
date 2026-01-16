package com.yowyob.common.security.password;

import com.yowyob.common.constant.SecurityConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service de gestion des mots de passe
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 * Hache et vérifie les mots de passe avec BCrypt
 * Thread-safe et sécurisé pour production
 */
@Slf4j
@Service
public class PasswordService {

    private final PasswordEncoder passwordEncoder;

    public PasswordService() {
        this.passwordEncoder = new BCryptPasswordEncoder(SecurityConstants.BCRYPT_STRENGTH);
    }

    public String hashPassword(String rawPassword) {
        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new IllegalArgumentException("Le mot de passe ne peut pas être vide");
        }

        log.debug("Hachage du mot de passe");
        return passwordEncoder.encode(rawPassword);
    }

    public boolean verifyPassword(String rawPassword, String hashedPassword) {
        if (rawPassword == null || hashedPassword == null) {
            return false;
        }

        return passwordEncoder.matches(rawPassword, hashedPassword);
    }

    public boolean needsRehash(String hashedPassword) {
        return !hashedPassword.startsWith("$2a$" + SecurityConstants.BCRYPT_STRENGTH);
    }
}