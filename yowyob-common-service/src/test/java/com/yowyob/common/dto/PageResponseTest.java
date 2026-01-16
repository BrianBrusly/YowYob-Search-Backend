package com.yowyob.common.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests pour PageResponse
 *
 * @author YowYob Team 4GI-ENSPY Promo 2027
 * @author HEUDEP DJANDJA BRIAN B 22P405
 * @version 1.0.0
 */
@DisplayName("PageResponse Tests")
class PageResponseTest {

    @Test
    @DisplayName("Devrait créer une page avec contenu")
    void shouldCreatePageWithContent() {
        List<String> content = Arrays.asList("item1", "item2", "item3");

        PageResponse<String> page = PageResponse.of(content, 0, 10, 23);

        assertThat(page.getContent()).hasSize(3);
        assertThat(page.getPage()).isEqualTo(0);
        assertThat(page.getSize()).isEqualTo(10);
        assertThat(page.getTotalElements()).isEqualTo(23);
        assertThat(page.getTotalPages()).isEqualTo(3);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.isLast()).isFalse();
        assertThat(page.isEmpty()).isFalse();
    }

    @Test
    @DisplayName("Devrait créer une page vide")
    void shouldCreateEmptyPage() {
        PageResponse<String> page = PageResponse.empty(0, 10);

        assertThat(page.getContent()).isEmpty();
        assertThat(page.getTotalElements()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(0);
        assertThat(page.isEmpty()).isTrue();
        assertThat(page.isFirst()).isTrue();
        assertThat(page.isLast()).isTrue();
    }

    @Test
    @DisplayName("Devrait calculer correctement le nombre total de pages")
    void shouldCalculateTotalPagesCorrectly() {
        List<String> content = Arrays.asList("item1", "item2");

        PageResponse<String> page = PageResponse.of(content, 2, 10, 25);

        assertThat(page.getTotalPages()).isEqualTo(3);
    }

    @Test
    @DisplayName("Devrait identifier correctement la dernière page")
    void shouldIdentifyLastPage() {
        List<String> content = Arrays.asList("item1", "item2");

        PageResponse<String> page = PageResponse.of(content, 2, 10, 22);

        assertThat(page.isLast()).isTrue();
        assertThat(page.isFirst()).isFalse();
    }

    @Test
    @DisplayName("Devrait vérifier s'il y a une page suivante")
    void shouldCheckIfHasNext() {
        List<String> content = Arrays.asList("item1");

        PageResponse<String> firstPage = PageResponse.of(content, 0, 10, 20);
        PageResponse<String> lastPage = PageResponse.of(content, 1, 10, 11);

        assertThat(firstPage.hasNext()).isTrue();
        assertThat(lastPage.hasNext()).isFalse();
    }

    @Test
    @DisplayName("Devrait vérifier s'il y a une page précédente")
    void shouldCheckIfHasPrevious() {
        List<String> content = Arrays.asList("item1");

        PageResponse<String> firstPage = PageResponse.of(content, 0, 10, 20);
        PageResponse<String> secondPage = PageResponse.of(content, 1, 10, 20);

        assertThat(firstPage.hasPrevious()).isFalse();
        assertThat(secondPage.hasPrevious()).isTrue();
    }
}