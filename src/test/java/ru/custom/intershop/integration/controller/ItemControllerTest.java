package ru.custom.intershop.integration.controller;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

class ItemControllerTest extends BaseControllerTest {
    @Test
    void handleShowItem_shouldReturnItemPage() throws Exception {
        mockMvc.perform(get("/items/1"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("text/html;charset=UTF-8"))
            .andExpect(view().name("item"))
            .andExpect(model().attributeExists("item"))
            .andExpect(model().attribute("item",
                Matchers.hasProperty("id", Matchers.comparesEqualTo(1L))));
    }

    @Test
    void handleChangeAmount_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/items/1"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void handleChangeAmount_shouldAddItem() throws Exception {
        mockMvc.perform(post("/items/1")
                .param("action", "PLUS"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/items/1"));
    }
}
