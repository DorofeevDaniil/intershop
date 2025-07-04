package ru.custom.intershop.model;

import java.math.BigDecimal;
import java.util.List;

public class Cart {
    private List<Item> items;
    private BigDecimal total;

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public List<Item> getItems() {
        return items;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public Boolean isEmpty() {
        return items.isEmpty();
    }
}
