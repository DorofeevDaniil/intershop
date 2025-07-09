package ru.custom.intershop.mapper;

import ru.custom.intershop.dto.CartDto;
import ru.custom.intershop.dto.ItemDto;
import ru.custom.intershop.model.Cart;
import ru.custom.intershop.model.Item;

import java.util.List;

public class CartMapper {
    private CartMapper() {}

    public static CartDto toCartDto(Cart cart) {
        List<ItemDto> items = cart.getItems().stream().map(CartMapper::toItemDto).toList();

        return new CartDto(items, cart.getTotal());
    }

    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setTitle(item.getTitle());
        itemDto.setDescription(item.getDescription());
        itemDto.setImgPath(item.getImgPath());
        itemDto.setPrice(item.getPrice());
        itemDto.setCount(item.getCount());

        return itemDto;
    }

    public static Cart toCart(CartDto cartDto) {
        List<Item> items = cartDto.getItems().stream().map(CartMapper::toItem).toList();

        return new Cart(items, cartDto.getTotal());
    }

    public static Item toItem(ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setTitle(itemDto.getTitle());
        item.setDescription(itemDto.getDescription());
        item.setImgPath(itemDto.getImgPath());
        item.setPrice(itemDto.getPrice());
        item.setCount(itemDto.getCount());

        return item;
    }
}
