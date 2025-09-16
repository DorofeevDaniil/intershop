package ru.custom.storefrontapp.unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;
import ru.custom.storefrontapp.dto.ItemDto;
import ru.custom.storefrontapp.dto.ItemListDto;
import ru.custom.storefrontapp.mapper.ItemMapper;
import ru.custom.storefrontapp.model.Item;
import ru.custom.storefrontapp.repository.ItemCacheRepository;
import ru.custom.storefrontapp.repository.ItemRepository;
import ru.custom.storefrontapp.service.ItemService;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemServiceTest extends BaseServiceTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemCacheRepository itemCacheRepository;
    @InjectMocks
    private ItemService itemService;

    @TempDir
    static Path tempDir;

    @BeforeEach
    void setupTest() throws IllegalAccessException, NoSuchFieldException {
        itemService = new ItemService(itemCacheRepository, itemRepository);

        // Проставляем путь вручную, чтобы юнит-тест работал
        Field field = ItemService.class.getDeclaredField("relativePath");
        field.setAccessible(true);
        field.set(itemService, tempDir.toString());
    }

    @Test
    void getItemCardById_shouldReturnItem_fromCache() {
        ItemDto expectedItem = populateItemDto();
        expectedItem.setId(TEST_ID);

        doReturn(Mono.just(expectedItem)).when(itemCacheRepository).findById(anyLong());

        StepVerifier.create(itemService.getItemCardById(TEST_ID))
            .assertNext(actualItem -> assertEquals(expectedItem, actualItem))
            .verifyComplete();

        verify(itemCacheRepository, times(1)).findById(TEST_ID);
        verify(itemRepository, never()).findById(anyLong());
    }

    @Test
    void getItemCardById_shouldReturnItem_fromDb() {
        Item expectedItem = populateItem();
        expectedItem.setId(TEST_ID);

        doReturn(Mono.empty()).when(itemCacheRepository).findById(anyLong());
        doReturn(Mono.just(true)).when(itemCacheRepository).saveItem(anyLong(), any(ItemDto.class));
        doReturn(Mono.just(expectedItem)).when(itemRepository).findById(anyLong());

        StepVerifier.create(itemService.getItemCardById(TEST_ID))
            .assertNext(actualItem -> assertEquals(ItemMapper.toItemDto(expectedItem), actualItem))
            .verifyComplete();

        verify(itemCacheRepository, times(1)).findById(TEST_ID);
        verify(itemRepository, times(1)).findById(TEST_ID);
        verify(itemCacheRepository, times(1)).saveItem(TEST_ID, ItemMapper.toItemDto(expectedItem));
    }

    @Test
    void getItemsListCached_shouldReturnItemsList_fromCache() {
        List<ItemListDto> expectedItemList = List.of(populateItemList());

        doReturn(Mono.just(expectedItemList)).when(itemCacheRepository).findAll();

        StepVerifier.create(itemService.getItemsListCached())
            .assertNext(actualItemsList -> assertEquals(expectedItemList, actualItemsList))
            .verifyComplete();

        verify(itemCacheRepository, times(1)).findAll();
        verify(itemRepository, never()).findAll();
    }

    @Test
    void getItemsListCached_shouldReturnItemsList_fromDb() {
        List<ItemListDto> expectedItemList = List.of(populateItemList());
        List<Item> returnedItemList = List.of(populateItem());

        doReturn(Mono.empty()).when(itemCacheRepository).findAll();
        doReturn(Mono.just(expectedItemList)).when(itemCacheRepository).saveAll(anyList());
        doReturn(Flux.fromIterable(returnedItemList)).when(itemRepository).findAll();

        StepVerifier.create(itemService.getItemsListCached())
            .assertNext(actualItemsList -> assertEquals(expectedItemList, actualItemsList))
            .verifyComplete();

        verify(itemCacheRepository, times(1)).findAll();
        verify(itemRepository, times(1)).findAll();
        verify(itemCacheRepository, times(1)).saveAll(anyList());
    }

    @Test
    void searchAndPaginate_shouldReturnPage_orderById() {
        ItemListDto firstItemListDto = populateItemList();
        ItemListDto secondItemListDto = populateItemList();
        secondItemListDto.setId(TEST_ID + 1);

        ItemDto firstItemDto = populateItemDto();
        firstItemDto.setId(TEST_ID);
        ItemDto secondItemDto = populateItemDto();
        secondItemDto.setId(TEST_ID + 1);

        List<ItemListDto> expectedItemList = List.of(firstItemListDto, secondItemListDto);
        Tuple2<List<ItemDto>, Long> tuple =  Tuples.of(List.of(firstItemDto, secondItemDto), 2L);

        doReturn(Mono.just(expectedItemList)).when(itemCacheRepository).findAll();
        doReturn(Mono.just(firstItemDto)).when(itemCacheRepository).findById(firstItemDto.getId());
        doReturn(Mono.just(secondItemDto)).when(itemCacheRepository).findById(secondItemDto.getId());

        StepVerifier.create(itemService.searchAndPaginate(TEST_PAGE, TEST_PAGE_SIZE, TEST_SORT_NO, ""))
            .assertNext(actualItemsList -> assertEquals(tuple, actualItemsList))
            .verifyComplete();

        verify(itemCacheRepository, times(1)).findAll();
        verify(itemCacheRepository, times(2)).findById(anyLong());
    }

    @Test
    void searchAndPaginate_shouldReturnPage_orderByTitle() {
        ItemListDto firstItemListDto = populateItemList();
        firstItemListDto.setTitle("west title");
        ItemListDto secondItemListDto = populateItemList();
        secondItemListDto.setId(TEST_ID + 1);

        ItemDto firstItemDto = populateItemDto();
        firstItemDto.setId(TEST_ID);
        firstItemDto.setTitle("west title");
        ItemDto secondItemDto = populateItemDto();
        secondItemDto.setId(TEST_ID + 1);

        List<ItemListDto> expectedItemList = List.of(firstItemListDto, secondItemListDto);
        Tuple2<List<ItemDto>, Long> tuple =  Tuples.of(List.of(secondItemDto, firstItemDto), 2L);

        doReturn(Mono.just(expectedItemList)).when(itemCacheRepository).findAll();
        doReturn(Mono.just(firstItemDto)).when(itemCacheRepository).findById(firstItemDto.getId());
        doReturn(Mono.just(secondItemDto)).when(itemCacheRepository).findById(secondItemDto.getId());

        StepVerifier.create(itemService.searchAndPaginate(TEST_PAGE, TEST_PAGE_SIZE, TEST_SORT_ALPHA, ""))
            .assertNext(actualItemsList -> assertEquals(tuple, actualItemsList))
            .verifyComplete();

        verify(itemCacheRepository, times(1)).findAll();
        verify(itemCacheRepository, times(2)).findById(anyLong());
    }

    @Test
    void searchAndPaginate_shouldReturnPage_orderByPrice() {
        ItemListDto firstItemListDto = populateItemList();
        ItemListDto secondItemListDto = populateItemList();
        secondItemListDto.setId(TEST_ID + 1);
        firstItemListDto.setPrice(secondItemListDto.getPrice().add(firstItemListDto.getPrice()));

        ItemDto firstItemDto = populateItemDto();
        firstItemDto.setId(TEST_ID);
        ItemDto secondItemDto = populateItemDto();
        secondItemDto.setId(TEST_ID + 1);
        firstItemDto.setPrice(secondItemListDto.getPrice().add(firstItemListDto.getPrice()));

        List<ItemListDto> expectedItemList = List.of(firstItemListDto, secondItemListDto);
        Tuple2<List<ItemDto>, Long> tuple =  Tuples.of(List.of(secondItemDto, firstItemDto), 2L);

        doReturn(Mono.just(expectedItemList)).when(itemCacheRepository).findAll();
        doReturn(Mono.just(firstItemDto)).when(itemCacheRepository).findById(firstItemDto.getId());
        doReturn(Mono.just(secondItemDto)).when(itemCacheRepository).findById(secondItemDto.getId());

        StepVerifier.create(itemService.searchAndPaginate(TEST_PAGE, TEST_PAGE_SIZE, TEST_SORT_PRICE, ""))
            .assertNext(actualItemsList -> assertEquals(tuple, actualItemsList))
            .verifyComplete();

        verify(itemCacheRepository, times(1)).findAll();
        verify(itemCacheRepository, times(2)).findById(anyLong());
    }

    @Test
    void searchAndPaginate_shouldReturnPage_searchByTitle() {
        ItemListDto firstItemListDto = populateItemList();
        firstItemListDto.setTitle(firstItemListDto.getTitle() + 1);
        ItemListDto secondItemListDto = populateItemList();
        secondItemListDto.setId(TEST_ID + 1);

        ItemDto firstItemDto = populateItemDto();
        firstItemDto.setId(TEST_ID);
        firstItemDto.setTitle(firstItemDto.getTitle() + 1);
        ItemDto secondItemDto = populateItemDto();
        secondItemDto.setId(TEST_ID + 1);

        List<ItemListDto> expectedItemList = List.of(firstItemListDto, secondItemListDto);
        Tuple2<List<ItemDto>, Long> tuple =  Tuples.of(List.of(firstItemDto), 1L);

        doReturn(Mono.just(expectedItemList)).when(itemCacheRepository).findAll();
        doReturn(Mono.just(firstItemDto)).when(itemCacheRepository).findById(firstItemDto.getId());

        StepVerifier.create(itemService.searchAndPaginate(TEST_PAGE, TEST_PAGE_SIZE, TEST_SORT_NO, "title1"))
            .assertNext(actualItemsList -> assertEquals(tuple, actualItemsList))
            .verifyComplete();

        verify(itemCacheRepository, times(1)).findAll();
        verify(itemCacheRepository, times(1)).findById(anyLong());
    }

    @Test
    void searchAndPaginate_shouldReturnPage_searchByDescription() {
        ItemListDto firstItemListDto = populateItemList();
        firstItemListDto.setDescription(firstItemListDto.getDescription() + 1);
        ItemListDto secondItemListDto = populateItemList();
        secondItemListDto.setId(TEST_ID + 1);

        ItemDto firstItemDto = populateItemDto();
        firstItemDto.setId(TEST_ID);
        firstItemDto.setDescription(firstItemDto.getDescription() + 1);
        ItemDto secondItemDto = populateItemDto();
        secondItemDto.setId(TEST_ID + 1);

        List<ItemListDto> expectedItemList = List.of(firstItemListDto, secondItemListDto);
        Tuple2<List<ItemDto>, Long> tuple =  Tuples.of(List.of(firstItemDto), 1L);

        doReturn(Mono.just(expectedItemList)).when(itemCacheRepository).findAll();
        doReturn(Mono.just(firstItemDto)).when(itemCacheRepository).findById(firstItemDto.getId());

        StepVerifier.create(itemService.searchAndPaginate(TEST_PAGE, TEST_PAGE_SIZE, TEST_SORT_NO, "description1"))
            .assertNext(actualItemsList -> assertEquals(tuple, actualItemsList))
            .verifyComplete();

        verify(itemCacheRepository, times(1)).findAll();
        verify(itemCacheRepository, times(1)).findById(anyLong());
    }

    @Test
    void addItem_shouldSaveItem_shouldAddItemToCache_shouldRemoveListCache() {
        FilePart image = populateFilePart();
        ItemDto expectedItem = populateItemDto();
        expectedItem.setId(TEST_ID);

        doReturn(Mono.just(ItemMapper.toItem(expectedItem))).when(itemRepository).save(any(Item.class));
        doReturn(Mono.just(true)).when(itemCacheRepository).saveItem(anyLong(), any(ItemDto.class));
        doReturn(Mono.just(1L)).when(itemCacheRepository).deleteList();

        StepVerifier.create(
            itemService.addItem(expectedItem, image)
        ).assertNext(returnedItem -> {
                assertEquals(expectedItem, returnedItem);

                Path expectedPath = tempDir.resolve(TEST_FILE_NAME);
                assertTrue(Files.exists(expectedPath));
                try {
                    assertEquals(TEST_FILE_CONTENT, Files.readString(expectedPath));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        ).verifyComplete();

        verify(itemRepository, times(1)).save(ItemMapper.toItem(expectedItem));
        verify(itemCacheRepository, times(1)).saveItem(TEST_ID, expectedItem);
        verify(itemCacheRepository, times(1)).deleteList();
    }

    @Test
    void saveToDb_shouldSaveItemToDb() {
        Item expectedItem = populateItem();
        expectedItem.setId(TEST_ID);

        doReturn(Mono.just(expectedItem)).when(itemRepository).save(any(Item.class));

        StepVerifier.create(itemService.saveToDb(expectedItem))
            .assertNext(actualItem -> assertEquals(expectedItem, actualItem))
            .verifyComplete();

        verify(itemRepository, times(1)).save(expectedItem);
    }

    @Test
    void getTotalCount_shouldReturnTotalItemsCount() {
        doReturn(Mono.just(1L)).when(itemRepository).count();

        StepVerifier.create(itemService.getTotalCount())
            .assertNext(actualCount -> assertEquals(1L, actualCount))
            .verifyComplete();

        verify(itemRepository, times(1)).count();
    }

    private Item populateItem() {
        return populateItemNoId();
    }

    private ItemDto populateItemDto() {
        return ItemMapper.toItemDto(populateItemNoId());
    }

    private ItemListDto populateItemList() {
        ItemListDto itemListDto = new ItemListDto();

        itemListDto.setId(TEST_ID);
        itemListDto.setTitle(TEST_TITLE);
        itemListDto.setDescription(TEST_DESCRIPTION);
        itemListDto.setPrice(TEST_PRICE);

        return itemListDto;
    }
}
