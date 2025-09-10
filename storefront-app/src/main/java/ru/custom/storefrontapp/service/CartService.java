package ru.custom.storefrontapp.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.custom.storefrontapp.repository.CartCacheRepository;

import java.util.Map;

@Service
public class CartService {
    private final CartCacheRepository cartCacheRepository;

    public CartService(CartCacheRepository cartCacheRepository) {
        this.cartCacheRepository = cartCacheRepository;
    }

    @PreAuthorize("hasRole('USER')")
    public Mono<Boolean> changeQuantity(Long id, String action, Long userId) {
        return switch (action.toUpperCase()) {
            case "PLUS" -> incrementItem(id, userId);
            case "MINUS" -> decrementItem(id, userId);
            case "DELETE" -> removeItem(id, userId);
            default -> throw new IllegalStateException("Unexpected value: " + action.toUpperCase());
        };
    }

    @PreAuthorize("hasRole('USER')")
    public Flux<Map.Entry<Object, Object>> getCart(Long userId) {
        return cartCacheRepository.findAll(userId);
    }

    @PreAuthorize("hasRole('USER')")
    public Mono<Integer> getItemQuantity(Long id, Long userId) {
        return cartCacheRepository.findById(id.toString(), userId);
    }

    @PreAuthorize("hasRole('USER')")
    public Mono<Long> cleanCart(Long userId) {
        return cartCacheRepository.deleteCart(userId);
    }

    private Mono<Boolean> incrementItem(Long id, Long userId) {
        return cartCacheRepository.updateItemQuantity(id.toString(), 1, userId)
            .thenReturn(true);
    }

    private Mono<Boolean> decrementItem(Long id, Long userId) {
        return cartCacheRepository.findById(id.toString(), userId)
            .flatMap(count -> {
                if (count <= 1) {
                    return cartCacheRepository.deleteItem(id.toString(), userId)
                        .thenReturn(true);
                } else {
                    return cartCacheRepository.updateItemQuantity(id.toString(), -1, userId)
                        .thenReturn(true);
                }
            });
    }

    private Mono<Boolean> removeItem(Long id, Long userId) {
        return cartCacheRepository.deleteItem(id.toString(), userId)
            .thenReturn(true);
    }
}
