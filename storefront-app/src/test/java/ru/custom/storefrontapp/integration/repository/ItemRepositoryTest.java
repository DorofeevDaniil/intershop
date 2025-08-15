package ru.custom.storefrontapp.integration.repository;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.custom.storefrontapp.model.Item;

import static org.junit.jupiter.api.Assertions.*;

class ItemRepositoryTest extends BaseRepositoryTest {
    @RepeatedTest(value = 5, name = RepeatedTest.LONG_DISPLAY_NAME)
    void findAllByOrderByIdAsc_shouldReturnCurrentOrder() {
        StepVerifier.create(
            itemRepository.findAllByOrderByIdAsc(PageRequest.of(TEST_PAGE - 1, TEST_PAGE_SIZE)).collectList()
        ).assertNext(items -> {
            assertEquals(2, items.size());
            assertEquals(1L, items.get(0).getId());
            assertEquals(2L, items.get(1).getId());
        }).verifyComplete();
    }

    @Test
    void findAllByCountGreaterThan_shouldReturnGreaterValues() {
        Mono<Item> updatedItem = itemRepository.findById(1L)
            .defaultIfEmpty(new Item())
            .map(item -> {
                item.setCount(2);
                return item;
            }).flatMap(itemRepository::save);

        StepVerifier.create(
            updatedItem.thenMany(itemRepository.findAllByCountGreaterThan(0))
        ).expectNextMatches(item ->
                item.getCount() == 2
            ).verifyComplete();
    }

    @Test
    void findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase_shouldReternFilteredBySearchParam() {
        StepVerifier.create(
            itemRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                SEARCH_PARAM,
                SEARCH_PARAM,
                PageRequest.of(TEST_PAGE - 1, TEST_PAGE_SIZE)
            ).collectList()
        ).assertNext(items -> {
            assertEquals(1, items.size());
            assertTrue(items.get(0).getTitle().toUpperCase().contains(SEARCH_PARAM) ||
                items.get(0).getDescription().toUpperCase().contains(SEARCH_PARAM));
        }).verifyComplete();
    }

    @Test
    void countByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase_shouldReturnItemsCount_filteredBySearchParam() {
        StepVerifier.create(
            itemRepository.countByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(SEARCH_PARAM, SEARCH_PARAM)
        ).expectNext(1L)
            .verifyComplete();
    }
}
