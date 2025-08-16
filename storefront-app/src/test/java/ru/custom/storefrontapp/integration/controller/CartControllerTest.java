package ru.custom.storefrontapp.integration.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockReset;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;
import ru.custom.storefrontapp.api.DefaultApi;
import ru.custom.storefrontapp.domain.BalanceDto;
import ru.custom.storefrontapp.service.StoreFrontService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

class CartControllerTest extends BaseControllerTest {
    @Autowired
    private StoreFrontService storeFrontService;

    @MockitoBean(reset = MockReset.BEFORE)
    private DefaultApi paymentApi;

    @Test
    void handleShowItems_shouldReturnItemsInCart() {
        storeFrontService.changeItemQuantity(1L, "PLUS").block();
        doReturn(Mono.just(new BalanceDto().balance(10000.0))).when(paymentApi).apiBalanceGet();

        webTestClient.get()
                .uri("/cart/items")
                .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(TEST_RESPONSE_MEDIA_TYPE)
                    .expectBody(String.class)
                    .value(body -> {
                        assertThat(body).contains("<button>Купить</button>");
                        assertThat(body).contains("test description 1");
                        assertThat(body).contains("100");
                        assertThat(body).contains("test title 1");
                    });

    }

    @Test
    void handleShowItems_shouldReturnItemsInCart_shouldDisableButtonWhenNotEnoughMoney() {
        storeFrontService.changeItemQuantity(1L, "PLUS").block();
        doReturn(Mono.just(new BalanceDto().balance(10.0))).when(paymentApi).apiBalanceGet();

        webTestClient.get()
            .uri("/cart/items")
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(TEST_RESPONSE_MEDIA_TYPE)
            .expectBody(String.class)
            .value(body -> {
                assertThat(body).contains("<button disabled=\"disabled\">Купить</button>");
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
