package ru.custom.storefrontapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.net.URI;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers("/admin/**").hasRole("ADMIN")
                .pathMatchers("/cart/**", "/orders/**", "/buy").authenticated()
                .pathMatchers("/register", "/login").permitAll()
                .anyExchange().permitAll()
            )
            .anonymous(anonymous -> anonymous
                .principal("anonymousUser")
            )
            .formLogin(form -> form
                .loginPage("/login")
                .authenticationSuccessHandler((webFilterExchange, authentication) -> {
                    var exchange = webFilterExchange.getExchange();
                    exchange.getResponse().setStatusCode(HttpStatus.FOUND);
                    exchange.getResponse().getHeaders().setLocation(
                        URI.create(exchange.getRequest().getPath().contextPath().value() + "/main/items"));
                    return exchange.getResponse().setComplete();
                })
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessHandler((webFilterExchange, authentication) -> {
                    var exchange = webFilterExchange.getExchange();
                    exchange.getResponse().setStatusCode(HttpStatus.FOUND);
                    exchange.getResponse().getHeaders().setLocation(
                        URI.create(exchange.getRequest().getPath().contextPath().value() + "/login?logout"));
                    return exchange.getResponse().setComplete();
                })
            )

            .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
