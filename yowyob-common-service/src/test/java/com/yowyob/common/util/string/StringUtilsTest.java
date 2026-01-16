package com.yowyob.common.util.string;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests pour StringUtils
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 */
@DisplayName("StringUtils Tests")
class StringUtilsTest {

    @Test
    @DisplayName("Devrait détecter une chaîne vide")
    void shouldDetectEmptyString() {
        assertThat(StringUtils.isEmpty("")).isTrue();
        assertThat(StringUtils.isEmpty(null)).isTrue();
        assertThat(StringUtils.isEmpty("text")).isFalse();
    }

    @Test
    @DisplayName("Devrait détecter une chaîne blank")
    void shouldDetectBlankString() {
        assertThat(StringUtils.isBlank("")).isTrue();
        assertThat(StringUtils.isBlank("   ")).isTrue();
        assertThat(StringUtils.isBlank(null)).isTrue();
        assertThat(StringUtils.isBlank("text")).isFalse();
    }

    @Test
    @DisplayName("Devrait capitaliser une chaîne")
    void shouldCapitalizeString() {
        assertThat(StringUtils.capitalize("hello")).isEqualTo("Hello");
        assertThat(StringUtils.capitalize("HELLO")).isEqualTo("HELLO");
        assertThat(StringUtils.capitalize("")).isEqualTo("");
        assertThat(StringUtils.capitalize(null)).isNull();
    }

    @Test
    @DisplayName("Devrait tronquer une chaîne")
    void shouldTruncateString() {
        String text = "This is a long text";

        assertThat(StringUtils.truncate(text, 10)).isEqualTo("This is a ...");
        assertThat(StringUtils.truncate(text, 100)).isEqualTo(text);
        assertThat(StringUtils.truncate(null, 10)).isNull();
    }

    @Test
    @DisplayName("Devrait tronquer avec suffixe personnalisé")
    void shouldTruncateWithCustomSuffix() {
        String text = "This is a long text";

        assertThat(StringUtils.truncate(text, 10, ">>")).isEqualTo("This is a >>");
    }

    @Test
    @DisplayName("Devrait joindre une collection")
    void shouldJoinCollection() {
        List<String> items = List.of("apple", "banana", "orange");

        assertThat(StringUtils.join(items, ", ")).isEqualTo("apple, banana, orange");
        assertThat(StringUtils.join((Collection<String>) null, ", ")).isEqualTo("");
    }

    @Test
    @DisplayName("Devrait joindre un tableau")
    void shouldJoinArray() {
        String[] items = {"apple", "banana", "orange"};

        assertThat(StringUtils.join(items, ", ")).isEqualTo("apple, banana, orange");
    }

    @Test
    @DisplayName("Devrait supprimer les espaces")
    void shouldRemoveWhitespace() {
        assertThat(StringUtils.removeWhitespace("hello world")).isEqualTo("helloworld");
        assertThat(StringUtils.removeWhitespace("  a  b  c  ")).isEqualTo("abc");
    }

    @Test
    @DisplayName("Devrait normaliser les espaces")
    void shouldNormalizeWhitespace() {
        assertThat(StringUtils.normalizeWhitespace("  hello   world  ")).isEqualTo("hello world");
        assertThat(StringUtils.normalizeWhitespace("a\t\tb\n\nc")).isEqualTo("a b c");
    }

    @Test
    @DisplayName("Devrait répéter une chaîne")
    void shouldRepeatString() {
        assertThat(StringUtils.repeat("ab", 3)).isEqualTo("ababab");
        assertThat(StringUtils.repeat("x", 0)).isEqualTo("");
        assertThat(StringUtils.repeat(null, 3)).isEqualTo("");
    }

    @Test
    @DisplayName("Devrait inverser une chaîne")
    void shouldReverseString() {
        assertThat(StringUtils.reverse("hello")).isEqualTo("olleh");
        assertThat(StringUtils.reverse("")).isEqualTo("");
        assertThat(StringUtils.reverse(null)).isNull();
    }

    @Test
    @DisplayName("Devrait vérifier si la chaîne est numérique")
    void shouldCheckIfNumeric() {
        assertThat(StringUtils.isNumeric("12345")).isTrue();
        assertThat(StringUtils.isNumeric("123a45")).isFalse();
        assertThat(StringUtils.isNumeric("")).isFalse();
    }

    @Test
    @DisplayName("Devrait vérifier si la chaîne est alphabétique")
    void shouldCheckIfAlpha() {
        assertThat(StringUtils.isAlpha("hello")).isTrue();
        assertThat(StringUtils.isAlpha("hello123")).isFalse();
        assertThat(StringUtils.isAlpha("")).isFalse();
    }

    @Test
    @DisplayName("Devrait vérifier si la chaîne est alphanumérique")
    void shouldCheckIfAlphanumeric() {
        assertThat(StringUtils.isAlphanumeric("hello123")).isTrue();
        assertThat(StringUtils.isAlphanumeric("hello 123")).isFalse();
        assertThat(StringUtils.isAlphanumeric("")).isFalse();
    }

    @Test
    @DisplayName("Devrait supprimer les accents")
    void shouldRemoveAccents() {
        assertThat(StringUtils.removeAccents("éàùçô")).isEqualTo("eauco");
        assertThat(StringUtils.removeAccents("Café")).isEqualTo("Cafe");
    }

    @Test
    @DisplayName("Devrait convertir en camelCase")
    void shouldConvertToCamelCase() {
        assertThat(StringUtils.toCamelCase("hello world")).isEqualTo("helloWorld");
        assertThat(StringUtils.toCamelCase("hello_world")).isEqualTo("helloWorld");
        assertThat(StringUtils.toCamelCase("hello-world")).isEqualTo("helloWorld");
    }

    @Test
    @DisplayName("Devrait convertir en snake_case")
    void shouldConvertToSnakeCase() {
        assertThat(StringUtils.toSnakeCase("helloWorld")).isEqualTo("hello_world");
        assertThat(StringUtils.toSnakeCase("HelloWorld")).isEqualTo("hello_world");
        assertThat(StringUtils.toSnakeCase("hello world")).isEqualTo("hello_world");
    }

    @Test
    @DisplayName("Devrait convertir en kebab-case")
    void shouldConvertToKebabCase() {
        assertThat(StringUtils.toKebabCase("helloWorld")).isEqualTo("hello-world");
        assertThat(StringUtils.toKebabCase("HelloWorld")).isEqualTo("hello-world");
        assertThat(StringUtils.toKebabCase("hello_world")).isEqualTo("hello-world");
    }
}