package ru.custom.intershop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private Long id;
    private List<ItemDto> items;

    public BigDecimal totalSum() {
        return items.stream()
            .map(
                itm ->
                    itm.getPrice().multiply(
                        BigDecimal.valueOf(itm.getCount())
                    ))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
