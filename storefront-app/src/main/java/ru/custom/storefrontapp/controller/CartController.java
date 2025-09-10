package ru.custom.storefrontapp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;
import ru.custom.storefrontapp.service.PaymentService;
import ru.custom.storefrontapp.service.StoreFrontService;

import java.math.BigDecimal;

@Controller
@RequestMapping("/cart")
public class CartController {
    private StoreFrontService storeFrontService;
    private final PaymentService paymentService;

    public CartController(StoreFrontService storeFrontService, PaymentService paymentService) {
        this.storeFrontService = storeFrontService;
        this.paymentService = paymentService;
    }


    @GetMapping("/items")
    public Mono<String> handleShowItems(
            WebSession session,
            Model model,
            @AuthenticationPrincipal UserDetails userDetails) {
        return storeFrontService.getCart(userDetails.getUsername())
            .flatMap(cartDto -> {
                model.addAttribute("items", cartDto.getItems());
                model.addAttribute("total", cartDto.getTotal());
                model.addAttribute("empty", cartDto.isEmpty());

                return paymentService.getBalance()
                    .map(balance -> {
                        model.addAttribute("enoughMoney", balance.subtract(cartDto.getTotal()).compareTo(BigDecimal.ZERO) > 0);
                        return "cart";
                    });
            });
    }

    @PostMapping("/items/{id}")
    public Mono<String> handleChangeQuantity (
        @PathVariable("id") Long id,
        ServerWebExchange exchange,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        return exchange.getFormData()
            .flatMap(data -> {
                String action = data.getFirst("action");

                if (action == null) {
                    return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing form field 'action'"));
                }

                return storeFrontService.changeItemQuantity(id, action, userDetails.getUsername())
                    .thenReturn("redirect:/cart/items");
            });
    }

    @ModelAttribute("_csrf")
    public Mono<CsrfToken> csrfToken(ServerWebExchange exchange) {
        return exchange.getAttribute(CsrfToken.class.getName());
    }
}
