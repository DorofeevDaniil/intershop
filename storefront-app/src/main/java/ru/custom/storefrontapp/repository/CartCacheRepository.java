package ru.custom.storefrontapp.repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface CartCacheRepository {
    Flux<Map.Entry<Object, Object>> findAll(Long userId);
    Mono<Long> updateItemQuantity(String id, Integer increment, Long userId);
    Mono<Integer> findById(String id, Long userId);
    Mono<Long> deleteItem(String id, Long userId);
    Mono<Long> deleteCart(Long userId);
}
