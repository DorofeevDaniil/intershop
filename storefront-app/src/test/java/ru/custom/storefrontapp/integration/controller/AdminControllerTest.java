package ru.custom.storefrontapp.integration.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.web.reactive.function.BodyInserters;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdminControllerTest extends BaseControllerTest {
    @Test
    void showDefault_shouldRedirectToAdd() {
        webTestClient.mutateWith(
                    SecurityMockServerConfigurers.mockUser()
                        .roles(TEST_ADMIN_ROLE)
                )
                .get()
                .uri("/admin")
                    .exchange()
                        .expectStatus().is3xxRedirection()
                        .expectHeader().valueEquals("Location", "/admin/add");
    }

    @Test
    void showDefault_shouldRedirectToLogin_whenNotAuthenticated() {
        webTestClient.get()
                .uri("/admin")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueMatches("Location", ".*/login");
    }

    @Test
    void showDefault_shouldDenyAccess_whenNoRole() {
        webTestClient.mutateWith(
                        SecurityMockServerConfigurers.mockUser()
                                .roles(TEST_USER_ROLE)
                )
                .get()
                .uri("/admin")
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(String.class).consumeWith(response ->
                        assertTrue(response.getResponseBody().contains("Access Denied"))
                );
    }

    @Test
    void showAdd_shouldReturnAddItemTemplate() {
        webTestClient.mutateWith(
                    SecurityMockServerConfigurers.mockUser()
                        .roles(TEST_ADMIN_ROLE)
                )
                .get()
                .uri("/admin/add")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(TEST_RESPONSE_MEDIA_TYPE)
                .expectBody(String.class).consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("Введите название товара"));
                });
    }

    @Test
    void showAdd_shouldRedirectToLogin_whenNotAuthenticated() {
        webTestClient.get()
                .uri("/admin/add")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueMatches("Location", ".*/login");
    }

    @Test
    void showAdd_shouldDenyAccess_whenNoRole() {
        webTestClient.mutateWith(
                        SecurityMockServerConfigurers.mockUser()
                                .roles(TEST_USER_ROLE)
                )
                .get()
                .uri("/admin/add")
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(String.class).consumeWith(response ->
                        assertTrue(response.getResponseBody().contains("Access Denied"))
                );
    }

    @Test
    void handleAddItem_shouldUpdateItem() {
        webTestClient.mutateWith(
                SecurityMockServerConfigurers.csrf()
            )
            .mutateWith(
                SecurityMockServerConfigurers.mockUser()
                    .roles(TEST_ADMIN_ROLE)
            )
            .post()
            .uri("/admin/add")
            .body(BodyInserters.fromMultipartData(createMultipartBuilder().build()))
            .exchange()
            .expectStatus().is3xxRedirection()
            .expectHeader().valueEquals("Location", "/admin/add");
    }

    @Test
    void handleAddItem_shouldRedirectToLogin_whenNotAuthenticated() {
        webTestClient.mutateWith(
                    SecurityMockServerConfigurers.csrf()
                )
                .post()
                .uri("/admin/add")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueMatches("Location", ".*/login");
    }

    @Test
    void handleAddItem_shouldDenyAccess_whenNoCsrf() {
        webTestClient.post()
                .uri("/admin/add")
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(String.class).consumeWith(response ->
                        assertTrue(response.getResponseBody().contains("Access Denied"))
                );
    }

    @Test
    void handleAddItem_shouldDenyAccess_whenNoRole() {
        webTestClient.mutateWith(
                    SecurityMockServerConfigurers.mockUser()
                        .roles(TEST_USER_ROLE)
                ).mutateWith(
                    SecurityMockServerConfigurers.csrf()
                )
                .post()
                .uri("/admin/add")
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(String.class).consumeWith(response ->
                        assertTrue(response.getResponseBody().contains("Access Denied"))
                );
    }

    private MultipartBodyBuilder createMultipartBuilder() {
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("image", TEST_FILE_CONTENT)
            .filename(TEST_FILE_NAME)
            .contentType(TEST_FILE_CONTENT_TYPE);

        multipartBodyBuilder.part("title", "new title")
            .header("Content-Disposition", "form-data; name=title");

        multipartBodyBuilder.part("price", "100")
            .header("Content-Disposition", "form-data; name=price");

        multipartBodyBuilder.part("description", "some test description")
            .header("Content-Disposition", "form-data; name=description");

        return multipartBodyBuilder;
    }
}
