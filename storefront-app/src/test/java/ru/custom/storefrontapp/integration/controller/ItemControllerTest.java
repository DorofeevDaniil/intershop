package ru.custom.storefrontapp.integration.controller;//package ru.custom.intershop.integration.controller;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.http.MediaType;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//class ItemControllerTest extends BaseControllerTest {
//    @Test
//    void handleShowItem_shouldReturnItemPage() {
//        webTestClient.get()
//                .uri("/items/1")
//                    .exchange()
//                        .expectHeader().contentType(TEST_RESPONSE_MEDIA_TYPE)
//                        .expectBody(String.class)
//                        .value(body -> {
//                            assertThat(body).contains("test title 1");
//                            assertThat(body).contains("test description 1");
//                        });
//    }
//
//    @Test
//    void handleChangeAmount_shouldReturnBadRequest() {
//        webTestClient.post()
//                .uri("/items/1")
//                    .exchange()
//                        .expectStatus().isBadRequest();
//    }
//
//    @Test
//    void handleChangeAmount_shouldAddItem() {
//        webTestClient.post()
//            .uri("/items/1")
//            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
//            .bodyValue("action=PLUS")
//            .exchange()
//            .expectStatus().is3xxRedirection()
//            .expectHeader().valueEquals("Location", "/items/1");
//    }
//}
