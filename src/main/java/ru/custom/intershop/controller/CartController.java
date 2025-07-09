package ru.custom.intershop.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.custom.intershop.dto.CartDto;
import ru.custom.intershop.model.Cart;
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
    public String handleShowItems(HttpSession session, Model model) {
        Cart cart = cartService.getCart();
        CartDto cartDto = CartMapper.toCartDto(cart);

        model.addAttribute("items", cartDto.getItems());
        model.addAttribute("total", cartDto.getTotal());
        model.addAttribute("empty", cartDto.isEmpty());

        session.setAttribute("cart", cartDto);

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
