package ru.custom.storefrontapp.integration.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.bean.override.mockito.MockReset;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import ru.custom.paymentservice.api.DefaultApi;

import ru.custom.paymentservice.domain.PaymentDto;
import ru.custom.storefrontapp.service.OrderService;
import ru.custom.storefrontapp.service.StoreFrontService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

class BuyControllerTest extends BaseControllerTest {
    @Autowired
    private OrderService orderService;
    @Autowired
    private StoreFrontService storeFrontService;
    @MockitoBean(reset = MockReset.BEFORE)
    private DefaultApi paymentApi;

    @Test
    void handleBuyItems_shouldBuyItems() {
        storeFrontService.changeItemQuantity(1L, "PLUS").block();
        doReturn(Mono.empty()).when(paymentApi).apiPaymentPost(any(PaymentDto.class));

        webTestClient.post()
            .uri("/buy")
            .exchange()
            .expectStatus().is3xxRedirection()
            .expectHeader().valueMatches("Location", "/orders/2\\?newOrder=true");
    }

    @Test
    void handleBuyItems_shouldThrowException() {
        storeFrontService.changeItemQuantity(1L, "PLUS").block();
        WebClientResponseException ex = WebClientResponseException.create(
            400, "Bad Request", new HttpHeaders(), null, null
        );

        doReturn(Mono.error(ex)).when(paymentApi).apiPaymentPost(any(PaymentDto.class));

        webTestClient.post()
            .uri("/buy")
            .exchange()
            .expectStatus().is5xxServerError();
    }
}
