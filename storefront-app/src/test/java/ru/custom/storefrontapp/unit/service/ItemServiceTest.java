package ru.custom.storefrontapp.unit.service;//package ru.custom.intershop.unit.service;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.io.TempDir;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.core.io.buffer.DataBuffer;
//import org.springframework.core.io.buffer.DefaultDataBufferFactory;
//import org.springframework.data.domain.*;
//import org.springframework.http.codec.multipart.FilePart;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.springframework.test.context.bean.override.mockito.MockReset;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//import reactor.test.StepVerifier;
//import ru.custom.intershop.model.Item;
//import ru.custom.intershop.repository.ItemRepository;
//import ru.custom.intershop.service.ItemService;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@SpringBootTest(classes = ItemService.class)
//class ItemServiceTest extends BaseServiceTest {
//    @MockitoBean(reset = MockReset.BEFORE)
//    private ItemRepository itemRepository;
//
//    @Autowired
//    private ItemService itemService;
//
//    @TempDir
//    static Path tempDir;
//
//    private static final String TEST_FILE_NAME = "test-image.jpg";
//    private static final String TEST_FILE_CONTENT = "test file content";
//
//    private static final String TEST_SEARCH = "test";
//
//    private static final Integer TEST_PAGE = 1;
//    private static final Integer TEST_PAGE_SIZE = 10;
//    private static final String TEST_SORT_ALPHA = "ALPHA";
//    private static final String TEST_SORT_PRICE = "PRICE";
//    private static final String TEST_SORT_NO = "NO";
//
//    @DynamicPropertySource
//    static void overrideProperties(DynamicPropertyRegistry registry) {
//        registry.add("app.upload-dir", () -> tempDir.toString());
//    }
//
//    @Test
//    void save_shouldSaveItem() {
//        Item expectedItem = populateItem();
//        expectedItem.setId(TEST_ID);
//
//        doReturn(Mono.just(expectedItem)).when(itemRepository).save(any(Item.class));
//
//        StepVerifier.create(
//            itemService.save(expectedItem)
//        ).assertNext(returnedItem ->
//            assertEquals(expectedItem, returnedItem)
//        ).verifyComplete();
//
//        verify(itemRepository, times(1)).save(expectedItem);
//    }
//
//    @Test
//    void getPage_shouldReturnItemsPage_sortByTitle() {
//        Item expectedItem = populateItem();
//        expectedItem.setId(TEST_ID);
//
//        List<Item> page = List.of(expectedItem);
//
//        doReturn(Flux.fromIterable(page)).when(itemRepository).findAllByOrderByTitleAsc(any(PageRequest.class));
//
//        StepVerifier.create(
//            itemService.getPage(TEST_PAGE, TEST_PAGE_SIZE, TEST_SORT_ALPHA)
//        ).assertNext(actualItem ->
//            assertEquals(expectedItem, actualItem)
//        ).verifyComplete();
//
//        verify(itemRepository, times(1))
//            .findAllByOrderByTitleAsc(PageRequest.of(TEST_PAGE - 1, TEST_PAGE_SIZE));
//    }
//
//    @Test
//    void getPage_shouldReturnItemsPage_sortByPrice() {
//        Item expectedItem = populateItem();
//        expectedItem.setId(TEST_ID);
//
//        List<Item> page = List.of(expectedItem);
//
//        doReturn(Flux.fromIterable(page)).when(itemRepository).findAllByOrderByPriceAsc(any(PageRequest.class));
//
//        StepVerifier.create(
//            itemService.getPage(TEST_PAGE, TEST_PAGE_SIZE, TEST_SORT_PRICE)
//        ).assertNext(actualItem ->
//            assertEquals(expectedItem, actualItem)
//        ).verifyComplete();
//
//        verify(itemRepository, times(1))
//            .findAllByOrderByPriceAsc(PageRequest.of(TEST_PAGE - 1, TEST_PAGE_SIZE));
//    }
//
//    @Test
//    void getPage_shouldReturnItemsPage_withoutSort() {
//        Item expectedItem = populateItem();
//        expectedItem.setId(TEST_ID);
//
//        List<Item> page = List.of(expectedItem);
//
//        doReturn(Flux.fromIterable(page)).when(itemRepository).findAllByOrderByIdAsc(any(PageRequest.class));
//
//        StepVerifier.create(
//            itemService.getPage(TEST_PAGE, TEST_PAGE_SIZE, TEST_SORT_NO)
//        ).assertNext(actualItem ->
//            assertEquals(expectedItem, actualItem)
//        ).verifyComplete();
//
//        verify(itemRepository, times(1))
//            .findAllByOrderByIdAsc(PageRequest.of(TEST_PAGE - 1, TEST_PAGE_SIZE));
//    }
//
//    @Test
//    void findBySearchParams_shouldSearch_sortByTitle() {
//        Item expectedItem = populateItem();
//        expectedItem.setId(TEST_ID);
//
//        List<Item> page = List.of(expectedItem);
//
//        doReturn(Flux.fromIterable(page))
//            .when(itemRepository)
//            .findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
//                anyString(), anyString(), any(PageRequest.class));
//
//        StepVerifier.create(
//            itemService.findBySearchParams(TEST_PAGE, TEST_PAGE_SIZE, TEST_SORT_ALPHA, TEST_SEARCH)
//        ).assertNext(actualItem ->
//            assertEquals(expectedItem, actualItem)
//        ).verifyComplete();
//
//        verify(itemRepository, times(1))
//            .findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
//                TEST_SEARCH.toUpperCase(),
//                TEST_SEARCH.toUpperCase(),
//                PageRequest.of(TEST_PAGE - 1, TEST_PAGE_SIZE, Sort.by("title").ascending()));
//    }
//
//    @Test
//    void findBySearchParams_shouldSearch_sortByPrice() {
//        Item expectedItem = populateItem();
//        expectedItem.setId(TEST_ID);
//
//        List<Item> page = List.of(expectedItem);
//
//        doReturn(Flux.fromIterable(page))
//            .when(itemRepository)
//            .findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
//                anyString(), anyString(), any(PageRequest.class));
//
//        StepVerifier.create(
//            itemService.findBySearchParams(TEST_PAGE, TEST_PAGE_SIZE, TEST_SORT_PRICE, TEST_SEARCH)
//        ).assertNext(actualItem ->
//            assertEquals(expectedItem, actualItem)
//        ).verifyComplete();
//
//        verify(itemRepository, times(1))
//            .findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
//                TEST_SEARCH.toUpperCase(),
//                TEST_SEARCH.toUpperCase(),
//                PageRequest.of(TEST_PAGE - 1, TEST_PAGE_SIZE, Sort.by("price").ascending()));
//    }
//
//    @Test
//    void findBySearchParams_shouldSearch_withoutSort() {
//        Item expectedItem = populateItem();
//        expectedItem.setId(TEST_ID);
//
//        List<Item> page = List.of(expectedItem);
//
//        doReturn(Flux.fromIterable(page))
//            .when(itemRepository)
//            .findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
//                anyString(), anyString(), any(PageRequest.class));
//
//
//        StepVerifier.create(
//            itemService.findBySearchParams(TEST_PAGE, TEST_PAGE_SIZE, TEST_SORT_NO, TEST_SEARCH)
//        ).assertNext(actualItem ->
//            assertEquals(expectedItem, actualItem)
//        ).verifyComplete();
//
//        verify(itemRepository, times(1))
//            .findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
//                TEST_SEARCH.toUpperCase(),
//                TEST_SEARCH.toUpperCase(),
//                PageRequest.of(TEST_PAGE - 1, TEST_PAGE_SIZE));
//    }
//
//    @Test
//    void getTotalSearchedElements_shouldReturnCountOfElementsByFilter() {
//        doReturn(Mono.just(1L))
//            .when(itemRepository)
//            .countByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
//                anyString(), anyString());
//
//        StepVerifier.create(
//            itemService.getTotalSearchedElements(TEST_SEARCH)
//        ).assertNext(count ->
//            assertEquals(1L, count)
//        ).verifyComplete();
//
//        verify(itemRepository, times(1))
//            .countByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
//                TEST_SEARCH.toUpperCase(), TEST_SEARCH.toUpperCase());
//    }
//
//    @Test
//    void getTotalCount_shouldReturnCount() {
//        doReturn(Mono.just(1L)).when(itemRepository).count();
//
//        StepVerifier.create(
//            itemService.getTotalCount()
//        ).assertNext(count ->
//            assertEquals(1L, count)
//        ).verifyComplete();
//
//        verify(itemRepository, times(1)).count();
//    }
//
//    @Test
//    void getItemById_shouldReturnItem() {
//        Item expextedItem = populateItem();
//        expextedItem.setId(TEST_ID);
//
//        doReturn(Mono.just(expextedItem)).when(itemRepository).findById(anyLong());
//
//        StepVerifier.create(
//            itemService.getItemById(TEST_ID)
//        ).assertNext(actualItem ->
//            assertEquals(expextedItem, actualItem)
//        ).verifyComplete();
//
//        verify(itemRepository, times(1)).findById(TEST_ID);
//    }
//
//    @Test
//    void getItemById_shouldReturnEmpty_whenNotFound() {
//        doReturn(Mono.empty()).when(itemRepository).findById(anyLong());
//
//        StepVerifier.create(
//            itemService.getItemById(TEST_ID)
//        ).assertNext(actualItem ->
//                assertEquals(new Item(), actualItem)
//        ).verifyComplete();
//
//        verify(itemRepository, times(1)).findById(TEST_ID);
//    }
//
//    @Test
//    void getAllInCart_shouldReturnItemsInCart() {
//        Item cartItem = populateItem();
//        Item item = populateItem();
//        item.setCount(0);
//
//        List<Item> page = List.of(cartItem);
//
//        doReturn(Flux.fromIterable(page)).when(itemRepository).findAllByCountGreaterThan(anyInt());
//
//        StepVerifier.create(
//            itemService.getAllInCart()
//        ).assertNext(actualItem ->
//            assertEquals(cartItem, actualItem)
//        ).verifyComplete();
//
//        verify(itemRepository, times(1)).findAllByCountGreaterThan(0);
//    }
//
//    @Test
//    void changeAmount_shouldIncreaseAmount() {
//        Item item = populateItem();
//        item.setId(TEST_ID);
//
//        Item expectedItem = populateItem();
//        expectedItem.setId(TEST_ID);
//        expectedItem.setCount(item.getCount() + 1);
//
//        doReturn(Mono.just(item)).when(itemRepository).findById(anyLong());
//        doReturn(Mono.just(expectedItem)).when(itemRepository).save(any(Item.class));
//
//        StepVerifier.create(
//            itemService.changeAmount(item.getId(), "PLUS")
//        ).assertNext(actualItem ->
//            assertEquals(actualItem, expectedItem)
//        ).verifyComplete();
//
//        verify(itemRepository, times(1)).findById(TEST_ID);
//        verify(itemRepository, times(1)).save(expectedItem);
//    }
//
//    @Test
//    void changeAmount_shouldDecreaseAmount() {
//        Item item = populateItem();
//        item.setId(TEST_ID);
//        item.setCount(2);
//
//        Item expectedItem = populateItem();
//        expectedItem.setId(TEST_ID);
//        expectedItem.setCount(item.getCount() - 1);
//
//        doReturn(Mono.just(item)).when(itemRepository).findById(anyLong());
//        doReturn(Mono.just(expectedItem)).when(itemRepository).save(any(Item.class));
//
//        StepVerifier.create(
//            itemService.changeAmount(item.getId(), "MINUS")
//        ).assertNext(actualItem ->
//            assertEquals(actualItem, expectedItem)
//        ).verifyComplete();
//
//        verify(itemRepository, times(1)).findById(TEST_ID);
//        verify(itemRepository, times(1)).save(expectedItem);
//    }
//
//    @Test
//    void changeAmount_shouldDecreaseAmountFromZero() {
//        Item item = populateItem();
//        item.setId(TEST_ID);
//        item.setCount(0);
//
//        Item expectedItem = populateItem();
//        expectedItem.setId(TEST_ID);
//        expectedItem.setCount(item.getCount());
//
//        doReturn(Mono.just(item)).when(itemRepository).findById(anyLong());
//        doReturn(Mono.just(expectedItem)).when(itemRepository).save(any(Item.class));
//
//        StepVerifier.create(
//            itemService.changeAmount(item.getId(), "MINUS")
//        ).assertNext(actualItem ->
//            assertEquals(actualItem, expectedItem)
//        ).verifyComplete();
//
//        verify(itemRepository, times(1)).findById(TEST_ID);
//        verify(itemRepository, times(1)).save(expectedItem);
//    }
//
//    @Test
//    void changeAmount_shouldDelete() {
//        Item item = populateItem();
//        item.setId(TEST_ID);
//        item.setCount(2);
//
//        Item expectedItem = populateItem();
//        expectedItem.setId(TEST_ID);
//        expectedItem.setCount(0);
//
//        doReturn(Mono.just(item)).when(itemRepository).findById(anyLong());
//        doReturn(Mono.just(expectedItem)).when(itemRepository).save(any(Item.class));
//
//        StepVerifier.create(
//            itemService.changeAmount(item.getId(), "DELETE")
//        ).assertNext(actualItem ->
//            assertEquals(actualItem, expectedItem)
//        ).verifyComplete();
//
//        verify(itemRepository, times(1)).findById(TEST_ID);
//        verify(itemRepository, times(1)).save(expectedItem);
//    }
//
//    @Test
//    void addItem_shouldSaveItem() throws IOException {
//        FilePart image = populateFilePart();
//        Item item = populateItem();
//
//        doReturn(Mono.just(item)).when(itemRepository).save(any(Item.class));
//
//        itemService.addItem(item, image).block();
//
//        Path expectedPath = tempDir.resolve(TEST_FILE_NAME);
//        assertTrue(Files.exists(expectedPath));
//        assertEquals(TEST_FILE_CONTENT, Files.readString(expectedPath));
//
//        verify(itemRepository, times(1)).save(item);
//    }
//
//    @Test
//    void updateItems_shouldUpdateItems() {
//        Item item = populateItem();
//        List<Item> items = List.of(item);
//
//        doReturn(Flux.fromIterable(items)).when(itemRepository).saveAll(anyList());
//
//        StepVerifier.create(
//            itemService.updateItems(items)
//        ).assertNext(actualItem ->
//            assertEquals(actualItem, item)
//        ).verifyComplete();
//
//        verify(itemRepository, times(1)).saveAll(items);
//    }
//
//    private FilePart populateFilePart() {
//        FilePart filePart = mock(FilePart.class);
//
//        when(filePart.filename()).thenReturn(TEST_FILE_NAME);
//
//        DataBuffer buffer = new DefaultDataBufferFactory().wrap(TEST_FILE_CONTENT.getBytes());
//        Flux<DataBuffer> content = Flux.just(buffer);
//        when(filePart.content()).thenReturn(content);
//
//        when(filePart.transferTo(any(Path.class))).thenAnswer(invocation -> {
//            Path path = invocation.getArgument(0);
//            return Mono.fromRunnable(() -> {
//                try {
//                    Files.writeString(path, TEST_FILE_CONTENT);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            });
//        });
//
//        return filePart;
//    }
//
//    private Item populateItem() {
//        return populateItemNoId();
//    }
//}
