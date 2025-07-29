package ru.custom.intershop.unit.service;

import ru.custom.intershop.model.Item;

import java.math.BigDecimal;

abstract class BaseServiceTest {
    protected static final Long TEST_ID = 1L;
    protected static final String TEST_TITLE = "test title";
    protected static final String TEST_DESCRIPTION = "test description";
    protected static final BigDecimal TEST_PRICE = BigDecimal.valueOf(100);
    protected static final String TEST_IMG_PATH = "/test/path.png";

    protected Item populateItemNoId() {
        Item item = new Item();

        item.setTitle(TEST_TITLE);
        item.setDescription(TEST_DESCRIPTION);
        item.setCount(1);
        item.setPrice(TEST_PRICE);
        item.setImgPath(TEST_IMG_PATH);

        return item;
    }
}
