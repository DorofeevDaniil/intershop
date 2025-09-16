package ru.custom.paymentservice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.custom.paymentservice.configuration.JwtDecoderTestConfig;
import ru.custom.paymentservice.domain.BalanceDto;
import ru.custom.paymentservice.domain.PaymentDto;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(JwtDecoderTestConfig.class)
class BaseApiControllerTest {
    @Autowired
    protected WebTestClient webTestClient;

    private static final String TEST_TOKEN = "fake";

    @Test
    void apiBalanceGet_shouldReturnBalance() {
        webTestClient
            .get()
            .uri("/api/balance/1")
            .accept(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + TEST_TOKEN)
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
        payment.setUserId(1L);

        webTestClient.post()
            .uri("/api/payment")
            .bodyValue(payment)
            .accept(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + TEST_TOKEN)
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
            .header("Authorization", "Bearer " + TEST_TOKEN)
            .exchange()
            .expectStatus().isBadRequest();
    }
}