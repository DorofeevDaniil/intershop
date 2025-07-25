package ru.custom.intershop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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
        @RequestParam(name = "action", required = true) String action
    ) {
        return itemService.changeAmount(id, action)
            .thenReturn( "redirect:/items/" + id);
    }
}
