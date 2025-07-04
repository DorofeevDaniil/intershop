package ru.custom.intershop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/buy")
public class BuyController {

    @PostMapping
    public String handleBuyItems(RedirectAttributes redirectAttributes) {
        long id = 1L;

        redirectAttributes.addAttribute("newOrder", true);
        return "redirect:/orders/" + id;
    }
}
