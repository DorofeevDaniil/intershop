package ru.custom.intershop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.custom.intershop.model.Order;
import ru.custom.intershop.service.OrderService;

import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private OrderService orderService;

     public OrderController(OrderService orderService) {
         this.orderService = orderService;
     }

    @GetMapping
    public String handleShowOrders(Model model) {
        List<Order> orders = orderService.getAllOrders();

        model.addAttribute("orders", orders);

        return "orders";
    }

    @GetMapping("/{id}")
    public String handleGetOrder(
        @PathVariable("id") Long id,
        @RequestParam(value = "newOrder", defaultValue = "false") Boolean newOrder,
        Model model
    ) {
        Order order = orderService.getOrderById(id);

        model.addAttribute("order", order);
        model.addAttribute("newOrder", newOrder);

        return "order";
    }
}
