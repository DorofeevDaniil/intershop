package ru.custom.intershop.unit.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockReset;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.custom.intershop.model.Item;
import ru.custom.intershop.repository.ItemRepository;
import ru.custom.intershop.service.ItemService;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = ItemService.class)
class ItemServiceTest {
    @MockitoBean(reset = MockReset.BEFORE)
    private ItemRepository itemRepository;

    @Autowired
    private ItemService itemService;

    @TempDir
    static Path tempDir;

    private static final String TEST_FILE_NAME = "test-image.jpg";
    private static final String TEST_FILE_CONTENT = "test file content";
    private static final String TEST_FILE_CONTENT_TYPE = "image/jpeg";

    private static final Long TEST_ID = 1L;
    private static final String TEST_TITLE = "test title";
    private static final String TEST_DESCRIPTION = "test description";
    private static final BigDecimal TEST_PRICE = BigDecimal.valueOf(100);
    private static final String TEST_IMG_PATH = "/test/path.png";
    private static final String TEST_SEARCH = "test";

    private static final Integer TEST_PAGE = 1;
    private static final Integer TEST_PAGE_SIZE = 10;
    private static final String TEST_SORT_ALPHA = "ALPHA";
    private static final String TEST_SORT_PRICE = "PRICE";
    private static final String TEST_SORT_NO = "NO";

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("app.upload-dir", () -> tempDir.toString());
    }

    @Test
    void save_shouldSaveItem() {
        Item expextedItem = populateItem();
        expextedItem.setId(TEST_ID);

        doReturn(expextedItem).when(itemRepository).save(any(Item.class));

        itemRepository.save(expextedItem);

        verify(itemRepository, times(1)).save(expextedItem);
    }

    @Test
    void getPage_shouldReturnItemsPage_sortByTitle() {
        Item expextedItem = populateItem();
        expextedItem.setId(TEST_ID);

        Page<Item> page = new PageImpl<>(List.of(expextedItem));

        doReturn(page).when(itemRepository).findAll(any(PageRequest.class));

        List<Item> actualItems = itemService.getPage(TEST_PAGE, TEST_PAGE_SIZE, TEST_SORT_ALPHA);

        verify(itemRepository, times(1))
            .findAll(PageRequest.of(TEST_PAGE - 1, TEST_PAGE_SIZE, Sort.by("title").ascending()));
        assertEquals(1, actualItems.size());
        assertTrue(actualItems.contains(expextedItem));
    }

    @Test
    void getPage_shouldReturnItemsPage_sortByPrice() {
        Item expextedItem = populateItem();
        expextedItem.setId(TEST_ID);

        Page<Item> page = new PageImpl<>(List.of(expextedItem));

        doReturn(page).when(itemRepository).findAll(any(PageRequest.class));

        List<Item> actualItems = itemService.getPage(TEST_PAGE, TEST_PAGE_SIZE, TEST_SORT_PRICE);

        verify(itemRepository, times(1))
            .findAll(PageRequest.of(TEST_PAGE - 1, TEST_PAGE_SIZE, Sort.by("price").ascending()));
        assertEquals(1, actualItems.size());
        assertTrue(actualItems.contains(expextedItem));
    }

    @Test
    void getPage_shouldReturnItemsPage_withoutSort() {
        Item expextedItem = populateItem();
        expextedItem.setId(TEST_ID);

        doReturn(List.of(expextedItem)).when(itemRepository).findAllByOrderByIdAsc(any(PageRequest.class));

        List<Item> actualItems = itemService.getPage(TEST_PAGE, TEST_PAGE_SIZE, TEST_SORT_NO);

        verify(itemRepository, times(1))
            .findAllByOrderByIdAsc(PageRequest.of(TEST_PAGE - 1, TEST_PAGE_SIZE));
        assertEquals(1, actualItems.size());
        assertTrue(actualItems.contains(expextedItem));
    }

    @Test
    void findBySearchParams_shouldSearch_sortByTitle() {
        Item expextedItem = populateItem();
        expextedItem.setId(TEST_ID);

        doReturn(List.of(expextedItem)).when(itemRepository).searchByText(anyString(), any(PageRequest.class));

        List<Item> actualItems = itemService.findBySearchParams(TEST_PAGE, TEST_PAGE_SIZE, TEST_SORT_ALPHA, TEST_SEARCH);

        verify(itemRepository, times(1))
            .searchByText(
                TEST_SEARCH.toUpperCase(),
                PageRequest.of(TEST_PAGE - 1, TEST_PAGE_SIZE, Sort.by("title").ascending()));
        assertEquals(1, actualItems.size());
        assertTrue(actualItems.contains(expextedItem));
    }

    @Test
    void findBySearchParams_shouldSearch_sortByPrice() {
        Item expextedItem = populateItem();
        expextedItem.setId(TEST_ID);

        doReturn(List.of(expextedItem)).when(itemRepository).searchByText(anyString(), any(PageRequest.class));

        List<Item> actualItems = itemService.findBySearchParams(TEST_PAGE, TEST_PAGE_SIZE, TEST_SORT_PRICE, TEST_SEARCH);

        verify(itemRepository, times(1))
            .searchByText(
                TEST_SEARCH.toUpperCase(),
                PageRequest.of(TEST_PAGE - 1, TEST_PAGE_SIZE, Sort.by("price").ascending()));
        assertEquals(1, actualItems.size());
        assertTrue(actualItems.contains(expextedItem));
    }

    @Test
    void findBySearchParams_shouldSearch_withoutSort() {
        Item expextedItem = populateItem();
        expextedItem.setId(TEST_ID);

        doReturn(List.of(expextedItem)).when(itemRepository).searchByText(anyString(), any(PageRequest.class));

        List<Item> actualItems = itemService.findBySearchParams(TEST_PAGE, TEST_PAGE_SIZE, TEST_SORT_NO, TEST_SEARCH);

        verify(itemRepository, times(1))
            .searchByText(
                TEST_SEARCH.toUpperCase(),
                PageRequest.of(TEST_PAGE - 1, TEST_PAGE_SIZE));
        assertEquals(1, actualItems.size());
        assertTrue(actualItems.contains(expextedItem));
    }

    @Test
    void getTotalSearchedElements_shouldReturnCountOfElementsByFilter() {
        doReturn(1L).when(itemRepository).getFilteredCount(anyString());

        itemService.getTotalSearchedElements(TEST_SEARCH);

        verify(itemRepository, times(1)).getFilteredCount(TEST_SEARCH.toUpperCase());
    }

    @Test
    void getTotalCount_shouldReturnCount() {
        doReturn(1L).when(itemRepository).count();

        itemService.getTotalCount();

        verify(itemRepository, times(1)).count();
    }

    @Test
    void getItemById_shouldReturnItem() {
        Item expextedItem = populateItem();
        expextedItem.setId(TEST_ID);

        doReturn(Optional.of(expextedItem)).when(itemRepository).findById(anyLong());

        Item actualItem = itemService.getItemById(TEST_ID);

        verify(itemRepository, times(1)).findById(TEST_ID);
        assertEquals(expextedItem, actualItem);
    }

    @Test
    void getItemById_shouldReturnEmpty_whenNotFound() {
        doReturn(Optional.empty()).when(itemRepository).findById(anyLong());

        Item actualItem = itemService.getItemById(TEST_ID);

        verify(itemRepository, times(1)).findById(TEST_ID);
        assertEquals(new Item(), actualItem);
    }

    @Test
    void getAllInCart_shouldReturnItemsInCart() {
        Item cartItem = populateItem();
        Item item = populateItem();
        item.setCount(0);

        doReturn(List.of(cartItem)).when(itemRepository).findAllByCountGreaterThan(anyInt());

        List<Item> items = itemService.getAllInCart();

        verify(itemRepository, times(1)).findAllByCountGreaterThan(0);
        assertEquals(1, items.size());
        assertTrue(items.contains(cartItem));
    }

    @Test
    void changeAmount_shouldIncreaseAmount() {
        Item item = populateItem();
        item.setId(TEST_ID);

        Item expectedItem = populateItem();
        expectedItem.setId(TEST_ID);
        expectedItem.setCount(item.getCount() + 1);

        doReturn(Optional.of(item)).when(itemRepository).findById(anyLong());
        doReturn(expectedItem).when(itemRepository).save(any(Item.class));

        itemService.changeAmount(item.getId(), "PLUS");

        verify(itemRepository, times(1)).findById(TEST_ID);
        ArgumentCaptor<Item> captor = ArgumentCaptor.forClass(Item.class);
        verify(itemRepository, times(1)).save(captor.capture());

        Integer actualCount = captor.getValue().getCount();
        assertEquals(expectedItem.getCount(), actualCount);
    }

    @Test
    void changeAmount_shouldDecreaseAmount() {
        Item item = populateItem();
        item.setId(TEST_ID);
        item.setCount(2);

        Item expectedItem = populateItem();
        expectedItem.setId(TEST_ID);
        expectedItem.setCount(item.getCount() - 1);

        doReturn(Optional.of(item)).when(itemRepository).findById(anyLong());
        doReturn(expectedItem).when(itemRepository).save(any(Item.class));

        itemService.changeAmount(item.getId(), "MINUS");

        verify(itemRepository, times(1)).findById(TEST_ID);
        ArgumentCaptor<Item> captor = ArgumentCaptor.forClass(Item.class);
        verify(itemRepository, times(1)).save(captor.capture());

        Integer actualCount = captor.getValue().getCount();
        assertEquals(expectedItem.getCount(), actualCount);
    }

    @Test
    void changeAmount_shouldDecreaseAmountFromZero() {
        Item item = populateItem();
        item.setId(TEST_ID);
        item.setCount(0);

        Item expectedItem = populateItem();
        expectedItem.setId(TEST_ID);
        expectedItem.setCount(item.getCount());

        doReturn(Optional.of(item)).when(itemRepository).findById(anyLong());
        doReturn(expectedItem).when(itemRepository).save(any(Item.class));

        itemService.changeAmount(item.getId(), "MINUS");

        verify(itemRepository, times(1)).findById(TEST_ID);
        ArgumentCaptor<Item> captor = ArgumentCaptor.forClass(Item.class);
        verify(itemRepository, times(1)).save(captor.capture());

        Integer actualCount = captor.getValue().getCount();
        assertEquals(expectedItem.getCount(), actualCount);
    }

    @Test
    void changeAmount_shouldDelete() {
        Item item = populateItem();
        item.setId(TEST_ID);
        item.setCount(2);

        Item expectedItem = populateItem();
        expectedItem.setId(TEST_ID);
        expectedItem.setCount(0);

        doReturn(Optional.of(item)).when(itemRepository).findById(anyLong());
        doReturn(expectedItem).when(itemRepository).save(any(Item.class));

        itemService.changeAmount(item.getId(), "DELETE");

        verify(itemRepository, times(1)).findById(TEST_ID);
        ArgumentCaptor<Item> captor = ArgumentCaptor.forClass(Item.class);
        verify(itemRepository, times(1)).save(captor.capture());

        Integer actualCount = captor.getValue().getCount();
        assertEquals(expectedItem.getCount(), actualCount);
    }

    @Test
    void addItem_shouldSaveItem() throws IOException {
        MockMultipartFile image = populateMultipart();

        Item item = populateItem();

        doReturn(item).when(itemRepository).save(any(Item.class));

        itemService.addItem(item, image);

        Path expectedPath = tempDir.resolve(TEST_FILE_NAME);
        assertTrue(Files.exists(expectedPath));
        assertEquals(TEST_FILE_CONTENT, Files.readString(expectedPath));

        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void updateItems_shouldUpdateItems() {
        Item item = populateItem();
        List<Item> items = List.of(item);

        doReturn(items).when(itemRepository).saveAll(anyList());

        itemService.updateItems(items);

        verify(itemRepository, times(1)).saveAll(items);
    }

    private MockMultipartFile populateMultipart() {
        return new MockMultipartFile(
            "file", TEST_FILE_NAME, TEST_FILE_CONTENT_TYPE, TEST_FILE_CONTENT.getBytes());
    }

    private Item populateItem() {
        Item item = new Item();

        item.setTitle(TEST_TITLE);
        item.setDescription(TEST_DESCRIPTION);
        item.setCount(1);
        item.setPrice(TEST_PRICE);
        item.setImgPath(TEST_IMG_PATH);

        return item;
    }
}
