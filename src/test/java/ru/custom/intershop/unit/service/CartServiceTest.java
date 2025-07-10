package ru.custom.intershop.unit.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockReset;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.custom.intershop.model.Cart;
import ru.custom.intershop.model.Item;
import ru.custom.intershop.service.CartService;
import ru.custom.intershop.service.ItemService;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = CartService.class)
class CartServiceTest {
    @MockitoBean(reset = MockReset.BEFORE)
    private ItemService itemService;

    @Autowired
    private CartService cartService;

    private static final Long TEST_ID = 1L;
    private static final String TEST_ACTION = "PLUS";
    private static final String TEST_TITLE = "test title";
    private static final String TEST_DESCRIPTION = "test description";
    private static final BigDecimal TEST_PRICE = BigDecimal.valueOf(100);
    private static final String TEST_IMG_PATH = "/test/path.png";

    @Test
    void changeItemAmount_shouldChangeAmount() {
        doNothing().when(itemService).changeAmount(anyLong(), anyString());

        cartService.changeItemAmount(TEST_ID, TEST_ACTION);

        verify(itemService, times(1)).changeAmount(TEST_ID, TEST_ACTION);
    }

    @Test
    void getCart_shouldReturnCart() {
        Item item = new Item();

        item.setId(TEST_ID);
        item.setTitle(TEST_TITLE);
        item.setDescription(TEST_DESCRIPTION);
        item.setCount(1);
        item.setPrice(TEST_PRICE);
        item.setImgPath(TEST_IMG_PATH);

        List<Item> items = List.of(item);

        Cart expectedCart = new Cart(items, TEST_PRICE);

        doReturn(items).when(itemService).getAllInCart();

        Cart actualCart = cartService.getCart();

        verify(itemService, times(1)).getAllInCart();
        assertEquals(expectedCart, actualCart);
    }
}
