package com.yowyob.common.util.string;

import lombok.extern.slf4j.Slf4j;

import java.util.regex.Pattern;

/**
 * Nettoyage et sanitisation de texte
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 *
 * Protège contre XSS et injection SQL
 * Nettoie les entrées utilisateur avant stockage ou affichage
 */
@Slf4j
public final class TextSanitizer {

    private TextSanitizer() {
        throw new UnsupportedOperationException("Cette classe utilitaire ne peut pas être instanciée");
    }

    private static final Pattern HTML_TAG_PATTERN = Pattern.compile("<[^>]*>");
    private static final Pattern SCRIPT_PATTERN = Pattern.compile("<script[^>]*>.*?</script>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile("('.+--)|(--)|(;)|(\\|\\|)|(\\*)", Pattern.CASE_INSENSITIVE);

    public static String sanitizeHtml(String html) {
        if (StringUtils.isEmpty(html)) {
            return html;
        }

        String sanitized = SCRIPT_PATTERN.matcher(html).replaceAll("");
        sanitized = sanitized.replaceAll("<iframe[^>]*>.*?</iframe>", "");
        sanitized = sanitized.replaceAll("javascript:", "");
        sanitized = sanitized.replaceAll("on\\w+\\s*=", "");

        return sanitized;
    }

    public static String stripHtml(String html) {
        if (StringUtils.isEmpty(html)) {
            return html;
        }
        return HTML_TAG_PATTERN.matcher(html).replaceAll("");
    }

    public static String escapeHtml(String text) {
        if (StringUtils.isEmpty(text)) {
            return text;
        }

        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;")
                .replace("/", "&#x2F;");
    }

    public static String sanitizeSql(String input) {
        if (StringUtils.isEmpty(input)) {
            return input;
        }

        String sanitized = input.replace("'", "''");

        if (SQL_INJECTION_PATTERN.matcher(sanitized).find()) {
            log.warn("Tentative d'injection SQL détectée: {}", input);
            return "";
        }

        return sanitized;
    }

    public static String removeSpecialChars(String text) {
        if (StringUtils.isEmpty(text)) {
            return text;
        }
        return text.replaceAll("[^a-zA-Z0-9\\s]", "");
    }

    public static String removeControlChars(String text) {
        if (StringUtils.isEmpty(text)) {
            return text;
        }
        return text.replaceAll("\\p{Cntrl}", "");
    }

    public static String sanitizeFilename(String filename) {
        if (StringUtils.isEmpty(filename)) {
            return filename;
        }

        String sanitized = filename.replaceAll("[^a-zA-Z0-9._-]", "_");
        sanitized = sanitized.replaceAll("_{2,}", "_");

        return sanitized;
    }

    public static String truncateAndSanitize(String text, int maxLength) {
        if (text == null) {
            return null;
        }

        String sanitized = sanitizeHtml(text);
        sanitized = removeControlChars(sanitized);
        sanitized = StringUtils.normalizeWhitespace(sanitized);

        return StringUtils.truncate(sanitized, maxLength);
    }
}