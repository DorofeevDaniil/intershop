package ru.custom.storefrontapp.mapper;

import ru.custom.storefrontapp.dto.CartDto;
import ru.custom.storefrontapp.dto.ItemDto;
import ru.custom.storefrontapp.model.Cart;
import ru.custom.storefrontapp.model.Item;

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
