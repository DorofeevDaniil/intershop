package ru.custom.paymentservice.configuration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@TestConfiguration
@Profile("test")
public class JwtDecoderTestConfig {

    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder() {
        return jwt -> Mono.just(Jwt.withTokenValue("fake")
                .header("alg", "none")
                .claim("sub", "user1")
                .claim("resource_access", Map.of(
                        "account", Map.of(
                                "roles", List.of("ROLE_USER")
                        )
                ))
                .build());
    }
}
