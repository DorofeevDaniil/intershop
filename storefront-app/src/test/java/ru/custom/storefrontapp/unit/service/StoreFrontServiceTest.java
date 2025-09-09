package ru.custom.storefrontapp.unit.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;
import ru.custom.storefrontapp.dto.CartDto;
import ru.custom.storefrontapp.dto.ItemDto;
import ru.custom.storefrontapp.mapper.ItemMapper;
import ru.custom.storefrontapp.pagination.PagedResult;
import ru.custom.storefrontapp.service.CartService;
import ru.custom.storefrontapp.service.ItemService;
import ru.custom.storefrontapp.service.StoreFrontService;

import java.math.BigDecimal;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoreFrontServiceTest extends BaseServiceTest {
    @Mock
    private ItemService itemService;
    @Mock
    private CartService cartService;
    @InjectMocks
    private StoreFrontService storeFrontService;

    @Test
    void getItem_shouldReturnItem() {
        ItemDto expectedItem = populateItemDto();
        expectedItem.setId(TEST_ID);

        doReturn(Mono.just(expectedItem)).when(itemService).getItemCardById(anyLong());
        doReturn(Mono.just(1)).when(cartService).getItemQuantity(anyLong());

        StepVerifier.create(storeFrontService.getItem(TEST_ID))
            .assertNext(actualItem -> assertEquals(expectedItem, actualItem))
            .verifyComplete();

        verify(itemService, times(1)).getItemCardById(TEST_ID);
        verify(cartService, times(1)).getItemQuantity(TEST_ID);
    }

    @Test
    void changeItemQuantity_shouldChangeItemsQuantity() {
        doReturn(Mono.just(true)).when(cartService).changeQuantity(anyLong(), anyString());

        StepVerifier.create(storeFrontService.changeItemQuantity(TEST_ID, TEST_PLUS_ACTION))
            .assertNext(Assertions::assertTrue)
            .verifyComplete();

        verify(cartService, times(1)).changeQuantity(TEST_ID, TEST_PLUS_ACTION);
    }

    @Test
    void getPage_shouldReturnPagedResult() {
        ItemDto firstItemDto = populateItemDto();
        firstItemDto.setId(TEST_ID);
        ItemDto secondItemDto = populateItemDto();
        secondItemDto.setId(TEST_ID + 1);

        Tuple2<List<ItemDto>, Long> tuple =  Tuples.of(List.of(firstItemDto, secondItemDto), 2L);
        PagedResult<List<ItemDto>> expectedPagedResult =
            new PagedResult<>(
                splitRows(List.of(firstItemDto, secondItemDto), 3), TEST_PAGE, TEST_PAGE_SIZE, 2L);

        doReturn(Mono.just(tuple)).when(itemService).searchAndPaginate(anyInt(), anyInt(), anyString(), anyString());
        doReturn(Mono.just(2)).when(cartService).getItemQuantity(anyLong());

        StepVerifier.create(storeFrontService.getPage(TEST_PAGE, TEST_PAGE_SIZE, TEST_SORT_NO, TEST_SEARCH))
            .assertNext(actualPagedResult -> assertEquals(expectedPagedResult, actualPagedResult))
            .verifyComplete();

        verify(itemService, times(1)).searchAndPaginate(TEST_PAGE, TEST_PAGE_SIZE, TEST_SORT_NO, TEST_SEARCH);
        verify(cartService, times(1)).getItemQuantity(TEST_ID);
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

        doReturn(Flux.just(entry)).when(cartService).getCart();
        doReturn(Mono.just(expectedItem)).when(itemService).getItemCardById(TEST_ID);

        StepVerifier.create(storeFrontService.getCart())
            .assertNext(actualCartEntry -> assertEquals(expectedCart, actualCartEntry))
            .verifyComplete();

        verify(cartService, times(1)).getCart();
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