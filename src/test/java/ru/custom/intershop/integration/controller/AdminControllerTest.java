package ru.custom.intershop.integration.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.web.reactive.function.BodyInserters;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdminControllerTest extends BaseControllerTest {
    @Test
    void showDefault_shouldRedirectToAdd() {
        webTestClient.get()
                .uri("/admin")
                    .exchange()
                        .expectStatus().is3xxRedirection()
                        .expectHeader().valueEquals("Location", "/admin/add");
    }

    @Test
    void showAdd_shouldReturnAddItemTemplate() {
        webTestClient.get()
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
    void handleAddItem_shouldUpdateItem() {
        webTestClient.post()
            .uri("/admin/add")
            .body(BodyInserters.fromMultipartData(createMultipartBuilder().build()))
            .exchange()
            .expectStatus().is3xxRedirection()
            .expectHeader().valueEquals("Location", "/admin/add");
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
