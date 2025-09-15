package ru.custom.storefrontapp.integration.controller;

import org.junit.jupiter.api.Test;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderControllerTest extends BaseControllerTest {
    @Test
    void handleShowOrders_shouldReturnOrders() {
        webTestClient.mutateWith(
                    SecurityMockServerConfigurers.mockUser()
                        .roles(TEST_USER_ROLE)
                )
                .get()
                .uri("/orders")
                    .exchange()
                        .expectStatus().isOk()
                .expectHeader().contentType(TEST_RESPONSE_MEDIA_TYPE)
                .expectBody(String.class)
                    .value(body -> {
                        assertThat(body).contains("Заказы");
                        assertThat(body).contains("100");
                        assertThat(body).contains("200");
                        assertThat(body).contains("test title 1");
                        assertThat(body).contains("test title 2");
                    });
    }

    @Test
    void handleShowOrders_shouldDenyAccess_whenNoRole() {
        webTestClient.mutateWith(
                        SecurityMockServerConfigurers.mockUser()
                                .roles("TEST")
                )
                .get()
                .uri("/orders")
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(String.class).consumeWith(response ->
                        assertTrue(response.getResponseBody().contains("Access Denied"))
                );
    }

    @Test
    void handleShowOrders_shouldRedirectToLogin_whenNotAuthenticated() {
        webTestClient.mutateWith(
                        SecurityMockServerConfigurers.csrf()
                )
                .get()
                .uri("/orders")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueMatches("Location", ".*/login");
    }

    @Test
    void handleGetOrder_shouldReturnOrder() {
        webTestClient.mutateWith(
                SecurityMockServerConfigurers.mockUser()
                    .roles(TEST_USER_ROLE)
            )
            .get()
            .uri("/orders/1")
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(TEST_RESPONSE_MEDIA_TYPE)
            .expectBody(String.class)
            .value(body -> {
                assertThat(body).contains("Заказ");
                assertThat(body).contains("100");
                assertThat(body).contains("200");
                assertThat(body).contains("test title 1");
                assertThat(body).contains("test title 2");
            });
    }

    @Test
    void handleGetOrder_shouldDenyAccess_whenNoRole() {
        webTestClient.mutateWith(
                        SecurityMockServerConfigurers.mockUser()
                                .roles("TEST")
                )
                .get()
                .uri("/orders/1")
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(String.class).consumeWith(response ->
                        assertTrue(response.getResponseBody().contains("Access Denied"))
                );
    }

    @Test
    void handleGetOrder_shouldRedirectToLogin_whenNotAuthenticated() {
        webTestClient.mutateWith(
                        SecurityMockServerConfigurers.csrf()
                )
                .get()
                .uri("/orders/1")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueMatches("Location", ".*/login");
    }
}
