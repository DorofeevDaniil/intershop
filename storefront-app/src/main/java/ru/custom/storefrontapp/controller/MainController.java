package ru.custom.storefrontapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.custom.storefrontapp.service.StoreFrontService;

@Controller
@RequestMapping("/main/items")
public class MainController {
    private final StoreFrontService storeFrontService;

    @Autowired
    public MainController(StoreFrontService storeFrontService) {
        this.storeFrontService = storeFrontService;
    }

    @GetMapping
    public Mono<String> handleGetItems(
        @RequestParam(name = "pageNumber", defaultValue = "1") int page,
        @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
        @RequestParam(name = "search", required = false) String search,
        @RequestParam(name = "sort", required = true, defaultValue = "NO") String sort,
        Model model
    ) {
        return storeFrontService.getPage(page, pageSize, sort, search)
            .map(pagedResult -> {
                model.addAttribute("search", search);
                model.addAttribute("items", pagedResult.getContent());
                model.addAttribute("sort", sort);
                model.addAttribute("paging", pagedResult);

                return "main";
            });
    }

    @PostMapping("/{id}")
    public Mono<String> handleChangeQuantity(
        @PathVariable("id") Long id,
        @RequestHeader(value = "referer", required = false) final String referer,
        ServerWebExchange exchange
    ) {
        return exchange.getFormData().flatMap(data -> {
            String action = data.getFirst("action");

            if (action == null) {
                return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing form field 'action'"));
            }

            return storeFrontService.changeItemQuantity(id, action)
                .then(Mono.fromSupplier(() -> {
                    if (referer == null) {
                        return "redirect:/main/items";
                    }

                    String basePath = exchange.getRequest().getPath().contextPath().value();

                    return "redirect:" + referer.substring(referer.indexOf(basePath) + basePath.length());
                }));
        });
    }

    @ModelAttribute("_csrf")
    public Mono<CsrfToken> csrfToken(ServerWebExchange exchange) {
        return exchange.getAttribute(CsrfToken.class.getName());
    }
}
