package ru.custom.storefrontapp.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@Table(name = "orders")
public class Order {
    @Id
    private Long id;
}
