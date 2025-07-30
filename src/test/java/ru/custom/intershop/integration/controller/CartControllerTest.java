package ru.custom.intershop.integration.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import ru.custom.intershop.service.ItemService;

import static org.assertj.core.api.Assertions.assertThat;

class CartControllerTest extends BaseControllerTest {
    @Autowired
    private ItemService itemService;

    @Test
    void handleShowItems_shouldReturnItemsInCart() {
        itemService.changeAmount(1L, "PLUS").block();

        webTestClient.get()
                .uri("/cart/items")
                .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(TEST_RESPONSE_MEDIA_TYPE)
                    .expectBody(String.class)
                    .value(body -> {
                        assertThat(body).contains("test description 1");
                        assertThat(body).contains("100");
                        assertThat(body).contains("test title 1");
                    });

    }

    @Test
    void handleChangeAmount_shouldReturnBadRequest() {
        webTestClient.post()
                .uri("/cart/items/1")
                    .exchange()
                    .expectStatus().isBadRequest();
    }

    @Test
    void handleChangeAmount_shouldAddItem() {
        webTestClient.post()
            .uri("/cart/items/1")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .bodyValue("action=PLUS")
            .exchange()
            .expectStatus().is3xxRedirection()
            .expectHeader().valueEquals("Location", "/cart/items");
    }
}
