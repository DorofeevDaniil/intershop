package ru.custom.storefrontapp.unit.mapper;//package ru.custom.intershop.unit.mapper;
//
//import org.junit.jupiter.api.Test;
//import ru.custom.intershop.dto.CartDto;
//import ru.custom.intershop.dto.ItemDto;
//import ru.custom.intershop.mapper.CartMapper;
//import ru.custom.intershop.model.Cart;
//import ru.custom.intershop.model.Item;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class CartMapperTest extends BaseMapperTest {
//
//    @Test
//    void toCartDto_shouldMapToDTO() {
//        Cart cart = populateCart();
//        CartDto expectedCartDto = populateCartDto();
//
//        CartDto actualCartDto = CartMapper.toCartDto(cart);
//
//        assertEquals(expectedCartDto, actualCartDto);
//    }
//
//    @Test
//    void toCart_shouldMapToCart() {
//        CartDto cartDto = populateCartDto();
//        Cart expectedCart = populateCart();
//
//        Cart actualCart = CartMapper.toCart(cartDto);
//
//        assertEquals(expectedCart, actualCart);
//    }
//
//    private Cart populateCart() {
//        Item item = populateItem();
//
//        return new Cart(List.of(item), item.getPrice());
//    }
//
//    private CartDto populateCartDto() {
//        ItemDto itemDto = populateItemDto();
//
//        return new CartDto(List.of(itemDto), itemDto.getPrice());
//    }
//}
