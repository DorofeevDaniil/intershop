package ru.custom.intershop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.custom.intershop.model.Item;
import ru.custom.intershop.service.ItemService;

@Controller
@RequestMapping("/items/{id}")
public class ItemController {
    private ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public String handleShowItem(
        @PathVariable("id") Long id,
        Model model
    ) {
        Item item = itemService.getItemById(id);
        model.addAttribute("item", item);

        return "item";
    }

    @PostMapping
    public String handleChangeAmount(
        @PathVariable("id") Long id,
        @RequestParam(name = "action", required = true) String action
    ) {
        itemService.changeAmount(id, action);

        return "redirect:/items/" + id;
    }
}
