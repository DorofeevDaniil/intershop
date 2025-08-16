package ru.custom.storefrontapp.integration.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import ru.custom.storefrontapp.dto.ItemDto;
import ru.custom.storefrontapp.dto.ItemListDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemCacheRepositoryTest extends BaseCacheRepository {
    private ItemListDto firstListItem;
    private ItemListDto secondListItem;
    private ItemDto firstItem;
    private ItemDto secondItem;

    @BeforeEach
    void populateCache() {
        hashOps = redisTemplate.opsForHash();

        redisTemplate.delete("items:list").block();
        redisTemplate.delete("item:card:1").block();
        redisTemplate.delete("item:card:2").block();

        firstListItem = populateItemListDto();
        secondListItem = populateItemListDto();
        secondListItem.setId(firstListItem.getId() + 1);

        firstItem = populateItemDto();
        secondItem = populateItemDto();
        secondItem.setId(firstItem.getId() + 1);

        Flux<Void> insert = Flux.concat(
            redisTemplate.opsForValue().set("items:list", List.of(firstListItem, secondListItem)).then(),
            redisTemplate.opsForValue().set("item:card:1", firstItem).then(),
            redisTemplate.opsForValue().set("item:card:2", secondItem).then()
        );

        insert.blockLast();
    }

    @Test
    void findAll_shouldReturnItemsList() {
        StepVerifier.create(itemCacheRepository.findAll())
            .assertNext(items -> {
                    assertEquals(firstListItem, items.get(0));
                    assertEquals(secondListItem, items.get(1));
                }
            ).verifyComplete();
    }

    @Test
    void saveAll_shouldSaveAndReturnItemsList() {
        redisTemplate.delete("items:list").block();

        StepVerifier.create(itemCacheRepository.saveAll(List.of(firstListItem, secondListItem)))
            .assertNext(items -> {
                    assertEquals(firstListItem, items.get(0));
                    assertEquals(secondListItem, items.get(1));
                }
            ).verifyComplete();
    }

    @Test
    void deleteList_shouldSaveAndReturnItemsList() {
        StepVerifier.create(itemCacheRepository.deleteList())
            .assertNext(removed -> assertEquals(1L, removed)
            ).verifyComplete();
    }

    @Test
    void findById_shouldReturnItem() {
        StepVerifier.create(itemCacheRepository.findById(TEST_ID))
            .assertNext(item -> assertEquals(firstItem, item)
            ).verifyComplete();
    }

    @Test
    void saveItem_shouldSaveAndReturnResult() {
        redisTemplate.delete("item:card:1").block();
        redisTemplate.delete("item:card:2").block();

        StepVerifier.create(itemCacheRepository.saveItem(firstItem.getId(), firstItem))
            .assertNext(item -> assertEquals(true, item)
            ).verifyComplete();
    }

    protected ItemListDto populateItemListDto() {
        ItemListDto item = new ItemListDto();

        item.setId(TEST_ID);
        item.setTitle(TEST_TITLE);
        item.setDescription(TEST_DESCRIPTION);
        item.setPrice(TEST_PRICE);

        return item;
    }

    protected ItemDto populateItemDto() {
        ItemDto item = new ItemDto();

        item.setId(TEST_ID);
        item.setTitle(TEST_TITLE);
        item.setDescription(TEST_DESCRIPTION);
        item.setCount(1);
        item.setPrice(TEST_PRICE);
        item.setImgPath(TEST_IMG_PATH);

        return item;
    }
}
