package ru.custom.intershop.mapper;

import ru.custom.intershop.dto.CartDto;
import ru.custom.intershop.dto.ItemDto;
import ru.custom.intershop.model.Cart;
import ru.custom.intershop.model.Item;

import java.util.List;

public class CartMapper {
    private CartMapper() {}

    public static CartDto toCartDto(Cart cart) {
        List<ItemDto> items = cart.getItems().stream().map(ItemMapper::toItemDto).toList();

        return new CartDto(items, cart.getTotal());
    }

    public static Cart toCart(CartDto cartDto) {
        List<Item> items = cartDto.getItems().stream().map(ItemMapper::toItem).toList();

        return new Cart(items, cartDto.getTotal());
    }
}
