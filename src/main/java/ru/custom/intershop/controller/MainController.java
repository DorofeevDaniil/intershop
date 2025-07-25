package ru.custom.intershop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
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
    public Mono<String> handleGetItems(
        @RequestParam(name = "pageNumber", defaultValue = "1") int page,
        @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
        @RequestParam(name = "search", required = false) String search,
        @RequestParam(name = "sort", required = true, defaultValue = "NO") String sort,
        Model model
    ) {
        Flux<Item> itemsFlux;
        Mono<Long> totalElementsMono;

        if (search == null || search.isBlank()) {
            itemsFlux = itemService.getPage(page, pageSize, sort);
            totalElementsMono = itemService.getTotalCount();
        } else {
            itemsFlux =  itemService.findBySearchParams(page, pageSize, sort, search);
            totalElementsMono = itemService.getTotalSearchedElements(search);

            model.addAttribute("search", search);
        }

        return Mono.zip(itemsFlux.collectList(), totalElementsMono)
            .map(tuple -> {
                List<Item> itemsList = tuple.getT1();
                Long totalElements = tuple.getT2();

                List<List<Item>> rows = new ArrayList<>();
                for (int i = 0; i < itemsList.size(); i += 3) {
                    rows.add(itemsList.subList(i, Math.min(i + 3, itemsList.size())));
                }

                model.addAttribute("items", rows);
                model.addAttribute("sort", sort);
                model.addAttribute("paging", new Paging(rows, page, pageSize, totalElements));

                return "main";
            });
    }

    @PostMapping("/{id}")
    public Mono<String> handleChangeAmount(
        @PathVariable("id") Long id,
        @RequestHeader(value = "referer", required = false) final String referer,
        ServerWebExchange exchange
    ) {
        return exchange.getFormData().flatMap(data -> {
            String action = data.getFirst("action");

            return itemService.changeAmount(id, action)
                .then(Mono.fromSupplier(() -> {
                    if (referer == null) {
                        return "redirect:/main/items";
                    }

                    String basePath = exchange.getRequest().getPath().contextPath().value();

                    return "redirect:" + referer.substring(referer.indexOf(basePath) + basePath.length());
                }));
        });
    }
}
