package ru.custom.paymentservice.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/api/balance").authenticated() // защищаем только баланс
                        .anyExchange().permitAll()                    // остальные открыты
                )
                .oauth2Login(oauth2 -> {})                       // включаем OAuth2 login
                .csrf(ServerHttpSecurity.CsrfSpec::disable);     // отключаем CSRF для быстрого теста

        return http.build();
    }
}
