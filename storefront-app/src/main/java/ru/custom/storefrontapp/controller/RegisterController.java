package ru.custom.storefrontapp.controller;

import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.custom.storefrontapp.dto.RegisterRequest;
import ru.custom.storefrontapp.service.UserManagementService;

@Controller
public class RegisterController {
    private final UserManagementService userManagementService;

    public RegisterController(
        UserManagementService userManagementService
    ) {
        this.userManagementService = userManagementService;
    }

    @PostMapping("/register")
    public Mono<String> register(@ModelAttribute RegisterRequest registerRequest) {
        return userManagementService.registerUser(registerRequest)
            .thenReturn("redirect:/login?registered");
    }

    @ModelAttribute("_csrf")
    public Mono<CsrfToken> csrfToken(ServerWebExchange exchange) {
        return exchange.getAttribute(CsrfToken.class.getName());
    }
}
