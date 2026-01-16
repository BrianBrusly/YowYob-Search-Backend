package com.yowyob.common.util.string;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests pour SlugGenerator
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 */
@DisplayName("SlugGenerator Tests")
class SlugGeneratorTest {

    @Test
    @DisplayName("Devrait générer un slug simple")
    void shouldGenerateSimpleSlug() {
        String text = "Hello World";

        String slug = SlugGenerator.toSlug(text);

        assertThat(slug).isEqualTo("hello-world");
    }

    @Test
    @DisplayName("Devrait supprimer les accents")
    void shouldRemoveAccents() {
        String text = "Café París";

        String slug = SlugGenerator.toSlug(text);

        assertThat(slug).isEqualTo("cafe-paris");
    }

    @Test
    @DisplayName("Devrait supprimer les caractères spéciaux")
    void shouldRemoveSpecialCharacters() {
        String text = "Hello @ World! #2024";

        String slug = SlugGenerator.toSlug(text);

        assertThat(slug).isEqualTo("hello-world-2024");
    }

    @Test
    @DisplayName("Devrait gérer les espaces multiples")
    void shouldHandleMultipleSpaces() {
        String text = "Hello    World";

        String slug = SlugGenerator.toSlug(text);

        assertThat(slug).isEqualTo("hello-world");
    }

    @Test
    @DisplayName("Devrait tronquer le slug à la longueur maximale")
    void shouldTruncateToMaxLength() {
        String text = "This is a very long title that should be truncated";

        String slug = SlugGenerator.toSlug(text, 20);

        assertThat(slug.length()).isLessThanOrEqualTo(20);
        assertThat(slug).isEqualTo("this-is-a-very-long"); // "this-is-a-very-long" is 19 chars
        assertThat(slug).doesNotEndWith("-");
    }

    @Test
    @DisplayName("Devrait générer un slug unique")
    void shouldGenerateUniqueSlug() {
        String text = "Hello World";

        String slug1 = SlugGenerator.uniqueSlug(text);
        String slug2 = SlugGenerator.uniqueSlug(text);

        assertThat(slug1).startsWith("hello-world-");
        assertThat(slug2).startsWith("hello-world-");
        assertThat(slug1).isNotEqualTo(slug2);
    }

    @Test
    @DisplayName("Devrait valider un slug valide")
    void shouldValidateValidSlug() {
        assertThat(SlugGenerator.isValidSlug("hello-world")).isTrue();
        assertThat(SlugGenerator.isValidSlug("hello-world-123")).isTrue();
        assertThat(SlugGenerator.isValidSlug("helloworld")).isTrue();
    }

    @Test
    @DisplayName("Devrait invalider un slug invalide")
    void shouldInvalidateInvalidSlug() {
        assertThat(SlugGenerator.isValidSlug("Hello World")).isFalse();
        assertThat(SlugGenerator.isValidSlug("hello_world")).isFalse();
        assertThat(SlugGenerator.isValidSlug("hello--world")).isFalse();
        assertThat(SlugGenerator.isValidSlug("-hello")).isFalse();
    }

    @Test
    @DisplayName("Devrait générer un slug aléatoire pour texte vide")
    void shouldGenerateRandomSlugForEmptyText() {
        String slug = SlugGenerator.toSlug("");

        assertThat(slug).startsWith("slug-");
        assertThat(slug).hasSize(13);
    }
}