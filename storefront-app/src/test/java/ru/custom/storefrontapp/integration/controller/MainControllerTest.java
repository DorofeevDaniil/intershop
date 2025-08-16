package ru.custom.storefrontapp.integration.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class MainControllerTest extends BaseControllerTest {
    @Test
    void handleGetItems_shouldReturnAllItemsList_withoutFilter_withoutSort() {
        webTestClient.get()
                .uri("/main/items")
                .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(TEST_RESPONSE_MEDIA_TYPE)
                    .expectBody(String.class)
                    .value(body -> {
                        assertThat(body).contains("Витрина товаров");
                        assertThat(body).contains("name=\"search\" value=\"\"");
                        assertThat(body).contains("value=\"NO\" selected=\"selected\"");
                        assertThat(body).contains("test description 1");
                        assertThat(body).contains("test description 2");
                    });
    }

    @Test
    void handleGetItems_shouldReturnAllItemsList_withFilter_withoutSort() {
        webTestClient.get()
                .uri(uriBuilder ->
                    uriBuilder.path("/main/items")
                        .queryParam("search", "1")
                        .build())
                .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(TEST_RESPONSE_MEDIA_TYPE)
                    .expectBody(String.class)
                    .value(body -> {
                        assertThat(body).contains("Витрина товаров");
                        assertThat(body).contains("name=\"search\" value=\"1\"");
                        assertThat(body).contains("value=\"NO\" selected=\"selected\"");
                        assertThat(body).contains("test description 1");
                        assertThat(body).doesNotContain("test description 2");
                    });
    }

    @Test
    void handleGetItems_shouldReturnAllItemsList_withoutFilter_withSort() {
        webTestClient.get()
                .uri(uriBuilder ->
                    uriBuilder.path("/main/items")
                        .queryParam("sort", "PRICE")
                        .build())
                .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(TEST_RESPONSE_MEDIA_TYPE)
                    .expectBody(String.class)
                    .value(body -> {
                        assertThat(body).contains("Витрина товаров");
                        assertThat(body).contains("name=\"search\" value=\"\"");
                        assertThat(body).contains("value=\"PRICE\" selected=\"selected\"");

                        int pos1 = body.indexOf("test description 1");
                        int pos2 = body.indexOf("test description 2");
                        assertThat(pos1).isLessThan(pos2);
                    });
    }

    @Test
    void handleGetItems_shouldReturnAllItemsList_withFilter_withSort() {
        webTestClient.get()
            .uri(uriBuilder ->
                uriBuilder.path("/main/items")
                    .queryParam("sort", "PRICE")
                    .queryParam("search", "1")
                    .build())
            .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(TEST_RESPONSE_MEDIA_TYPE)
                .expectBody(String.class)
                .value(body -> {
                    assertThat(body).contains("Витрина товаров");
                    assertThat(body).contains("name=\"search\" value=\"1\"");
                    assertThat(body).contains("value=\"PRICE\" selected=\"selected\"");
                    assertThat(body).contains("test description 1");
                    assertThat(body).doesNotContain("test description 2");
                });
    }

    @Test
    void handleChangeAmount_shouldReturnBadRequest() {
        webTestClient.post()
            .uri("/main/items/1")
            .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void handleChangeAmount_shouldAddItem() {
        webTestClient.post()
            .uri("/main/items/1")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .bodyValue("action=PLUS")
            .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/main/items");
    }
}
