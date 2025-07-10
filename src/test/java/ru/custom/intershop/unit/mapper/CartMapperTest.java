package ru.custom.intershop.unit.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.custom.intershop.dto.CartDto;
import ru.custom.intershop.dto.ItemDto;
import ru.custom.intershop.mapper.CartMapper;
import ru.custom.intershop.model.Cart;
import ru.custom.intershop.model.Item;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = CartMapper.class)
class CartMapperTest {
    private static final Long TEST_ID = 1L;
    private static final String TEST_TITLE = "test title";
    private static final String TEST_DESCRIPTION = "test description";
    private static final BigDecimal TEST_PRICE = BigDecimal.valueOf(100);
    private static final String TEST_IMG_PATH = "/test/path.png";

    @Test
    void toItemDto_shouldMapToDTO() {
        Item item = populateItem();
        ItemDto expectedItemDto = populateItemDto();

        ItemDto actualItemDto = CartMapper.toItemDto(item);

        assertEquals(expectedItemDto, actualItemDto);
    }

    @Test
    void toItem_shouldMapToItem() {
        ItemDto itemDto = populateItemDto();
        Item expectedItem = populateItem();

        Item actualItem = CartMapper.toItem(itemDto);

        assertEquals(expectedItem, actualItem);
    }

    @Test
    void toCartDto_shouldMapToDTO() {
        Cart cart = populateCart();
        CartDto expectedCartDto = populateCartDto();

        CartDto actualCartDto = CartMapper.toCartDto(cart);

        assertEquals(expectedCartDto, actualCartDto);
    }

    @Test
    void toCart_shouldMapToCart() {
        CartDto cartDto = populateCartDto();
        Cart expectedCart = populateCart();

        Cart actualCart = CartMapper.toCart(cartDto);

        assertEquals(expectedCart, actualCart);
    }

    private Item populateItem() {
        Item item = new Item();

        item.setId(TEST_ID);
        item.setTitle(TEST_TITLE);
        item.setDescription(TEST_DESCRIPTION);
        item.setCount(1);
        item.setPrice(TEST_PRICE);
        item.setImgPath(TEST_IMG_PATH);

        return item;
    }

    private ItemDto populateItemDto() {
        ItemDto itemDto = new ItemDto();

        itemDto.setId(TEST_ID);
        itemDto.setTitle(TEST_TITLE);
        itemDto.setDescription(TEST_DESCRIPTION);
        itemDto.setCount(1);
        itemDto.setPrice(TEST_PRICE);
        itemDto.setImgPath(TEST_IMG_PATH);

        return itemDto;
    }

    private Cart populateCart() {
        Item item = populateItem();

        return new Cart(List.of(item), item.getPrice());
    }

    private CartDto populateCartDto() {
        ItemDto itemDto = populateItemDto();

        return new CartDto(List.of(itemDto), itemDto.getPrice());
    }
}
