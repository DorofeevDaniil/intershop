package ru.custom.storefrontapp.model;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
    private List<Item> items;
    private BigDecimal total;
}
