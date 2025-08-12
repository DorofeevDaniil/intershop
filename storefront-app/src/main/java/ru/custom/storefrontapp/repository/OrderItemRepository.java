package ru.custom.storefrontapp.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import ru.custom.storefrontapp.model.OrderItem;

public interface OrderItemRepository extends R2dbcRepository<OrderItem, Long> {
    Flux<OrderItem> findAllByOrderId(Long orderId);
}
