package ru.custom.intershop.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Table(name = "item")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private  String title;
    private String description;
    @Column(name = "img_path", nullable = false)
    private String imgPath;
//    @Transient
    private Integer count;
    private BigDecimal price;

    @OneToMany(mappedBy = "item")
    private List<OrderItem> cartItems;
}
