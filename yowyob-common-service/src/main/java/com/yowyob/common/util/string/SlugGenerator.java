package com.yowyob.common.util.string;

import lombok.extern.slf4j.Slf4j;

import java.text.Normalizer;
import java.util.Locale;
import java.util.UUID;

/**
 * Générateur de slugs URL-friendly
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 *          Transforme des chaînes arbitraires en slugs valides pour URLs
 *          Gère la translittération des accents et caractères spéciaux
 */
@Slf4j
public final class SlugGenerator {

    private SlugGenerator() {
        throw new UnsupportedOperationException("Cette classe utilitaire ne peut pas être instanciée");
    }

    public static String toSlug(String text) {
        if (StringUtils.isEmpty(text)) {
            return generateRandomSlug();
        }

        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        String withoutAccents = normalized.replaceAll("\\p{M}", "");

        String slug = withoutAccents.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");

        return slug.isEmpty() ? generateRandomSlug() : slug;
    }

    public static String toSlug(String text, int maxLength) {
        String slug = toSlug(text);
        if (slug.length() <= maxLength) {
            return slug;
        }
        return slug.substring(0, maxLength).replaceAll("-$", "");
    }

    public static String uniqueSlug(String text) {
        String baseSlug = toSlug(text);
        String uniqueSuffix = UUID.randomUUID().toString().substring(0, 8);
        return baseSlug + "-" + uniqueSuffix;
    }

    public static String uniqueSlug(String text, String separator) {
        String baseSlug = toSlug(text);
        String uniqueSuffix = UUID.randomUUID().toString().substring(0, 8);
        return baseSlug + separator + uniqueSuffix;
    }

    private static String generateRandomSlug() {
        return "slug-" + UUID.randomUUID().toString().substring(0, 8);
    }

    public static boolean isValidSlug(String slug) {
        if (StringUtils.isEmpty(slug)) {
            return false;
        }
        return slug.matches("^[a-z0-9]+(?:-[a-z0-9]+)*$");
    }
}