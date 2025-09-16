package ru.custom.storefrontapp.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.custom.storefrontapp.service.OrderService;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

     public OrderController(OrderService orderService) {
         this.orderService = orderService;
     }

    @GetMapping
    public Mono<String> handleShowOrders(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        return orderService.getAllOrders(userDetails.getUsername())
            .collectList()
            .map(orders -> {
                model.addAttribute("orders", orders);

                return "orders";
            });
    }

    @GetMapping("/{id}")
    public Mono<String> handleGetOrder(
        @PathVariable("id") Long id,
        @RequestParam(value = "newOrder", defaultValue = "false") Boolean newOrder,
        Model model
    ) {
        return orderService.getOrderById(id)
            .map(order -> {
                model.addAttribute("order", order);
                model.addAttribute("newOrder", newOrder);

                return "order";
            });
    }
}
