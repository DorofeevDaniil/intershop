package ru.custom.intershop.repository;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Repository
public class RedisCartCacheRepository implements CartCacheRepository {
    private static final String CART_KEY = "cart";

    private final ReactiveRedisTemplate<String, Object> redisTemplate;

    public RedisCartCacheRepository(ReactiveRedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Flux<Map.Entry<Object, Object>> findAll() {
        return redisTemplate.opsForHash()
            .entries(CART_KEY);
    }

    @Override
    public Mono<Integer> findById(String id) {
        return redisTemplate.opsForHash()
            .get(CART_KEY, id)
            .map(count -> Integer.valueOf(count.toString()))
            .defaultIfEmpty(0);
    }

    @Override
    public Mono<Long> deleteItem(String id) {
        return redisTemplate.opsForHash()
            .remove(CART_KEY, id);
    }

    @Override
    public Mono<Long> updateItemAmount(String id, Integer increment) {
        return redisTemplate.opsForHash()
            .increment(CART_KEY, id, increment);
    }

    @Override
    public Mono<Long> deleteCart() {
        return redisTemplate.delete(CART_KEY);
    }
}
