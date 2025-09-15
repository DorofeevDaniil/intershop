package ru.custom.storefrontapp.integration.controller;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultControllerTest extends BaseControllerTest {
    @Test
    void handleMainRedirect_shouldRedirectToMainController() {
        webTestClient.get()
            .uri("/")
            .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/main/items");
    }

    @Test
    void loginPage_shouldRedirectToMainController() {
        webTestClient.get()
            .uri("/login")
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(TEST_RESPONSE_MEDIA_TYPE)
            .expectBody(String.class)
            .value(body -> assertThat(body).contains("Вход в систему"));
    }

    @Test
    void registerPage_shouldRedirectToMainController() {
        webTestClient.get()
            .uri("/register")
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(TEST_RESPONSE_MEDIA_TYPE)
            .expectBody(String.class)
            .value(body -> assertThat(body).contains("Регистрация"));
    }
}
