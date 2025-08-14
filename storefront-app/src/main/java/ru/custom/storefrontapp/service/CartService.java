package ru.custom.storefrontapp.service;

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

    public Mono<Boolean> changeQuantity(Long id, String action) {
        return switch (action.toUpperCase()) {
            case "PLUS" -> incrementItem(id);
            case "MINUS" -> decrementItem(id);
            case "DELETE" -> removeItem(id);
            default -> throw new IllegalStateException("Unexpected value: " + action.toUpperCase());
        };
    }

    public Flux<Map.Entry<Object, Object>> getCart() {
        return cartCacheRepository.findAll();
    }

    public Mono<Integer> getItemQuantity(Long id) {
        return cartCacheRepository.findById(id.toString());
    }

    public Mono<Long> cleanCart() {
        return cartCacheRepository.deleteCart();
    }

    private Mono<Boolean> incrementItem(Long id) {
        return cartCacheRepository.updateItemQuantity(id.toString(), 1)
            .thenReturn(true);
    }

    private Mono<Boolean> decrementItem(Long id) {
        return cartCacheRepository.findById(id.toString())
            .flatMap(count -> {
                if (count <= 1) {
                    return cartCacheRepository.deleteItem(id.toString())
                        .thenReturn(true);
                } else {
                    return cartCacheRepository.updateItemQuantity(id.toString(), -1)
                        .thenReturn(true);
                }
            });
    }

    private Mono<Boolean> removeItem(Long id) {
        return cartCacheRepository.deleteItem(id.toString())
            .thenReturn(true);
    }
}
