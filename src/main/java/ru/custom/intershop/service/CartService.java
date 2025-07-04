package ru.custom.intershop.service;

import org.springframework.stereotype.Service;
import ru.custom.intershop.model.Cart;
import ru.custom.intershop.model.Item;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartService {
    private ItemService itemService;

    public CartService(ItemService itemService) {
        this.itemService = itemService;
    }

    public Cart getCart() {
        List<Item> items = itemService.getAllInCart();

        Cart cart = new Cart();
        cart.setItems(items);

        BigDecimal summ = BigDecimal.valueOf(0);
        for (Item item : items) {
            summ = summ.add(item.getPrice().multiply(BigDecimal.valueOf(item.getCount())));
        }

        cart.setTotal(summ);

        return cart;
    }

    public void changeItemAmount(Long id, String action) {
        itemService.changeAmount(id, action);
    }

}
