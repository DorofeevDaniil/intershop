package ru.custom.storefrontapp.controller;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.custom.storefrontapp.dto.ItemDto;
import ru.custom.storefrontapp.service.StoreFrontService;

import java.math.BigDecimal;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final StoreFrontService storeFrontService;

    public AdminController(StoreFrontService storeFrontService) {
        this.storeFrontService = storeFrontService;
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
    public Mono<String> handleAddItem(
        @RequestPart("title") Mono<String> titleMono,
        @RequestPart("price") Mono<String> priceMono,
        @RequestPart("description") Mono<String> descriptionMono,
        @RequestPart("image") Mono<FilePart> imageMono) {

        return Mono.zip(titleMono, priceMono, descriptionMono, imageMono)
            .flatMap(tuple -> {
                String title = tuple.getT1();
                BigDecimal price = new BigDecimal(tuple.getT2());
                String description = tuple.getT3();
                FilePart image = tuple.getT4();

                ItemDto item = new ItemDto();

                item.setTitle(title);
                item.setPrice(price);
                item.setDescription(description);
                item.setImgPath(image.filename());
                item.setCount(0);

                return storeFrontService.createItem(item, image);
            })
            .thenReturn("redirect:/admin/add");
    }

    @ModelAttribute("_csrf")
    public Mono<CsrfToken> csrfToken(ServerWebExchange exchange) {
        return exchange.getAttribute(CsrfToken.class.getName());
    }
}
