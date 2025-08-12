package ru.custom.storefrontapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto implements Serializable {
    private Long id;
    private String title;
    private String description;
    private String imgPath;
    private Integer count;
    private BigDecimal price;
}
