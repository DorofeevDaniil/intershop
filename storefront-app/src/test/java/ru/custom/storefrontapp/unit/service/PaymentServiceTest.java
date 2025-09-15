package ru.custom.storefrontapp.unit.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest extends BaseServiceTest {
    @Mock
    private DefaultApi paymentApi;
    @InjectMocks
    private PaymentService paymentService;

    @Test
    void getBalance_shouldReturnBalance() {
        doReturn(Mono.just(new BalanceDto().balance(100.0))).when(paymentApi).apiBalanceUserIdGet(TEST_USER_ID);

        StepVerifier.create(paymentService.getBalance(TEST_USER_ID))
            .expectNext(BigDecimal.valueOf(100.0))
            .verifyComplete();

        verify(paymentApi, times(1)).apiBalanceUserIdGet(anyLong());
    }

    @Test
    void getBalance_shouldReturnZeroOnError() {
        WebClientRequestException ex = new WebClientRequestException(
            new RuntimeException("Network error"),
            HttpMethod.GET,
            URI.create("http://localhost/test"),
            new HttpHeaders()
        );

        doReturn(Mono.error(ex)).when(paymentApi).apiBalanceUserIdGet(TEST_USER_ID);

        StepVerifier.create(paymentService.getBalance(TEST_USER_ID))
            .expectNext(BigDecimal.ZERO)
            .verifyComplete();

        verify(paymentApi, times(1)).apiBalanceUserIdGet(anyLong());
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
