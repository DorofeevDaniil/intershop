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
import java.util.List;

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
        Model model
    ) {
        List<Item> itemsList;
        Long totalElements;

        if (search == null || search.isBlank()) {
            itemsList = itemService.getPage(page, pageSize, sort);
            totalElements = itemService.getTotalCount();
        } else {
            itemsList =  itemService.findBySearchParams(page, pageSize, sort, search);
            model.addAttribute("search", search);
            totalElements = itemService.getTotalSearchedElements(search);
        }

        List<List<Item>> rows = new ArrayList<>();
        for (int i = 0; i < itemsList.size(); i += 3) {
            rows.add(itemsList.subList(i, Math.min(i + 3, itemsList.size())));
        }

        model.addAttribute("items", rows);
        model.addAttribute("sort", sort);
        model.addAttribute("paging", new Paging(rows, page, pageSize, totalElements));

        return "main";
    }

    @PostMapping("/{id}")
    public String handleChangeAmount(
        @PathVariable("id") Long id,
        @RequestParam("action") String action,
        @RequestHeader(value = "referer", required = false) final String referer,
        HttpSession session
    ) {
        itemService.changeAmount(id, action);

        if (referer == null) {
            return "redirect:/main/items";
        }

        String basePath = session.getServletContext().getContextPath();

        return "redirect:" + referer.substring(referer.indexOf(basePath) + basePath.length());
    }
}
