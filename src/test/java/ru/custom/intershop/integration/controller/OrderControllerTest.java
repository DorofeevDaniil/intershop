package ru.custom.intershop.integration.controller;

import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

class OrderControllerTest extends BaseControllerTest {
    @Test
    void handleShowOrders_shouldReturnOrders() throws Exception {
        mockMvc.perform(get("/orders"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("text/html;charset=UTF-8"))
            .andExpect(view().name("orders"))
            .andExpect(model().attributeExists("orders"))
            .andExpect(model().attribute("orders", hasSize(1)))
            .andExpect(model().attribute("orders",
                contains(
                    hasProperty("items", hasSize(2))
                )
            ));
    }

    @Test
    void handleGetOrder_shouldReturnOrder() throws Exception {
        mockMvc.perform(get("/orders/1"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("text/html;charset=UTF-8"))
            .andExpect(view().name("order"))
            .andExpect(model().attributeExists("order"))
            .andExpect(model().attribute("order",
                hasProperty("items",
                    hasSize(2)
                )
            ));
    }
}
