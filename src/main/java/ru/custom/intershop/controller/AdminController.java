package ru.custom.intershop.controller;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
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

                Item item = new Item();

                item.setTitle(title);
                item.setPrice(price);
                item.setDescription(description);
                item.setImgPath(image.filename());
                item.setCount(0);

                return itemService.addItem(item, image);
            })
            .thenReturn("redirect:/admin/add");
    }
}
