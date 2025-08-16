package ru.custom.storefrontapp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import ru.custom.storefrontapp.api.DefaultApi;
import ru.custom.storefrontapp.domain.PaymentDto;

import java.math.BigDecimal;
import java.time.Duration;

@Service
@Slf4j
public class PaymentService {
    private final DefaultApi paymentApi;

    public PaymentService(DefaultApi paymentApi) {
        this.paymentApi = paymentApi;
    }

    public Mono<BigDecimal> getBalance() {
        return paymentApi.apiBalanceGet()
            .map(balanceDto -> BigDecimal.valueOf(balanceDto.getBalance()))
            .onErrorReturn(WebClientRequestException.class, BigDecimal.ZERO);
    }

    public Mono<Void> postPayment(PaymentDto paymentDto) {
        return paymentApi.apiPaymentPost(paymentDto)
            .retryWhen(
                Retry.backoff(3, Duration.ofMillis(500))
                    .filter(err -> err instanceof WebClientResponseException ex
                        && ex.getStatusCode().is4xxClientError())
                    .onRetryExhaustedThrow((spec, signal) -> signal.failure())
            );
    }
}
