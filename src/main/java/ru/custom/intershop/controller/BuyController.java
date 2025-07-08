package ru.custom.intershop.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.custom.intershop.model.Cart;
import ru.custom.intershop.service.OrderService;

@Controller
@RequestMapping("/buy")
public class BuyController {
    private OrderService orderService;

    public BuyController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public String handleBuyItems(HttpSession session, RedirectAttributes redirectAttributes) {
        Cart cart = (Cart) session.getAttribute("cart");

        if (cart == null || cart.isEmpty()) return "redirect:/cart/items";

        Long id = orderService.createOrder(cart);
        session.removeAttribute("cart");

        return "redirect:/orders/" + id;
    }
}
