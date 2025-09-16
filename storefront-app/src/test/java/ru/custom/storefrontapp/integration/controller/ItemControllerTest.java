package ru.custom.storefrontapp.integration.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemControllerTest extends BaseControllerTest {
    @Test
    void handleShowItem_shouldReturnItemPage() {
        webTestClient.get()
                .uri("/items/1")
                    .exchange()
                        .expectHeader().contentType(TEST_RESPONSE_MEDIA_TYPE)
                        .expectBody(String.class)
                        .value(body -> {
                            assertThat(body).contains("test title 1");
                            assertThat(body).contains("test description 1");
                        });
    }

    @Test
    void handleChangeAmount_shouldReturnBadRequest() {
        webTestClient.mutateWith(
                    SecurityMockServerConfigurers.mockUser()
                        .roles(TEST_USER_ROLE)
                )
                .post()
                .uri("/items/1")
                    .exchange()
                        .expectStatus().isBadRequest();
    }

    @Test
    void handleChangeAmount_shouldAddItem() {
        webTestClient.mutateWith(
                SecurityMockServerConfigurers.mockUser()
                    .roles(TEST_USER_ROLE)
            )
            .post()
            .uri("/items/1")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .bodyValue("action=PLUS")
            .exchange()
            .expectStatus().is3xxRedirection()
            .expectHeader().valueEquals("Location", "/items/1");
    }

    @Test
    void handleChangeAmount_shouldDenyAccess_whenNoRole() {
        webTestClient.mutateWith(
                        SecurityMockServerConfigurers.mockUser()
                                .roles("TEST")
                )
                .post()
                .uri("/items/1")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue("action=PLUS")
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(String.class).consumeWith(response ->
                        assertTrue(response.getResponseBody().contains("Access Denied"))
                );
    }

    @Test
    void handleChangeAmount_shouldRedirectToLogin_whenNotAuthenticated() {
        webTestClient
                .post()
                .uri("/items/1")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue("action=PLUS")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueMatches("Location", ".*/login");
    }
}
