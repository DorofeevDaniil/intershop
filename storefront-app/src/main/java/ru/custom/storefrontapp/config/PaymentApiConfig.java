package ru.custom.storefrontapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import ru.custom.storefrontapp.ApiClient;
import ru.custom.storefrontapp.api.DefaultApi;

@Configuration
public class PaymentApiConfig {
    @Bean
    public DefaultApi paymentApi(WebClient.Builder webClientBuilder,
                                 ReactiveOAuth2AuthorizedClientManager authorizedClientManager,
                                 @Value("${app.payment-api.url}") String apiUrl) {
        var oauth = new ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
        oauth.setDefaultClientRegistrationId("storefront-client");

        ApiClient client = new ApiClient(
            webClientBuilder
                .filter(oauth)
                .build()

        );
        client.setBasePath(apiUrl);
        return new DefaultApi(client);
    }
}