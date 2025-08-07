package ru.custom.intershop.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.custom.intershop.model.Item;

import java.math.BigDecimal;
import java.util.Map;
import java.util.function.Supplier;

@Slf4j
@Service
public class ItemCacheService {
    private final ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;
    private final ReactiveZSetOperations<String, Object> zSetOperations;
    private final ReactiveHashOperations<String, String, Object> hashOperations;

    public ItemCacheService(ReactiveRedisTemplate<String, Object> reactiveRedisTemplate) {
        this.reactiveRedisTemplate = reactiveRedisTemplate;
        this.zSetOperations = reactiveRedisTemplate.opsForZSet();
        this.hashOperations = reactiveRedisTemplate.opsForHash();
    }

    public Mono<Void> ensureCacheLoadedIfMissing(Supplier<Flux<Item>> itemsSupplier) {
        return reactiveRedisTemplate.hasKey("items:by_price")
            .flatMap(gotKey -> {
                if (Boolean.TRUE.equals(gotKey)) {
                    return Mono.empty();
                } else {
                    return cacheItems(itemsSupplier);
                }
            });
    }

    public Flux<Item> getSortedPage(String sortBy, int page, int size) {
        String zsetKey = switch (sortBy) {
            case "PRICE" -> "products:by_price";
            case "ALPHA" -> "products:by_title";
            default -> throw new IllegalArgumentException("Unknown sort: " + sortBy);
        };

        int start = (page - 1) * size;
        int end = start + size - 1;

        return zSetOperations.range(zsetKey, start, end)
            .flatMap(idObj -> {
                String hashKey = getKey(idObj.toString());

                return hashOperations.entries(hashKey)
                    .collectMap(Map.Entry::getKey, Map.Entry::getValue)
                    .map(data -> {
                        Item item = new Item();
                        item.setId(Long.valueOf(idObj.toString()));
                        item.setTitle((String) data.get("title"));
                        item.setDescription((String) data.get("description"));
                        item.setImgPath((String) data.get("image"));
                        item.setPrice(BigDecimal.valueOf(Double.parseDouble(data.get("price").toString())));

                        return item;
                    });
            });
    }

    private Mono<Void> cacheItems(Supplier<Flux<Item>> itemsSupplier) {
        return itemsSupplier.get()
            .flatMap(this::cacheItem)
            .then();
    }

    private Mono<Void> cacheItem(Item item) {
        String hashKey = getKey(item.getId().toString());

        return hashOperations.put(hashKey, "title", item.getTitle())
            .doOnError(e -> log.error("Failed to put 'title' for item {}", item.getId(), e))
            .then(hashOperations.put(hashKey, "description", item.getDescription())
                .doOnError(e -> log.error("Failed to put 'description' for item {}", item.getId(), e)))
            .then(hashOperations.put(hashKey, "price", item.getPrice())
                .doOnError(e -> log.error("Failed to put 'price' for item {}", item.getId(), e)))
            .then(hashOperations.put(hashKey, "image", item.getImgPath())
                .doOnError(e -> log.error("Failed to put 'image' for item {}", item.getId(), e)))
            .then(zSetOperations.add("items:by_title", item.getId(), item.getTitle().toLowerCase().hashCode())
                .doOnNext(success -> {
                    if (Boolean.FALSE.equals(success)) {
                        log.warn("ZSet insert by_title failed for item {}", item.getId());
                    }
                })
                .doOnError(e -> log.error("ZSet insert by_title error for item {}", item.getId(), e)))
            .then(zSetOperations.add("items:by_price", item.getId(), item.getPrice().doubleValue())
                .doOnNext(success -> {
                    if (Boolean.FALSE.equals(success)) {
                        log.warn("ZSet insert by_price failed for item {}", item.getId());
                    }
                })
                .doOnError(e -> log.error("ZSet insert by_price error for item {}", item.getId(), e)))
            .then();
    }

    private String getKey(String id) {
        return "item:" + id;
    }
}
