package ru.custom.intershop.model;

import org.springframework.data.annotation.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Table(name = "item")
public class Item {
    @Id
    private Long id;
    private  String title;
    private String description;
    @Column("img_path")
    private String imgPath;
    private Integer count;
    private BigDecimal price;
}
