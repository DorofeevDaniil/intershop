package ru.custom.storefrontapp.service;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.custom.storefrontapp.dto.CartDto;
import ru.custom.storefrontapp.dto.ItemDto;
import ru.custom.storefrontapp.pagination.PagedResult;
import ru.custom.storefrontapp.util.SecurityUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StoreFrontService {
    private final ItemService itemService;
    private final CartService cartService;
    private final SecurityUtils securityUtils;
    private final UserManagementService userManagementService;

    public StoreFrontService(
            ItemService itemService,
            CartService cartService,
            SecurityUtils securityUtils,
            UserManagementService userManagementService) {
        this.itemService = itemService;
        this.cartService = cartService;
        this.securityUtils = securityUtils;
        this.userManagementService = userManagementService;
    }

    public Mono<ItemDto> getItem(Long id, Authentication auth) {
        Optional<String> username = securityUtils.getUsername(auth);

        return itemService.getItemCardById(id)
            .flatMap(item -> {
                if (username.isEmpty()) {
                    item.setCount(0);
                    return Mono.just(item);
                }
                return userManagementService.findUserByName(username.get())
                        .flatMap(user ->
                                cartService.getItemQuantity(id, user.getId())
                                        .map(count -> {
                                            item.setCount(count);
                                            return item;
                                        })
                        );
            });
    }

    @PreAuthorize("hasRole('USER')")
    public Mono<Boolean> changeItemQuantity(Long id, String action, String username) {
        return userManagementService.findUserByName(username)
            .flatMap(user ->
                cartService.changeQuantity(id, action, user.getId())
            );
    }

    public Mono<PagedResult<List<ItemDto>>> getPage(int page, int pageSize, String sort, String searchText, Authentication auth) {
        Optional<String> username = securityUtils.getUsername(auth);

        return itemService.searchAndPaginate(page, pageSize, sort, searchText)
            .flatMap(tuple -> {
                List<ItemDto> items = tuple.getT1();
                long total = tuple.getT2();

                return Flux.fromIterable(items)
                    .flatMapSequential(item -> {
                        if (username.isEmpty()) {
                            item.setCount(0);
                            return Mono.just(item);
                        }
                        return userManagementService.findUserByName(username.get())
                                .flatMap(user ->
                                        cartService.getItemQuantity(item.getId(), user.getId())
                                                .defaultIfEmpty(0)
                                                .map(count -> {
                                                    item.setCount(count);
                                                    return item;
                                                })
                                );
                    })
                    .collectList()
                    .map(itemsList -> new PagedResult<>(splitRows(itemsList, 3), page, pageSize, total));
            });
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ItemDto> createItem(ItemDto item, FilePart image) {
        return itemService.addItem(item, image);
    }

    @PreAuthorize("hasRole('USER')")
    public Mono<CartDto> getCart(String username) {
        return userManagementService.findUserByName(username)
            .flatMap(user -> cartService.getCart(user.getId())
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
                    }));
    }

    private List<List<ItemDto>> splitRows(List<ItemDto> items, int rowSize) {
        List<List<ItemDto>> rows = new ArrayList<>();
        for (int i = 0; i < items.size(); i += rowSize) {
            rows.add(items.subList(i, Math.min(i + rowSize, items.size())));
        }
        return rows;
    }
}
