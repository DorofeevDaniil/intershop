package ru.custom.storefrontapp.unit.mapper;

import org.junit.jupiter.api.Test;
import ru.custom.storefrontapp.dto.CartDto;
import ru.custom.storefrontapp.dto.ItemDto;
import ru.custom.storefrontapp.mapper.CartMapper;
import ru.custom.storefrontapp.model.Cart;
import ru.custom.storefrontapp.model.Item;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CartMapperTest extends BaseMapperTest {

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

    private Cart populateCart() {
        Item item = populateItem();

        return new Cart(List.of(item), item.getPrice());
    }

    private CartDto populateCartDto() {
        ItemDto itemDto = populateItemDto();

        return new CartDto(List.of(itemDto), itemDto.getPrice());
    }
}
