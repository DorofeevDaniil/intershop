package ru.custom.intershop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemListDto {
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
}
