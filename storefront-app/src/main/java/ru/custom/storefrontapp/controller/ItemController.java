package ru.custom.storefrontapp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.custom.storefrontapp.service.StoreFrontService;

@Controller
@RequestMapping("/items/{id}")
public class ItemController {
    private final StoreFrontService storeFrontService;

    public ItemController(StoreFrontService storeFrontService) {
        this.storeFrontService = storeFrontService;
    }

    @GetMapping
    public Mono<String> handleShowItem(
        @PathVariable("id") Long id,
        Model model,
        @AuthenticationPrincipal Authentication authentication
    ) {
        return storeFrontService.getItem(id, authentication)
            .map(item -> {
                model.addAttribute("item", item);
                return "item";
            });
    }

    @PostMapping
    public Mono<String> handleChangeQuantity(
        @PathVariable("id") Long id,
        ServerWebExchange exchange,
        @AuthenticationPrincipal Authentication authentication
    ) {
        return exchange.getFormData()
            .flatMap(data -> {
                String action = data.getFirst("action");

                if (action == null) {
                    return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing form field 'action'"));
                }

                return storeFrontService.changeItemQuantity(id, action, authentication.getName())
                            .thenReturn( "redirect:/items/" + id);
            });
    }

    @ModelAttribute("_csrf")
    public Mono<CsrfToken> csrfToken(ServerWebExchange exchange) {
        return exchange.getAttribute(CsrfToken.class.getName());
    }
}
