package ru.custom.storefrontapp.unit.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockReset;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.custom.storefrontapp.repository.RedisCartCacheRepository;
import ru.custom.storefrontapp.service.CartService;

import java.util.AbstractMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = CartService.class)
class CartServiceTest extends BaseServiceTest {
    @MockitoBean(reset = MockReset.BEFORE)
    private RedisCartCacheRepository cartCacheRepository;

    @Autowired
    private CartService cartService;

    @Test
    void changeQuantity_shouldIncrementQuantity() {
        doReturn(Mono.just(TEST_COUNT + 1)).when(cartCacheRepository).updateItemQuantity(anyString(), anyInt());

        StepVerifier.create(cartService.changeQuantity(TEST_ID, TEST_PLUS_ACTION))
            .assertNext(Assertions::assertTrue)
            .verifyComplete();

        verify(cartCacheRepository, times(1)).updateItemQuantity(TEST_ID.toString(), 1);
    }

    @Test
    void changeQuantity_shouldDecrementQuantity() {
        doReturn(Mono.just(TEST_COUNT)).when(cartCacheRepository).updateItemQuantity(anyString(), anyInt());
        doReturn(Mono.just(TEST_COUNT + 1)).when(cartCacheRepository).findById(anyString());

        StepVerifier.create(cartService.changeQuantity(TEST_ID, TEST_MINUS_ACTION))
            .assertNext(Assertions::assertTrue)
            .verifyComplete();

        verify(cartCacheRepository, times(1)).updateItemQuantity(TEST_ID.toString(), - 1);
    }

    @Test
    void changeQuantity_shouldDecrementQuantity_byDeleteMethod() {
        doReturn(Mono.just(TEST_COUNT)).when(cartCacheRepository).deleteItem(anyString());
        doReturn(Mono.just(TEST_COUNT)).when(cartCacheRepository).findById(anyString());

        StepVerifier.create(cartService.changeQuantity(TEST_ID, TEST_MINUS_ACTION))
            .assertNext(Assertions::assertTrue)
            .verifyComplete();

        verify(cartCacheRepository, times(1)).deleteItem(TEST_ID.toString());
        verify(cartCacheRepository, times(0)).updateItemQuantity(any(), any());
    }

    @Test
    void changeQuantity_shouldRemoveItem() {
        doReturn(Mono.just(TEST_COUNT)).when(cartCacheRepository).deleteItem(anyString());

        StepVerifier.create(cartService.changeQuantity(TEST_ID, TEST_DELETE_ACTION))
            .assertNext(Assertions::assertTrue)
            .verifyComplete();

        verify(cartCacheRepository, times(1)).deleteItem(TEST_ID.toString());
    }

    @Test
    void getCart_shouldReturnCart() {
        Map.Entry<Object, Object> entry = new AbstractMap.SimpleEntry<>(TEST_ID.toString(), TEST_COUNT.toString());

        doReturn(Flux.just(entry)).when(cartCacheRepository).findAll();

        StepVerifier.create(cartService.getCart())
            .assertNext(actualCartEntry -> assertEquals(entry, actualCartEntry))
            .verifyComplete();

        verify(cartCacheRepository, times(1)).findAll();
    }

    @Test
    void getItemQuantity_shouldReturnQuantity() {
        doReturn(Mono.just(TEST_COUNT)).when(cartCacheRepository).findById(anyString());

        StepVerifier.create(cartService.getItemQuantity(TEST_ID))
            .assertNext(actualCount -> assertEquals(TEST_COUNT, actualCount))
            .verifyComplete();

        verify(cartCacheRepository, times(1)).findById(TEST_ID.toString());
    }

    @Test
    void cleanCart_shouldDeleteCartCacheAndReturn() {
        doReturn(Mono.just(1L)).when(cartCacheRepository).deleteCart();

        StepVerifier.create(cartService.cleanCart())
            .assertNext(actualCount -> assertEquals(1L, actualCount))
            .verifyComplete();

        verify(cartCacheRepository, times(1)).deleteCart();
    }
}
