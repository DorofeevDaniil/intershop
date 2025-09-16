package ru.custom.storefrontapp.integration.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CartCacheRepositoryTest extends BaseCacheRepository {

    private static final String CART_CACHE_ID = "cart:" + TEST_USER_ID;

    @BeforeEach
    void populateCache() {
        hashOps = redisTemplate.opsForHash();

        redisTemplate.delete(CART_CACHE_ID).block();

        Map.Entry<Object, Object> entry1 = new java.util.AbstractMap.SimpleEntry<>(TEST_FIRST_ID, TEST_FIRST_COUNT);
        Map.Entry<Object, Object> entry2 = new java.util.AbstractMap.SimpleEntry<>(TEST_SECOND_ID, TEST_SECOND_COUNT);

        Flux<Void> insert = Flux.concat(
            hashOps.put(CART_CACHE_ID, entry1.getKey(), entry1.getValue()).then(),
            hashOps.put(CART_CACHE_ID, entry2.getKey(), entry2.getValue()).then()
        );

        insert.blockLast();
    }

    @Test
    void findAll_shouldReturnInsertedEntries() {
        StepVerifier.create(cartCacheRepository.findAll(TEST_USER_ID))
            .expectNextMatches(e -> e.getKey().equals(TEST_FIRST_ID) && e.getValue().equals(TEST_FIRST_COUNT))
            .expectNextMatches(e -> e.getKey().equals(TEST_SECOND_ID) && e.getValue().equals(TEST_SECOND_COUNT))
            .verifyComplete();
    }

    @Test
    void findById_shouldReturnInsertedEntryById() {
        StepVerifier.create(cartCacheRepository.findById(TEST_FIRST_ID, TEST_USER_ID))
            .assertNext(quantity ->
                assertEquals(Integer.parseInt(TEST_FIRST_COUNT), quantity)
            ).verifyComplete();
    }

    @Test
    void findById_shouldReturnDefault() {
        StepVerifier.create(cartCacheRepository.findById("3", TEST_USER_ID))
            .assertNext(quantity ->
                assertEquals(0, quantity)
            ).verifyComplete();
    }

    @Test
    void deleteItem_shouldRemoveItemFromCache() {
        StepVerifier.create(cartCacheRepository.deleteItem(TEST_SECOND_ID, TEST_USER_ID))
            .assertNext(quantity ->
                assertEquals(1, quantity)
            ).verifyComplete();
    }

    @Test
    void deleteCart_shouldReturnAllItems() {
        StepVerifier.create(cartCacheRepository.deleteCart(TEST_USER_ID))
            .assertNext(quantity ->
                assertEquals(1, quantity)
            ).verifyComplete();
    }

    @Test
    void updateItemQuantity_shouldInsertItem() {
        StepVerifier.create(cartCacheRepository.updateItemQuantity("3", 1, TEST_USER_ID))
            .assertNext(quantity ->
                assertEquals(1, quantity)
            ).verifyComplete();
    }
}
