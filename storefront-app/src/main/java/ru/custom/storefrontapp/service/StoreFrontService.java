package ru.custom.storefrontapp.service;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.custom.storefrontapp.dto.CartDto;
import ru.custom.storefrontapp.dto.ItemDto;
import ru.custom.storefrontapp.model.Item;
import ru.custom.storefrontapp.pagination.PagedResult;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class StoreFrontService {
    private final ItemService itemService;
    private final CartService cartService;

    public StoreFrontService(ItemService itemService, CartService cartService) {
        this.itemService = itemService;
        this.cartService = cartService;
    }

    public Mono<ItemDto> getItem(Long id) {
        return itemService.getItemCardById(id)
            .flatMap(item ->
                cartService.getItemAmount(id)
                    .map(count -> {
                        item.setCount(count);
                        return item;
                    })
            );
    }

    public Mono<Boolean> changeItemAmount(Long id, String action) {
        return cartService.changeAmount(id, action);
    }

    public Mono<PagedResult<List<ItemDto>>> getPage(int page, int pageSize, String sort, String searchText) {
        return itemService.searchAndPaginate(page, pageSize, sort, searchText)
            .flatMap(tuple -> {
                List<ItemDto> items = tuple.getT1();
                long total = tuple.getT2();

                return Flux.fromIterable(items)
                    .flatMap(item ->
                        cartService.getItemAmount(item.getId())
                            .defaultIfEmpty(0)
                            .map(count -> {
                                item.setCount(count);
                                return item;
                            })
                    )
                    .collectList()
                    .map(itemsList -> new PagedResult<>(splitRows(itemsList, 3), page, pageSize, total));
            });
    }

    public Mono<ItemDto> createItem(Item item, FilePart image) {
        return itemService.addItem(item, image);
    }

    public Mono<CartDto> getCart() {
        return cartService.getCart()
            .flatMap(cart -> {
                Long itemId = Long.parseLong(cart.getKey().toString());
                Integer count = Integer.parseInt(cart.getValue().toString());

                return itemService.getItemCardById(itemId)
                    .map(item -> {
                        item.setCount(count);

                        return item;
                    });
            })
            .collectList()
            .flatMap(items -> {
                BigDecimal total = items.stream()
                    .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getCount())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

                return Mono.just(new CartDto(items, total));
            });
    }

    private List<List<ItemDto>> splitRows(List<ItemDto> items, int rowSize) {
        List<List<ItemDto>> rows = new ArrayList<>();
        for (int i = 0; i < items.size(); i += rowSize) {
            rows.add(items.subList(i, Math.min(i + rowSize, items.size())));
        }
        return rows;
    }
}
