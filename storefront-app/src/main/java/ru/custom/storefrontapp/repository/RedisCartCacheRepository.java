package ru.custom.storefrontapp.repository;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Repository
public class RedisCartCacheRepository implements CartCacheRepository {
    private static final String CART_KEY = "cart";

    private final ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;

    public RedisCartCacheRepository(ReactiveRedisTemplate<String, Object> redisTemplate) {
        this.reactiveRedisTemplate = redisTemplate;
    }

    @Override
    public Flux<Map.Entry<Object, Object>> findAll() {
        return reactiveRedisTemplate.opsForHash()
            .entries(CART_KEY);
    }

    @Override
    public Mono<Integer> findById(String id) {
        return reactiveRedisTemplate.opsForHash()
            .get(CART_KEY, id)
            .map(count -> Integer.valueOf(count.toString()))
            .defaultIfEmpty(0);
    }

    @Override
    public Mono<Long> deleteItem(String id) {
        return reactiveRedisTemplate.opsForHash()
            .remove(CART_KEY, id);
    }

    @Override
    public Mono<Long> updateItemAmount(String id, Integer increment) {
        return reactiveRedisTemplate.opsForHash()
            .increment(CART_KEY, id, increment);
    }

    @Override
    public Mono<Long> deleteCart() {
        return reactiveRedisTemplate.delete(CART_KEY);
    }
}
