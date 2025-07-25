package ru.custom.intershop.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import ru.custom.intershop.model.Order;

public interface OrderRepository extends R2dbcRepository<Order, Long> {
}
