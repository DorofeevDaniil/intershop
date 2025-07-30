package ru.custom.intershop.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.custom.intershop.service.ItemService;

@Controller
@RequestMapping("/items/{id}")
public class ItemController {
    private ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public Mono<String> handleShowItem(
        @PathVariable("id") Long id,
        Model model
    ) {
        return itemService.getItemById(id)
            .map(item -> {
                model.addAttribute("item", item);
                return "item";
            });
    }

    @PostMapping
    public Mono<String> handleChangeAmount(
        @PathVariable("id") Long id,
        ServerWebExchange exchange
    ) {
        return exchange.getFormData()
            .flatMap(data -> {
                String action = data.getFirst("action");

                if (action == null) {
                    return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing form field 'action'"));
                }

                return itemService.changeAmount(id, action)
                            .thenReturn( "redirect:/items/" + id);
            });
    }
}
