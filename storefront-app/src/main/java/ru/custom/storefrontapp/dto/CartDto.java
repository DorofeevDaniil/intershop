package ru.custom.storefrontapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDto implements Serializable {
    private List<ItemDto> items;
    private BigDecimal total;

    public boolean isEmpty() {
        return items == null || items.isEmpty();
    }
}
