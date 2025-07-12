package ru.custom.intershop.integration.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.custom.intershop.mapper.CartMapper;
import ru.custom.intershop.model.Cart;
import ru.custom.intershop.service.CartService;
import ru.custom.intershop.service.ItemService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class BuyControllerTest extends BaseControllerTest {
    @Autowired
    private ItemService itemService;

    @Autowired
    private CartService cartService;

    @Test
    void handleBuyItems_shouldBuyItems() throws Exception {
        itemService.changeAmount(1L, "PLUS");

        Cart cart = cartService.getCart();

        mockMvc.perform(post("/buy")
                .sessionAttr("cart", CartMapper.toCartDto(cart)))
            .andExpect(status().is3xxRedirection())
            .andExpect(request().sessionAttributeDoesNotExist("cart"))
            .andExpect(redirectedUrlPattern("/orders/*?newOrder=true"));
    }
}
