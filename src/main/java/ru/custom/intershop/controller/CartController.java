package ru.custom.intershop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;
import ru.custom.intershop.service.CartService;
import ru.custom.intershop.mapper.CartMapper;

@Controller
@RequestMapping("/cart")
public class CartController {
    private CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }


    @GetMapping("/items")
    public Mono<String> handleShowItems(WebSession session, Model model) {
        return cartService.getCart()
            .flatMap(cart -> Mono.just(CartMapper.toCartDto(cart)))
            .map(cartDto -> {
                model.addAttribute("items", cartDto.getItems());
                model.addAttribute("total", cartDto.getTotal());
                model.addAttribute("empty", cartDto.isEmpty());

                session.getAttributes().put("cart", cartDto);

                return "cart";
            });
    }

    @PostMapping("/items/{id}")
    public Mono<String> handleChangeAmount (
        @PathVariable("id") Long id,
        @RequestParam(name = "action", required = true) String action
    ) {
        return cartService.changeItemAmount(id, action)
            .thenReturn("redirect:/cart/items");
    }
}
