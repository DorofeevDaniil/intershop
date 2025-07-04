package ru.custom.intershop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.custom.intershop.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
