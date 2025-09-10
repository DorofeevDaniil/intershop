package ru.custom.storefrontapp.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;
import ru.custom.storefrontapp.service.OrderService;
import ru.custom.storefrontapp.service.StoreFrontService;

@Controller
@RequestMapping("/buy")
public class BuyController {
    private final OrderService orderService;
    private final StoreFrontService storeFrontService;

    public BuyController(OrderService orderService, StoreFrontService storeFrontService) {
        this.orderService = orderService;
        this.storeFrontService = storeFrontService;
    }

    @PostMapping
    public Mono<String> handleBuyItems(@AuthenticationPrincipal UserDetails userDetails) {
        return storeFrontService.getCart(userDetails.getUsername())
            .flatMap(cart -> {
                if (cart == null || cart.isEmpty()) return Mono.just("redirect:/cart/items");

                return orderService.createOrder(cart, userDetails.getUsername())
                    .map(id -> "redirect:/orders/" + id + "?newOrder=true");
            });
    }
}
