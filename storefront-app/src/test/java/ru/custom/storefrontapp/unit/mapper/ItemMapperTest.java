package ru.custom.storefrontapp.unit.mapper;

import org.junit.jupiter.api.Test;
import ru.custom.storefrontapp.dto.ItemDto;
import ru.custom.storefrontapp.mapper.ItemMapper;
import ru.custom.storefrontapp.model.Item;

import static org.junit.jupiter.api.Assertions.*;

class ItemMapperTest extends BaseMapperTest {

    @Test
    void toItemDto_shouldMapToDTO() {
        Item item = populateItem();
        ItemDto expectedItemDto = populateItemDto();

        ItemDto actualItemDto = ItemMapper.toItemDto(item);

        assertEquals(expectedItemDto, actualItemDto);
    }

    @Test
    void toItem_shouldMapToItem() {
        ItemDto itemDto = populateItemDto();
        Item expectedItem = populateItem();

        Item actualItem = ItemMapper.toItem(itemDto);

        assertEquals(expectedItem, actualItem);
    }
}