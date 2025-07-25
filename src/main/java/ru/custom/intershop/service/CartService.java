package ru.custom.intershop.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.custom.intershop.model.Cart;
import ru.custom.intershop.model.Item;

import java.math.BigDecimal;

@Service
public class CartService {
    private ItemService itemService;

    public CartService(ItemService itemService) {
        this.itemService = itemService;
    }

    public Mono<Cart> getCart() {
        return itemService.getAllInCart()
            .collectList()
            .flatMap(items -> {
                BigDecimal total = items.stream()
                    .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getCount())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

                return Mono.just(new Cart(items, total));
            });
    }

    public Mono<Item> changeItemAmount(Long id, String action) {
        return itemService.changeAmount(id, action);
    }

}
