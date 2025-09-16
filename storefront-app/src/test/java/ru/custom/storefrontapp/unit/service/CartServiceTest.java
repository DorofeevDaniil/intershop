package ru.custom.storefrontapp.unit.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.custom.storefrontapp.repository.RedisCartCacheRepository;
import ru.custom.storefrontapp.service.CartService;

import java.util.AbstractMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CartServiceTest extends BaseServiceTest {
    @Mock
    private RedisCartCacheRepository cartCacheRepository;

    @InjectMocks
    private CartService cartService;

    @Test
    void changeQuantity_shouldIncrementQuantity() {
        doReturn(Mono.just(TEST_COUNT + 1)).when(cartCacheRepository).updateItemQuantity(anyString(), anyInt(), anyLong());

        StepVerifier.create(cartService.changeQuantity(TEST_ID, TEST_PLUS_ACTION, TEST_USER_ID))
            .assertNext(Assertions::assertTrue)
            .verifyComplete();

        verify(cartCacheRepository, times(1)).updateItemQuantity(TEST_ID.toString(), 1, TEST_USER_ID);
    }

    @Test
    void changeQuantity_shouldDecrementQuantity() {
        doReturn(Mono.just(TEST_COUNT)).when(cartCacheRepository).updateItemQuantity(anyString(), anyInt(), anyLong());
        doReturn(Mono.just(TEST_COUNT + 1)).when(cartCacheRepository).findById(anyString(), anyLong());

        StepVerifier.create(cartService.changeQuantity(TEST_ID, TEST_MINUS_ACTION, TEST_USER_ID))
            .assertNext(Assertions::assertTrue)
            .verifyComplete();

        verify(cartCacheRepository, times(1)).updateItemQuantity(TEST_ID.toString(), - 1, TEST_USER_ID);
    }

    @Test
    void changeQuantity_shouldDecrementQuantity_byDeleteMethod() {
        doReturn(Mono.just(TEST_COUNT)).when(cartCacheRepository).deleteItem(anyString(), anyLong());
        doReturn(Mono.just(TEST_COUNT)).when(cartCacheRepository).findById(anyString(), anyLong());

        StepVerifier.create(cartService.changeQuantity(TEST_ID, TEST_MINUS_ACTION, TEST_USER_ID))
            .assertNext(Assertions::assertTrue)
            .verifyComplete();

        verify(cartCacheRepository, times(1)).deleteItem(TEST_ID.toString(), TEST_USER_ID);
        verify(cartCacheRepository, times(0)).updateItemQuantity(any(), any(), anyLong());
    }

    @Test
    void changeQuantity_shouldRemoveItem() {
        doReturn(Mono.just(TEST_COUNT)).when(cartCacheRepository).deleteItem(anyString(), anyLong());

        StepVerifier.create(cartService.changeQuantity(TEST_ID, TEST_DELETE_ACTION, TEST_USER_ID))
            .assertNext(Assertions::assertTrue)
            .verifyComplete();

        verify(cartCacheRepository, times(1)).deleteItem(TEST_ID.toString(), TEST_USER_ID);
    }

    @Test
    void getCart_shouldReturnCart() {
        Map.Entry<Object, Object> entry = new AbstractMap.SimpleEntry<>(TEST_ID.toString(), TEST_COUNT.toString());

        doReturn(Flux.just(entry)).when(cartCacheRepository).findAll(TEST_USER_ID);

        StepVerifier.create(cartService.getCart(TEST_USER_ID))
            .assertNext(actualCartEntry -> assertEquals(entry, actualCartEntry))
            .verifyComplete();

        verify(cartCacheRepository, times(1)).findAll(TEST_USER_ID);
    }

    @Test
    void getItemQuantity_shouldReturnQuantity() {
        doReturn(Mono.just(TEST_COUNT)).when(cartCacheRepository).findById(anyString(), anyLong());

        StepVerifier.create(cartService.getItemQuantity(TEST_ID, TEST_USER_ID))
            .assertNext(actualCount -> assertEquals(TEST_COUNT, actualCount))
            .verifyComplete();

        verify(cartCacheRepository, times(1)).findById(TEST_ID.toString(), TEST_USER_ID);
    }

    @Test
    void cleanCart_shouldDeleteCartCacheAndReturn() {
        doReturn(Mono.just(1L)).when(cartCacheRepository).deleteCart(anyLong());

        StepVerifier.create(cartService.cleanCart(TEST_USER_ID))
            .assertNext(actualCount -> assertEquals(1L, actualCount))
            .verifyComplete();

        verify(cartCacheRepository, times(1)).deleteCart(TEST_USER_ID);
    }
}
