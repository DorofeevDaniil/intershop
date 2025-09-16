package ru.custom.storefrontapp.integration.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.bean.override.mockito.MockReset;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import ru.custom.storefrontapp.api.DefaultApi;

import ru.custom.storefrontapp.domain.PaymentDto;
import ru.custom.storefrontapp.service.StoreFrontService;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

class BuyControllerTest extends BaseControllerTest {
    @Autowired
    private StoreFrontService storeFrontService;
    @MockitoBean(reset = MockReset.BEFORE)
    private DefaultApi paymentApi;

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void handleBuyItems_shouldBuyItems() {
        storeFrontService.changeItemQuantity(1L, "PLUS", "user").block();
        doReturn(Mono.empty()).when(paymentApi).apiPaymentPost(any(PaymentDto.class));

        webTestClient
            .post()
            .uri("/buy")
            .exchange()
            .expectStatus().is3xxRedirection()
            .expectHeader().valueMatches("Location", "/orders/2\\?newOrder=true");
    }

    @Test
    void handleBuyItems_shouldRedirectToLogin_whenNotAuthenticated() {
        webTestClient
            .post()
            .uri("/buy")
            .exchange()
            .expectStatus().is3xxRedirection()
            .expectHeader().valueMatches("Location", ".*/login");
    }

    @Test
    void handleBuyItems_shouldDenyAccess_whenNoRole() {
        webTestClient.mutateWith(
                SecurityMockServerConfigurers.mockUser()
                    .roles("TEST")
            )
            .post()
            .uri("/buy")
            .exchange()
            .expectStatus().isForbidden()
            .expectBody(String.class).consumeWith(response ->
                    assertTrue(response.getResponseBody().contains("Access Denied"))
            );
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void handleBuyItems_shouldThrowException() {
        storeFrontService.changeItemQuantity(1L, "PLUS", "user").block();
        WebClientResponseException ex = WebClientResponseException.create(
            400, "Bad Request", new HttpHeaders(), null, null
        );

        doReturn(Mono.error(ex)).when(paymentApi).apiPaymentPost(any(PaymentDto.class));

        webTestClient
            .post()
            .uri("/buy")
            .exchange()
            .expectStatus().is5xxServerError();
    }
}
