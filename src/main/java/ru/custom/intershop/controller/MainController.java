package ru.custom.intershop.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.custom.intershop.model.Item;
import ru.custom.intershop.pagination.Paging;
import ru.custom.intershop.service.ItemService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/main/items")
public class MainController {
    private final ItemService itemService;

    @Autowired
    public MainController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public String handleGetItems(
        @RequestParam(name = "pageNumber", defaultValue = "1") int page,
        @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
        @RequestParam(name = "search", required = false) String search,
        @RequestParam(name = "sort", required = true, defaultValue = "NO") String sort,
        Model model,
        HttpSession session
    ) {
        List<Item> itemsList = itemService.getAll();
        Long totalElements = itemService.getTotalCount();

        List<List<Item>> rows = new ArrayList<>();
        for (int i = 0; i < itemsList.size(); i += 3) {
            rows.add(itemsList.subList(i, Math.min(i + 3, itemsList.size())));
        }

        model.addAttribute("items", rows);
        model.addAttribute("paging", new Paging(rows, page, pageSize, totalElements));

        return "main";
    }

    @PostMapping("/{id}")
    public String handleAddItem(
        @PathVariable("id") Long id,
        @RequestParam("action") String action,
        HttpSession session
    ) {
        itemService.changeAmount(id, action);

        return "redirect:/main/items";
    }
}
