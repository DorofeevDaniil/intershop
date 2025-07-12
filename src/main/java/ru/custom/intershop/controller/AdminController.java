package ru.custom.intershop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.custom.intershop.model.Item;
import ru.custom.intershop.service.ItemService;

import java.math.BigDecimal;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private ItemService itemService;

    public AdminController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public String showDefault() {
        return "redirect:/admin/add";
    }

    @GetMapping("/add")
    public String showAdd(Model model) {
        return "add-item";
    }

    @PostMapping("/add")
    public String handleAddItem(
        @RequestParam("title") String title,
        @RequestParam("price") BigDecimal price,
        @RequestParam("description") String description,
        @RequestParam("image") MultipartFile image) {

        Item item = new Item();

        item.setTitle(title);
        item.setPrice(price);
        item.setDescription(description);
        item.setImgPath(image.getOriginalFilename());
        item.setCount(0);

        itemService.addItem(item, image);

        return "redirect:/admin/add";
    }
}
