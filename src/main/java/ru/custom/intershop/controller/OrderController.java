package ru.custom.intershop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;
import ru.custom.intershop.service.OrderService;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private OrderService orderService;

     public OrderController(OrderService orderService) {
         this.orderService = orderService;
     }

    @GetMapping
    public Mono<String> handleShowOrders(Model model) {
        return orderService.getAllOrders()
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
