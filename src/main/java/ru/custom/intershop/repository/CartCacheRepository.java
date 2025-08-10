package ru.custom.intershop.repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface CartCacheRepository {
    Flux<Map.Entry<Object, Object>> findAll();
    Mono<Long> updateItemAmount(String id, Integer increment);
    Mono<Integer> findById(String id);
    Mono<Long> deleteItem(String id);
    Mono<Long> deleteCart();
}
