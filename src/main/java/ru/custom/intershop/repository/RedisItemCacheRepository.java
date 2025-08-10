package ru.custom.intershop.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import ru.custom.intershop.dto.ItemDto;
import ru.custom.intershop.dto.ItemListDto;

import java.util.List;

@Repository
public class RedisItemCacheRepository implements ItemCacheRepository {
    private static final String ITEM_CARD_PREFIX = "item:card:";
    private static final String ITEMS_LIST_KEY = "items:list";

    private final ReactiveRedisTemplate<String, Object> redisTemplate;
    private final ReactiveRedisTemplate<String, ItemDto> itemDtoReactiveRedisTemplate;

    public RedisItemCacheRepository(ReactiveRedisTemplate<String, Object> redisTemplate,
                            ReactiveRedisTemplate<String, ItemDto> itemDtoReactiveRedisTemplate) {
        this.redisTemplate = redisTemplate;
        this.itemDtoReactiveRedisTemplate = itemDtoReactiveRedisTemplate;
    }


    @Override
    public Mono<List<ItemListDto>> findAll() {
        return redisTemplate.opsForValue().get(ITEMS_LIST_KEY)
            .flatMap(obj -> {
                if (obj == null) return Mono.empty();
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    String json = mapper.writeValueAsString(obj);
                    List<ItemListDto> list = mapper.readValue(json, new TypeReference<List<ItemListDto>>() {});
                    return Mono.just(list);
                } catch (JsonProcessingException e) {
                    return Mono.error(e);
                }
            });
    }

    @Override
    public Mono<List<ItemListDto>> saveAll(List<ItemListDto> list) {
        return redisTemplate.opsForValue().set(ITEMS_LIST_KEY, list).thenReturn(list);
    }

    @Override
    public Mono<ItemDto> findById(Long id) {
        String key = ITEM_CARD_PREFIX + id;
        return itemDtoReactiveRedisTemplate.opsForValue().get(key);
    }

    @Override
    public Mono<Long> deleteList() {
        return redisTemplate.delete(ITEMS_LIST_KEY);
    }

    @Override
    public Mono<Boolean> saveItem(Long id, ItemDto item) {
        String cardKey = ITEM_CARD_PREFIX + id;
        return itemDtoReactiveRedisTemplate.opsForValue().set(cardKey, item);
    }
}
