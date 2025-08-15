package ru.custom.storefrontapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import ru.custom.paymentservice.ApiClient;
import ru.custom.paymentservice.api.DefaultApi;

@Configuration
public class PaymentApiConfig {

    @Bean
    public DefaultApi paymentApi(WebClient.Builder webClientBuilder,
                                 @Value("${app.payment-api.url}") String apiUrl) {
        ApiClient client = new ApiClient(webClientBuilder.build());
        client.setBasePath(apiUrl);
        return new DefaultApi(client);
    }
}