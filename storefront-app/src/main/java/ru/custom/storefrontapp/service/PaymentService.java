package ru.custom.storefrontapp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.custom.paymentservice.ApiClient;
import ru.custom.paymentservice.api.DefaultApi;
import ru.custom.paymentservice.domain.PaymentDto;

import java.math.BigDecimal;

@Service
@Slf4j
public class PaymentService {
    private final DefaultApi paymentApi;

    public PaymentService(WebClient.Builder webClientBuilder, @Value("${app.payment-api.url}") String apiUrl) {
        ApiClient client = new ApiClient(webClientBuilder.build());
        client.setBasePath(apiUrl);

        this.paymentApi = new DefaultApi(client);
    }

    public Mono<BigDecimal> getBalance() {
        return paymentApi.apiBalanceGet()
            .map(balanceDto -> BigDecimal.valueOf(balanceDto.getBalance()));
    }

    public Mono<Void> postPayment(PaymentDto paymentDto) {
        return paymentApi.apiPaymentPost(paymentDto);
    }
}
