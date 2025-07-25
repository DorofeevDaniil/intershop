package ru.custom.intershop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;
import ru.custom.intershop.dto.CartDto;
import ru.custom.intershop.mapper.CartMapper;
import ru.custom.intershop.service.OrderService;

@Controller
@RequestMapping("/buy")
public class BuyController {
    private OrderService orderService;

    public BuyController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public Mono<String> handleBuyItems(WebSession session) {
        CartDto cart = session.getAttribute("cart");

        if (cart == null || cart.isEmpty()) return Mono.just("redirect:/cart/items");

        return orderService.createOrder(CartMapper.toCart(cart))
            .map(id -> {
                session.getAttributes().remove("cart");
                return "redirect:/orders/" + id + "?newOrder=true";
            });
    }
}
