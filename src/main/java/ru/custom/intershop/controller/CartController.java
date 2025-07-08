package ru.custom.intershop.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.custom.intershop.model.Cart;
import ru.custom.intershop.service.CartService;

@Controller
@RequestMapping("/cart")
public class CartController {
    private CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }


    @GetMapping("/items")
    public String handleShowItems(HttpSession session, Model model) {
        Cart cart = cartService.getCart();

        model.addAttribute("items", cart.getItems());
        model.addAttribute("total", cart.getTotal());
        model.addAttribute("empty", cart.isEmpty());

        session.setAttribute("cart", cart);

        return "cart";
    }

    @PostMapping("/items/{id}")
    public String handleChangeAmount (
        @PathVariable("id") Long id,
        @RequestParam(name = "action", required = true) String action
    ) {
        cartService.changeItemAmount(id, action);

        return "redirect:/cart/items";
    }
}
