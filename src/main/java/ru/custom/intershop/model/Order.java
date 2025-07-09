package ru.custom.intershop.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;

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
