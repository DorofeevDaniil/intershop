package ru.custom.intershop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @GetMapping
    public String handleShowOrders(Model model) {


        return "orders";
    }

    @GetMapping("/{id}")
    public String handleGetOrder(
        @PathVariable("id") Long id,
        @RequestParam("newOrder") Boolean newOrder,
        Model model
    ) {

        return "order";
    }
}
