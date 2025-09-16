package ru.custom.storefrontapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DefaultController {
    @GetMapping("/")
    public String handleMainRedirect(Model model) {
        return "redirect:/main/items";
    }

    @GetMapping
    public String handleMainRedirectFromEmpty(Model model) {
        return "redirect:/main/items";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }
}
