package ru.custom.intershop.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.custom.intershop.model.Item;
import ru.custom.intershop.service.CartService;
import ru.custom.intershop.service.ItemService;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/items/{id}")
public class ItemController {
    private CartService cartService;
    private ItemService itemService;

    public ItemController(CartService cartService, ItemService itemService) {
        this.cartService = cartService;
        this.itemService = itemService;
    }

    @GetMapping
    public String handleShowItem(
        @PathVariable("id") Long id,
        HttpSession session,
        Model model
    ) {
        Item item = itemService.getItemById(id);
        model.addAttribute("item", item);

        return "item";
    }

    @PostMapping
    public String handleChangeAmount(
        @PathVariable("id") Long id,
        @RequestParam(name = "action", required = true) String action,
        HttpSession session
    ) {
        itemService.changeAmount(id, action);

        return "redirect:/items/" + id;
    }
}
