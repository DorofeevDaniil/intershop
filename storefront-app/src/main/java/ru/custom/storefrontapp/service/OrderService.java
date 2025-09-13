package ru.custom.storefrontapp.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.custom.storefrontapp.domain.PaymentDto;
import ru.custom.storefrontapp.dto.CartDto;
import ru.custom.storefrontapp.dto.OrderDto;
import ru.custom.storefrontapp.mapper.CartMapper;
import ru.custom.storefrontapp.model.Cart;
import ru.custom.storefrontapp.model.Item;
import ru.custom.storefrontapp.model.Order;
import ru.custom.storefrontapp.model.OrderItem;
import ru.custom.storefrontapp.repository.OrderItemRepository;
import ru.custom.storefrontapp.repository.OrderRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartService cartService;
    private final ItemService itemService;
    private final PaymentService paymentService;
    private final UserManagementService userManagementService;
    private final TransactionalOperator txOperator;

    public OrderService(
        OrderRepository orderRepository,
        OrderItemRepository orderItemRepository,
        CartService cartService,
        ItemService itemService,
        PaymentService paymentService,
        UserManagementService userManagementService,
        ReactiveTransactionManager txManager
    ) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartService = cartService;
        this.itemService = itemService;
        this.paymentService = paymentService;
        this.userManagementService = userManagementService;
        this.txOperator = TransactionalOperator.create(txManager);
    }

    @PreAuthorize("hasRole('USER')")
    public Mono<Long> createOrder(CartDto cart, String username) {
        return Mono.defer(() -> userManagementService.findUserByName(username)
                .flatMap(user -> {
                    PaymentDto paymentDto = new PaymentDto();
                    paymentDto.setAmount(cart.getTotal().doubleValue());
                    paymentDto.setUserId(user.getId());

                    return paymentService.postPayment(paymentDto)
                        .then(
                            Mono.defer(() -> {
                                Order order = new Order();
                                order.setUserId(user.getId());

                                return orderRepository.save(order)
                                    .flatMap(savedOrder ->
                                        orderItemRepository.saveAll(
                                                populateOrderItems(savedOrder.getId(), CartMapper.toCart(cart))
                                            )
                                            .then(cleanCartItems(user.getId()))
                                            .thenReturn(savedOrder.getId())
                                    );
                            })
                        );
                })
        ).as(txOperator::transactional);
    }

    @PreAuthorize("hasRole('USER')")
    public Mono<OrderDto> getOrderById(Long id) {
        return orderItemRepository.findAllByOrderId(id)
            .flatMap(orderItem ->
                itemService.getItemCardById(orderItem.getItemId())
                    .map(relatedItem -> {
                        relatedItem.setCount(orderItem.getCount());
                        return relatedItem;
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

    @PreAuthorize("hasRole('USER')")
    public Flux<OrderDto> getAllOrders(String username) {
        return userManagementService.findUserByName(username)
            .flatMapMany(user ->
                    orderRepository.findAllByUserId(user.getId())
                            .flatMap(order ->
                                    orderItemRepository.findAllByOrderId(order.getId())
                                            .flatMap(orderItem ->
                                                    itemService.getItemCardById(orderItem.getItemId())
                                                            .map(relatedItem -> {
                                                                relatedItem.setCount(orderItem.getCount());
                                                                return relatedItem;
                                                            })
                                            )
                                            .collectList()
                                            .map(items -> {
                                                OrderDto orderDto = new OrderDto();
                                                orderDto.setId(order.getId());
                                                orderDto.setItems(items);

                                                return orderDto;
                                            })
                            )
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

    private Mono<Void> cleanCartItems(Long userId) {
        return cartService.cleanCart(userId)
            .then();
    }
}
