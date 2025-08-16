package ru.custom.paymentservice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.custom.paymentservice.domain.BalanceDto;
import ru.custom.paymentservice.domain.PaymentDto;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BaseApiControllerTest {
    @Autowired
    protected WebTestClient webTestClient;

    @Test
    void apiBalanceGet_shouldReturnBalance() {
        webTestClient.get()
            .uri("/api/balance")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
            .expectBody(BalanceDto.class)
            .value(dto -> {
                assertNotNull(dto);
                assertEquals(100000.0, dto.getBalance());
            });
    }

    @Test
    void apiPaymentPost_shouldProcessPayment() {
        PaymentDto payment = new PaymentDto();
        payment.setAmount(100.0);

        webTestClient.post()
            .uri("/api/payment")
            .bodyValue(payment)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody().isEmpty();
    }

    @Test
    void apiPaymentPost_shouldReturnBadRequest() {
        PaymentDto payment = new PaymentDto();
        payment.setAmount(10000000.0);

        webTestClient.post()
            .uri("/api/payment")
            .bodyValue(payment)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isBadRequest();
    }
}