package ru.custom.intershop.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;


@Data
@NoArgsConstructor
public class Cart {
    private List<Item> items;
    private BigDecimal total;

    public Boolean isEmpty() {
        return items == null || items.isEmpty();
    }
}
