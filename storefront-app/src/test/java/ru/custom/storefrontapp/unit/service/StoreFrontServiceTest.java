package ru.custom.storefrontapp.unit.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;
import ru.custom.storefrontapp.dto.CartDto;
import ru.custom.storefrontapp.dto.ItemDto;
import ru.custom.storefrontapp.mapper.ItemMapper;
import ru.custom.storefrontapp.model.User;
import ru.custom.storefrontapp.pagination.PagedResult;
import ru.custom.storefrontapp.service.CartService;
import ru.custom.storefrontapp.service.ItemService;
import ru.custom.storefrontapp.service.StoreFrontService;
import ru.custom.storefrontapp.service.UserManagementService;
import ru.custom.storefrontapp.util.SecurityUtils;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoreFrontServiceTest extends BaseServiceTest {
    @Mock
    private ItemService itemService;
    @Mock
    private CartService cartService;
    @Mock
    private SecurityUtils securityUtils;
    @Mock
    private UserManagementService userManagementService;
    @Mock
    private Authentication authentication;
    @InjectMocks
    private StoreFrontService storeFrontService;

    @BeforeEach
    void setUp() {
        User testUser = new User();
        testUser.setId(TEST_USER_ID);
        testUser.setPassword(TEST_USER_NAME);
        testUser.setUsername("user");
        testUser.setEnabled(true);

        lenient().doReturn(Mono.just(testUser)).when(userManagementService).findUserByName(anyString());
    }

    @Test
    void getItem_shouldReturnItemWithCountInCart_whenUserIsAuthorized() {
        ItemDto expectedItem = populateItemDto();
        expectedItem.setId(TEST_ID);

        doReturn(Mono.just(expectedItem)).when(itemService).getItemCardById(anyLong());
        doReturn(Mono.just(5)).when(cartService).getItemQuantity(1L, TEST_USER_ID);
        doReturn(Optional.of(TEST_USER_NAME)).when(securityUtils).getUsername(any());

        StepVerifier.create(storeFrontService.getItem(TEST_ID, authentication))
            .assertNext(actual -> {
                assertEquals(5, actual.getCount());
                assertEquals("test title", actual.getTitle());
            })
            .verifyComplete();

        verify(itemService, times(1)).getItemCardById(TEST_ID);
        verify(cartService, times(1)).getItemQuantity(TEST_ID, TEST_USER_ID);
        verify(securityUtils, times(1)).getUsername(authentication);
    }

    @Test
    void getItem_shouldReturnItemWithCountZero_whenUserIsAnonymous() {
        ItemDto expectedItem = populateItemDto();
        expectedItem.setId(TEST_ID);

        doReturn(Mono.just(expectedItem)).when(itemService).getItemCardById(anyLong());
        doReturn(Optional.empty()).when(securityUtils).getUsername(any());

        StepVerifier.create(storeFrontService.getItem(TEST_ID, authentication))
                .assertNext(actual -> {
                    assertEquals(0, actual.getCount());
                    assertEquals("test title", actual.getTitle());
                })
                .verifyComplete();

        verify(itemService, times(1)).getItemCardById(TEST_ID);
        verify(securityUtils, times(1)).getUsername(authentication);
    }

    @Test
    void changeItemQuantity_shouldChangeItemsQuantity() {
        doReturn(Mono.just(true)).when(cartService).changeQuantity(anyLong(), anyString(), anyLong());

        StepVerifier.create(storeFrontService.changeItemQuantity(TEST_ID, TEST_PLUS_ACTION, TEST_USER_NAME))
            .assertNext(Assertions::assertTrue)
            .verifyComplete();

        verify(cartService, times(1)).changeQuantity(TEST_ID, TEST_PLUS_ACTION, TEST_USER_ID);
    }

    @Test
    void getPage_shouldReturnPagedResultWithCountInCart_whenUserIsAuthorized() {
        ItemDto firstItemDto = populateItemDto();
        firstItemDto.setId(TEST_ID);
        ItemDto secondItemDto = populateItemDto();
        secondItemDto.setId(TEST_ID + 1);

        Tuple2<List<ItemDto>, Long> tuple =  Tuples.of(List.of(firstItemDto, secondItemDto), 2L);
        PagedResult<List<ItemDto>> expectedPagedResult =
            new PagedResult<>(
                splitRows(List.of(firstItemDto, secondItemDto), 3), TEST_PAGE, TEST_PAGE_SIZE, 2L);

        doReturn(Mono.just(tuple)).when(itemService).searchAndPaginate(anyInt(), anyInt(), anyString(), anyString());
        doReturn(Mono.just(2)).when(cartService).getItemQuantity(anyLong(), anyLong());
        doReturn(Optional.of(TEST_USER_NAME)).when(securityUtils).getUsername(any());

        StepVerifier.create(storeFrontService.getPage(TEST_PAGE, TEST_PAGE_SIZE, TEST_SORT_NO, TEST_SEARCH, authentication))
            .assertNext(actualPagedResult -> {
                assertEquals(2, actualPagedResult.getContent().getFirst().getFirst().getCount());
                assertEquals("test title", actualPagedResult.getContent().getFirst().getFirst().getTitle());
                assertEquals(expectedPagedResult, actualPagedResult);
            })
            .verifyComplete();

        verify(itemService, times(1)).searchAndPaginate(TEST_PAGE, TEST_PAGE_SIZE, TEST_SORT_NO, TEST_SEARCH);
        verify(cartService, times(1)).getItemQuantity(TEST_ID, TEST_USER_ID);
    }

    @Test
    void getPage_shouldReturnPagedResultWithCountZero_whenUserIsAnonymous() {
        ItemDto firstItemDto = populateItemDto();
        firstItemDto.setId(TEST_ID);
        ItemDto secondItemDto = populateItemDto();
        secondItemDto.setId(TEST_ID + 1);

        Tuple2<List<ItemDto>, Long> tuple =  Tuples.of(List.of(firstItemDto, secondItemDto), 2L);
        PagedResult<List<ItemDto>> expectedPagedResult =
                new PagedResult<>(
                        splitRows(List.of(firstItemDto, secondItemDto), 3), TEST_PAGE, TEST_PAGE_SIZE, 2L);

        doReturn(Mono.just(tuple)).when(itemService).searchAndPaginate(anyInt(), anyInt(), anyString(), anyString());
        doReturn(Optional.empty()).when(securityUtils).getUsername(any());

        StepVerifier.create(storeFrontService.getPage(TEST_PAGE, TEST_PAGE_SIZE, TEST_SORT_NO, TEST_SEARCH, authentication))
                .assertNext(actualPagedResult -> {
                    assertEquals(0, actualPagedResult.getContent().getFirst().getFirst().getCount());
                    assertEquals("test title", actualPagedResult.getContent().getFirst().getFirst().getTitle());
                    assertEquals(expectedPagedResult, actualPagedResult);
                })
                .verifyComplete();

        verify(itemService, times(1)).searchAndPaginate(TEST_PAGE, TEST_PAGE_SIZE, TEST_SORT_NO, TEST_SEARCH);
    }

    @Test
    void createItem_shouldAddItem() {
        ItemDto expectedItem = populateItemDto();
        expectedItem.setId(TEST_ID);
        FilePart image = populateFilePart();

        doReturn(Mono.just(expectedItem)).when(itemService).addItem(any(ItemDto.class), any(FilePart.class));

        StepVerifier.create(storeFrontService.createItem(expectedItem, image))
            .assertNext(actualItem -> assertEquals(expectedItem, actualItem))
            .verifyComplete();

        verify(itemService, times(1)).addItem(expectedItem, image);
    }

    @Test
    void getCart_shouldReturnCartItems() {
        Map.Entry<Object, Object> entry = new AbstractMap.SimpleEntry<>(TEST_ID.toString(), TEST_COUNT.toString());
        ItemDto expectedItem = populateItemDto();
        expectedItem.setId(TEST_ID);
        CartDto expectedCart = new CartDto(List.of(expectedItem), expectedItem.getPrice().multiply(BigDecimal.valueOf(TEST_COUNT)));

        doReturn(Flux.just(entry)).when(cartService).getCart(TEST_USER_ID);
        doReturn(Mono.just(expectedItem)).when(itemService).getItemCardById(TEST_ID);

        StepVerifier.create(storeFrontService.getCart(TEST_USER_NAME))
            .assertNext(actualCartEntry -> assertEquals(expectedCart, actualCartEntry))
            .verifyComplete();

        verify(cartService, times(1)).getCart(anyLong());
        verify(itemService, times(1)).getItemCardById(anyLong());
    }

    private ItemDto populateItemDto() {
        return ItemMapper.toItemDto(populateItemNoId());
    }

    private List<List<ItemDto>> splitRows(List<ItemDto> items, int rowSize) {
        List<List<ItemDto>> rows = new ArrayList<>();
        for (int i = 0; i < items.size(); i += rowSize) {
            rows.add(items.subList(i, Math.min(i + rowSize, items.size())));
        }
        return rows;
    }
}