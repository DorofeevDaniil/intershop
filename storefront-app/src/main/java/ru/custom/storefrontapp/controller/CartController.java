package ru.custom.storefrontapp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;
import ru.custom.storefrontapp.service.StoreFrontService;

@Controller
@RequestMapping("/cart")
public class CartController {
    private StoreFrontService storeFrontService;

    public CartController(StoreFrontService storeFrontService) {
        this.storeFrontService = storeFrontService;
    }


    @GetMapping("/items")
    public Mono<String> handleShowItems(WebSession session, Model model) {
        return storeFrontService.getCart()
            .flatMap(Mono::just)
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
        ServerWebExchange exchange
    ) {
        return exchange.getFormData()
            .flatMap(data -> {
                String action = data.getFirst("action");

                if (action == null) {
                    return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing form field 'action'"));
                }

                return storeFrontService.changeItemAmount(id, action)
                    .thenReturn("redirect:/cart/items");
            });
    }
}
