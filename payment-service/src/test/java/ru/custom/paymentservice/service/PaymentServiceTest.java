package ru.custom.paymentservice.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.custom.paymentservice.domain.BalanceDto;
import ru.custom.paymentservice.domain.PaymentDto;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(classes = PaymentService.class)
class PaymentServiceTest {

    @Autowired
    private PaymentService paymentService;

    @Test
    void getBalance_shouldReturnMockBalance() {
        BalanceDto balanceDto = new BalanceDto();
        balanceDto.setBalance(100000.0);

        StepVerifier.create(paymentService.getBalance())
            .assertNext(actualBalance -> assertEquals(balanceDto, actualBalance))
            .verifyComplete();
    }

    @Test
    void processPayment_shouldProcess() {
        PaymentDto payment = new PaymentDto();
        payment.setAmount(100.0);

        StepVerifier.create(paymentService.processPayment(Mono.just(payment)))
            .assertNext(actualResult -> assertEquals(true, actualResult))
            .verifyComplete();
    }

    @Test
    void processPayment_shouldCancelPayment() {
        PaymentDto payment = new PaymentDto();
        payment.setAmount(1000000.0);

        StepVerifier.create(paymentService.processPayment(Mono.just(payment)))
            .assertNext(actualResult -> assertEquals(false, actualResult))
            .verifyComplete();
    }
}