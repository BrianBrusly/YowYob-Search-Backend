package com.yowyob.common.util.string;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests pour TextSanitizer
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 */
@DisplayName("TextSanitizer Tests")
class TextSanitizerTest {

    @Test
    @DisplayName("Devrait supprimer les balises script")
    void shouldRemoveScriptTags() {
        String html = "Hello <script>alert('xss')</script> World";

        String sanitized = TextSanitizer.sanitizeHtml(html);

        assertThat(sanitized).doesNotContain("<script>");
        assertThat(sanitized).doesNotContain("alert");
    }

    @Test
    @DisplayName("Devrait supprimer les balises iframe")
    void shouldRemoveIframeTags() {
        String html = "Hello <iframe src='evil.com'></iframe> World";

        String sanitized = TextSanitizer.sanitizeHtml(html);

        assertThat(sanitized).doesNotContain("<iframe>");
    }

    @Test
    @DisplayName("Devrait supprimer javascript dans les URLs")
    void shouldRemoveJavascriptUrls() {
        String html = "Click <a href='javascript:alert(1)'>here</a>";

        String sanitized = TextSanitizer.sanitizeHtml(html);

        assertThat(sanitized).doesNotContain("javascript:");
    }

    @Test
    @DisplayName("Devrait supprimer toutes les balises HTML")
    void shouldStripAllHtmlTags() {
        String html = "<p>Hello <b>World</b></p>";

        String stripped = TextSanitizer.stripHtml(html);

        assertThat(stripped).isEqualTo("Hello World");
        assertThat(stripped).doesNotContain("<");
        assertThat(stripped).doesNotContain(">");
    }

    @Test
    @DisplayName("Devrait échapper les caractères HTML")
    void shouldEscapeHtmlCharacters() {
        String text = "<script>alert('xss')</script>";

        String escaped = TextSanitizer.escapeHtml(text);

        assertThat(escaped).contains("&lt;script&gt;");
        assertThat(escaped).contains("&lt;&#x2F;script&gt;");
    }

    @Test
    @DisplayName("Devrait nettoyer les entrées SQL")
    void shouldSanitizeSqlInput() {
        String input = "admin' OR '1'='1";

        String sanitized = TextSanitizer.sanitizeSql(input);

        assertThat(sanitized).isEqualTo("admin'' OR ''1''=''1");
    }

    @Test
    @DisplayName("Devrait supprimer les caractères spéciaux")
    void shouldRemoveSpecialCharacters() {
        String text = "Hello@World#2024!";

        String cleaned = TextSanitizer.removeSpecialChars(text);

        assertThat(cleaned).isEqualTo("HelloWorld2024");
    }

    @Test
    @DisplayName("Devrait supprimer les caractères de contrôle")
    void shouldRemoveControlCharacters() {
        String text = "Hello\u0000\u0001World\u0002";

        String cleaned = TextSanitizer.removeControlChars(text);

        assertThat(cleaned).isEqualTo("HelloWorld");
    }

    @Test
    @DisplayName("Devrait nettoyer les noms de fichier")
    void shouldSanitizeFilename() {
        String filename = "my file/name\\test:2024.txt";

        String sanitized = TextSanitizer.sanitizeFilename(filename);

        assertThat(sanitized).doesNotContain("/");
        assertThat(sanitized).doesNotContain("\\");
        assertThat(sanitized).doesNotContain(":");
    }

    @Test
    @DisplayName("Devrait tronquer et nettoyer le texte")
    void shouldTruncateAndSanitize() {
        String text = "<p>This is a very long text with <script>alert('xss')</script> content</p>";

        String result = TextSanitizer.truncateAndSanitize(text, 30);

        assertThat(result).hasSizeLessThanOrEqualTo(33);
        assertThat(result).doesNotContain("<script>");
    }
}