package com.yowyob.common.constant;

/**
 * Constantes de sécurité pour la plateforme
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 * Centralise les paramètres de sécurité JWT, mots de passe,
 * et autres aspects de la sécurité applicative
 */
public final class SecurityConstants {

    private SecurityConstants() {
        throw new UnsupportedOperationException("Cette classe utilitaire ne peut pas être instanciée");
    }

    public static final long JWT_ACCESS_TOKEN_EXPIRATION_MS = 900000L;
    public static final long JWT_REFRESH_TOKEN_EXPIRATION_MS = 604800000L;

    public static final String JWT_ISSUER = "yowyob-search";
    public static final String JWT_AUDIENCE = "yowyob-clients";
    public static final String JWT_CLAIM_USER_ID = "userId";
    public static final String JWT_CLAIM_ROLES = "roles";
    public static final String JWT_CLAIM_EMAIL = "email";
    public static final String JWT_CLAIM_USERNAME = "username";

    public static final int BCRYPT_STRENGTH = 12;

    public static final int PASSWORD_MIN_LENGTH = 8;
    public static final int PASSWORD_MAX_LENGTH = 128;
    public static final boolean PASSWORD_REQUIRE_UPPERCASE = true;
    public static final boolean PASSWORD_REQUIRE_LOWERCASE = true;
    public static final boolean PASSWORD_REQUIRE_DIGIT = true;
    public static final boolean PASSWORD_REQUIRE_SPECIAL_CHAR = true;

    public static final String ROLE_PREFIX = "ROLE_";
    public static final String ROLE_USER = ROLE_PREFIX + "USER";
    public static final String ROLE_ADMIN = ROLE_PREFIX + "ADMIN";
    public static final String ROLE_MERCHANT = ROLE_PREFIX + "MERCHANT";
    public static final String ROLE_MODERATOR = ROLE_PREFIX + "MODERATOR";

    public static final String PERMISSION_PREFIX = "PERMISSION_";
    public static final String PERMISSION_READ = PERMISSION_PREFIX + "READ";
    public static final String PERMISSION_WRITE = PERMISSION_PREFIX + "WRITE";
    public static final String PERMISSION_DELETE = PERMISSION_PREFIX + "DELETE";
    public static final String PERMISSION_ADMIN = PERMISSION_PREFIX + "ADMIN";

    public static final String[] PUBLIC_ENDPOINTS = {
            "/api/v1/auth/login",
            "/api/v1/auth/register",
            "/api/v1/auth/refresh",
            "/api/v1/health",
            "/actuator/**"
    };

    public static final String AES_ALGORITHM = "AES";
    public static final String AES_TRANSFORMATION = "AES/GCM/NoPadding";
    public static final int AES_KEY_SIZE = 256;
    public static final int GCM_TAG_LENGTH = 128;
    public static final int GCM_IV_LENGTH = 12;

    public static final String RSA_ALGORITHM = "RSA";
    public static final int RSA_KEY_SIZE = 2048;
}