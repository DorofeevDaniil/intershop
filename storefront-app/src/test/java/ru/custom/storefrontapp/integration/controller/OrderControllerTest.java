package ru.custom.storefrontapp.integration.controller;//package ru.custom.intershop.integration.controller;
//
//import org.junit.jupiter.api.Test;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//class OrderControllerTest extends BaseControllerTest {
//    @Test
//    void handleShowOrders_shouldReturnOrders() {
//        webTestClient.get()
//                .uri("/orders")
//                    .exchange()
//                        .expectStatus().isOk()
//                .expectHeader().contentType(TEST_RESPONSE_MEDIA_TYPE)
//                .expectBody(String.class)
//                    .value(body -> {
//                        assertThat(body).contains("Заказы");
//                        assertThat(body).contains("100");
//                        assertThat(body).contains("200");
//                        assertThat(body).contains("test title 1");
//                        assertThat(body).contains("test title 2");
//                    });
//    }
//
//    @Test
//    void handleGetOrder_shouldReturnOrder() {
//        webTestClient.get()
//            .uri("/orders/1")
//            .exchange()
//            .expectStatus().isOk()
//            .expectHeader().contentType(TEST_RESPONSE_MEDIA_TYPE)
//            .expectBody(String.class)
//            .value(body -> {
//                assertThat(body).contains("Заказ");
//                assertThat(body).contains("100");
//                assertThat(body).contains("200");
//                assertThat(body).contains("test title 1");
//                assertThat(body).contains("test title 2");
//            });
//    }
//}
