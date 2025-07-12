package ru.custom.intershop.integration.controller;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.custom.intershop.service.ItemService;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

class CartControllerTest extends BaseControllerTest {
    @Autowired
    private ItemService itemService;

    @Test
    void handleShowItems_shouldReturnItemsInCart() throws Exception {
        itemService.changeAmount(1L, "PLUS");

        mockMvc.perform(get("/cart/items"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("text/html;charset=UTF-8"))
            .andExpect(view().name("cart"))
            .andExpect(model().attributeExists("items"))
            .andExpect(model().attributeExists("total"))
            .andExpect(model().attributeExists("empty"))
            .andExpect(model().attribute("items", hasSize(1)))
            .andExpect(model().attribute("total", Matchers.is(BigDecimal.valueOf(100))))
            .andExpect(model().attribute("empty", Matchers.is(false)));

    }

    @Test
    void handleChangeAmount_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/cart/items/1"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void handleChangeAmount_shouldAddItem() throws Exception {
        mockMvc.perform(post("/cart/items/1")
                .param("action", "PLUS"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/cart/items"));
    }
}
