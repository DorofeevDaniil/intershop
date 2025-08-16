package ru.custom.storefrontapp.unit.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.bean.override.mockito.MockReset;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.custom.storefrontapp.api.DefaultApi;
import ru.custom.storefrontapp.domain.BalanceDto;
import ru.custom.storefrontapp.domain.PaymentDto;
import ru.custom.storefrontapp.service.PaymentService;

import java.math.BigDecimal;
import java.net.URI;

import static org.mockito.Mockito.*;

@SpringBootTest(classes = PaymentService.class)
class PaymentServiceTest extends BaseServiceTest {
    @MockitoBean(reset = MockReset.BEFORE)
    private DefaultApi paymentApi;

    @Autowired
    private PaymentService paymentService;

    @Test
    void getBalance_shouldReturnBalance() {
        doReturn(Mono.just(new BalanceDto().balance(100.0))).when(paymentApi).apiBalanceGet();

        StepVerifier.create(paymentService.getBalance())
            .expectNext(BigDecimal.valueOf(100.0))
            .verifyComplete();

        verify(paymentApi, times(1)).apiBalanceGet();
    }

    @Test
    void getBalance_shouldReturnZeroOnError() {
        WebClientRequestException ex = new WebClientRequestException(
            new RuntimeException("Network error"),
            HttpMethod.GET,
            URI.create("http://localhost/test"),
            new HttpHeaders()
        );

        doReturn(Mono.error(ex)).when(paymentApi).apiBalanceGet();

        StepVerifier.create(paymentService.getBalance())
            .expectNext(BigDecimal.ZERO)
            .verifyComplete();

        verify(paymentApi, times(1)).apiBalanceGet();
    }

    @Test
    void postPayment_shouldProcessPayment() {
        PaymentDto payment = new PaymentDto();
        payment.setAmount(100.0);

        doReturn(Mono.empty()).when(paymentApi).apiPaymentPost(any(PaymentDto.class));

        StepVerifier.create(paymentService.postPayment(payment))
            .verifyComplete();

        verify(paymentApi, times(1)).apiPaymentPost(payment);
    }

    @Test
    void postPayment_shouldThrowException() {
        PaymentDto payment = new PaymentDto();

        WebClientResponseException ex = WebClientResponseException.create(
            400, "Bad Request", new HttpHeaders(), null, null
        );

        doReturn(Mono.error(ex)).when(paymentApi).apiPaymentPost(any(PaymentDto.class));

        StepVerifier.create(paymentService.postPayment(payment))
            .verifyError(WebClientResponseException.class);
    }
}
