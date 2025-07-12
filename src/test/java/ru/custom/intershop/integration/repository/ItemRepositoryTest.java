package ru.custom.intershop.integration.repository;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.custom.intershop.model.Item;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemRepositoryTest extends BaseRepositoryTest {
    private static final String SEARCH_PARAM = "1";

    @RepeatedTest(value = 5, name = RepeatedTest.LONG_DISPLAY_NAME)
    void findAllByOrderByIdAsc_shouldReturnCurrentOrder() {
        List<Item> items = itemRepository.findAllByOrderByIdAsc(
            PageRequest.of(TEST_PAGE - 1, TEST_PAGE_SIZE, Sort.by("title").ascending())
        );

        assertEquals(2, items.size());
        assertEquals(1L, items.get(0).getId());
        assertEquals(2L, items.get(1).getId());
    }

    @Test
    void findAllByCountGreaterThan_shouldReturnGreaterValues() {
        Item item = itemRepository.findById(1L).orElseGet(Item::new);
        item.setCount(2);

        itemRepository.save(item);

        List<Item> items = itemRepository.findAllByCountGreaterThan(0);

        assertEquals(1, items.size());
        assertEquals(2, items.get(0).getCount());
    }

    @Test
    void searchByText_shouldReternFilteredBySearchParam() {
        List<Item> items = itemRepository.searchByText(
            SEARCH_PARAM,
            PageRequest.of(TEST_PAGE - 1, TEST_PAGE_SIZE)
        );

        assertEquals(1, items.size());
        assertTrue(
            items.get(0).getTitle().toUpperCase().contains(SEARCH_PARAM)
                || items.get(0).getDescription().toUpperCase().contains(SEARCH_PARAM));
    }

    @Test
    void getFilteredCount_shouldReturnItemsCount_filteredBySearchParam() {
        Long count = itemRepository.getFilteredCount(SEARCH_PARAM);

        assertEquals(1L, count);
    }
}
