package ru.custom.intershop.mapper;

import ru.custom.intershop.dto.ItemDto;
import ru.custom.intershop.model.Item;

public class ItemMapper {
    private ItemMapper() {}

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
