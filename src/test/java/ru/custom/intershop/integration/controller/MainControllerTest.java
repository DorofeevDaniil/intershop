package ru.custom.intershop.integration.controller;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

class MainControllerTest extends BaseControllerTest {
    @Test
    void handleGetItems_shouldReturnAllItemsList_withoutFilter_withoutSort() throws Exception {
        mockMvc.perform(get("/main/items"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("text/html;charset=UTF-8"))
            .andExpect(view().name("main"))
            .andExpect(model().attributeExists("items"))
            .andExpect(model().attributeExists("sort"))
            .andExpect(model().attributeExists("paging"))
            .andExpect(model().attribute("items", contains(hasSize(2))))
            .andExpect(model().attribute("sort", Matchers.is("NO")))
            .andExpect(model().attribute("paging",
                hasProperty("totalElements", Matchers.comparesEqualTo(2L))));
    }

    @Test
    void handleGetItems_shouldReturnAllItemsList_withFilter_withoutSort() throws Exception {
        mockMvc.perform(get("/main/items")
                .param("search", "1")
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType("text/html;charset=UTF-8"))
            .andExpect(view().name("main"))
            .andExpect(model().attributeExists("items"))
            .andExpect(model().attributeExists("sort"))
            .andExpect(model().attributeExists("paging"))
            .andExpect(model().attribute("items", contains(hasSize(1))))
            .andExpect(model().attribute("sort", Matchers.is("NO")))
            .andExpect(model().attribute("paging",
                hasProperty("totalElements", Matchers.comparesEqualTo(1L))));
    }

    @Test
    void handleGetItems_shouldReturnAllItemsList_withoutFilter_withSort() throws Exception {
        mockMvc.perform(get("/main/items")
                .param("sort", "PRICE"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("text/html;charset=UTF-8"))
            .andExpect(view().name("main"))
            .andExpect(model().attributeExists("items"))
            .andExpect(model().attributeExists("sort"))
            .andExpect(model().attributeExists("paging"))
            .andExpect(model().attribute("items", contains(
                contains(
                    hasProperty("id", is(1L)),
                    hasProperty("id", is(2L))
                )
            )))
            .andExpect(model().attribute("sort", Matchers.is("PRICE")))
            .andExpect(model().attribute("paging",
                hasProperty("totalElements", Matchers.comparesEqualTo(2L))));
    }

    @Test
    void handleGetItems_shouldReturnAllItemsList_withFilter_withSort() throws Exception {
        mockMvc.perform(get("/main/items")
                .param("search", "1")
                .param("sort", "PRICE")
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType("text/html;charset=UTF-8"))
            .andExpect(view().name("main"))
            .andExpect(model().attributeExists("items"))
            .andExpect(model().attributeExists("sort"))
            .andExpect(model().attributeExists("paging"))
            .andExpect(model().attribute("items", contains(
                contains(
                    hasProperty("id", is(1L))
                )
            )))
            .andExpect(model().attribute("sort", Matchers.is("PRICE")))
            .andExpect(model().attribute("paging",
                hasProperty("totalElements", Matchers.comparesEqualTo(1L))));
    }

    @Test
    void handleChangeAmount_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/main/items/1"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void handleChangeAmount_shouldAddItem() throws Exception {
        mockMvc.perform(post("/main/items/1")
                .param("action", "PLUS"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/main/items"));
    }
}
