package ru.custom.storefrontapp.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import ru.custom.storefrontapp.model.Order;

public interface OrderRepository extends R2dbcRepository<Order, Long> {
}
