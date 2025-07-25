package ru.custom.intershop.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.custom.intershop.dto.OrderDto;
import ru.custom.intershop.mapper.ItemMapper;
import ru.custom.intershop.model.Cart;
import ru.custom.intershop.model.Item;
import ru.custom.intershop.model.Order;
import ru.custom.intershop.model.OrderItem;
import ru.custom.intershop.repository.OrderItemRepository;
import ru.custom.intershop.repository.OrderRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    private OrderRepository orderRepository;
    private OrderItemRepository orderItemRepository;
    private ItemService itemService;
    private final TransactionalOperator txOperator;

    public OrderService(
        OrderRepository orderRepository,
        OrderItemRepository orderItemRepository,
        ItemService itemService,
        ReactiveTransactionManager txManager
    ) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.itemService = itemService;
        this.txOperator = TransactionalOperator.create(txManager);
    }

    public Mono<Long> createOrder(Cart cart) {
        return orderRepository.save(new Order())
            .flatMap(savedOrder ->
                orderItemRepository.saveAll(
                    populateOrderItems(
                        savedOrder.getId(), cart))
                    .then(cleanCartItems(cart))
                    .thenReturn(savedOrder.getId()))
            .as(txOperator::transactional);
    }

    public Mono<OrderDto> getOrderById(Long id) {
        return orderItemRepository.findAllByOrderId(id)
            .flatMap(orderItem ->
                itemService.getItemById(orderItem.getItemId())
                    .map(relatedItem -> {
                        relatedItem.setCount(orderItem.getCount());
                        return ItemMapper.toItemDto(relatedItem);
                    })
            )
            .collectList()
            .map(items -> {
                OrderDto orderDto = new OrderDto();
                orderDto.setId(id);
                orderDto.setItems(items);

                return orderDto;
            });
    }

    public Flux<OrderDto> getAllOrders() {
        return orderRepository.findAll()
            .flatMap(order ->
                orderItemRepository.findAllByOrderId(order.getId())
                    .flatMap(orderItem ->
                        itemService.getItemById(orderItem.getItemId())
                            .map(relatedItem -> {
                                relatedItem.setCount(orderItem.getCount());
                                return ItemMapper.toItemDto(relatedItem);
                            })
                    )
                    .collectList()
                    .map(items -> {
                        OrderDto orderDto = new OrderDto();
                        orderDto.setId(order.getId());
                        orderDto.setItems(items);

                        return orderDto;
                    })
            );
    }

    private List<OrderItem> populateOrderItems(Long orderId, Cart cart) {
        List<OrderItem> orderItems = new ArrayList<>();
        for (Item item : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(orderId);
            orderItem.setItemId(item.getId());
            orderItem.setCount(item.getCount());
            orderItem.setPrice(item.getPrice());

            orderItems.add(orderItem);
        }

        return orderItems;
    }

    private Mono<Void> cleanCartItems(Cart cart) {
        return Flux.fromIterable(cart.getItems())
            .map(item -> {
                item.setCount(0);
                return item;
            })
            .collectList()
            .flatMap(items -> itemService.updateItems(items).then());
    }
}
