package ru.custom.intershop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.custom.intershop.model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
