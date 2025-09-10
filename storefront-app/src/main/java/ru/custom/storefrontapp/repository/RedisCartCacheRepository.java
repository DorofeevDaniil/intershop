package ru.custom.storefrontapp.repository;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

@Repository
public class RedisCartCacheRepository implements CartCacheRepository {
    private static final String CART_KEY = "cart";
    private static final Duration TTL = Duration.ofMinutes(60);

    private final ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;

    public RedisCartCacheRepository(ReactiveRedisTemplate<String, Object> redisTemplate) {
        this.reactiveRedisTemplate = redisTemplate;
    }

    private String getCartKey(Long userId) {
        return CART_KEY + ":" + userId;
    }

    @Override
    public Flux<Map.Entry<Object, Object>> findAll(Long userId) {
        return reactiveRedisTemplate.opsForHash()
            .entries(getCartKey(userId));
    }

    @Override
    public Mono<Integer> findById(String id, Long userId) {
        return reactiveRedisTemplate.opsForHash()
            .get(getCartKey(userId), id)
            .map(count -> Integer.valueOf(count.toString()))
            .defaultIfEmpty(0);
    }

    @Override
    public Mono<Long> deleteItem(String id, Long userId) {
        return reactiveRedisTemplate.opsForHash()
            .remove(getCartKey(userId), id);
    }

    @Override
    public Mono<Long> updateItemQuantity(String id, Integer increment, Long userId) {
        return reactiveRedisTemplate.opsForHash()
            .increment(getCartKey(userId), id, increment)
            .flatMap(amount ->
                reactiveRedisTemplate.expire(getCartKey(userId), TTL)
                    .thenReturn(amount)
            );
    }

    @Override
    public Mono<Long> deleteCart(Long userId) {
        return reactiveRedisTemplate.delete(getCartKey(userId));
    }
}
