package ru.custom.intershop.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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
    public String handleBuyItems(HttpSession session, RedirectAttributes redirectAttributes) {
        CartDto cart = (CartDto) session.getAttribute("cart");

        if (cart == null || cart.isEmpty()) return "redirect:/cart/items";

        Long id = orderService.createOrder(CartMapper.toCart(cart));
        session.removeAttribute("cart");

        redirectAttributes.addAttribute("newOrder", true);
        return "redirect:/orders/" + id;
    }
}
