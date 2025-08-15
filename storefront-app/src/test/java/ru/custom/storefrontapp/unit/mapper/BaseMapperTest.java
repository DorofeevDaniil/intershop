package ru.custom.storefrontapp.unit.mapper;

import ru.custom.storefrontapp.dto.ItemDto;
import ru.custom.storefrontapp.model.Item;

import java.math.BigDecimal;

abstract class BaseMapperTest {
    protected static final Long TEST_ID = 1L;
    protected static final String TEST_TITLE = "test title";
    protected static final String TEST_DESCRIPTION = "test description";
    protected static final BigDecimal TEST_PRICE = BigDecimal.valueOf(100);
    protected static final String TEST_IMG_PATH = "/test/path.png";

    protected Item populateItem() {
        Item item = new Item();

        item.setId(TEST_ID);
        item.setTitle(TEST_TITLE);
        item.setDescription(TEST_DESCRIPTION);
        item.setCount(1);
        item.setPrice(TEST_PRICE);
        item.setImgPath(TEST_IMG_PATH);

        return item;
    }

    protected ItemDto populateItemDto() {
        ItemDto itemDto = new ItemDto();

        itemDto.setId(TEST_ID);
        itemDto.setTitle(TEST_TITLE);
        itemDto.setDescription(TEST_DESCRIPTION);
        itemDto.setCount(1);
        itemDto.setPrice(TEST_PRICE);
        itemDto.setImgPath(TEST_IMG_PATH);

        return itemDto;
    }
}
