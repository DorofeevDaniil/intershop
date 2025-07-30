package ru.custom.intershop.integration.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.custom.intershop.service.ItemService;

class BuyControllerTest extends BaseControllerTest {
    @Autowired
    private ItemService itemService;

    @Test
    void handleBuyItems_shouldBuyItems() {
        itemService.changeAmount(1L, "PLUS").block();

        var response = webTestClient.get()
            .uri("/cart/items")
            .exchange()
            .expectStatus().isOk()
            .returnResult(String.class);

        String sessionCookie = response.getResponseHeaders().getFirst("Set-Cookie");
        String sessionId = extractSessionId(sessionCookie);

        webTestClient.post()
            .uri("/buy")
            .cookie("SESSION", sessionId)
            .exchange()
            .expectStatus().is3xxRedirection()
            .expectHeader().valueMatches("Location", "/orders/2\\?newOrder=true");
    }

    @Test
    void handleBuyItems_shouldRedirectOnEmptyCart() {
        webTestClient.post()
            .uri("/buy")
            .exchange()
            .expectStatus().is3xxRedirection()
            .expectHeader().valueMatches("Location", "/cart/items");
    }

    private String extractSessionId(String cookie) {
        if (cookie == null) return "";
        int start = cookie.indexOf("SESSION=");
        if (start == -1) return "";
        start += "SESSION=".length();
        int end = cookie.indexOf(';', start);
        return end == -1 ? cookie.substring(start) : cookie.substring(start, end);
    }
}
