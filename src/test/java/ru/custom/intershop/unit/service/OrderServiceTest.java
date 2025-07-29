package ru.custom.intershop.unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.ReactiveTransaction;
import org.springframework.transaction.ReactiveTransactionManager;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.custom.intershop.dto.ItemDto;
import ru.custom.intershop.dto.OrderDto;
import ru.custom.intershop.model.Cart;
import ru.custom.intershop.model.Item;
import ru.custom.intershop.model.Order;
import ru.custom.intershop.model.OrderItem;
import ru.custom.intershop.repository.OrderItemRepository;
import ru.custom.intershop.repository.OrderRepository;
import ru.custom.intershop.service.ItemService;
import ru.custom.intershop.service.OrderService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest extends BaseServiceTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ItemService itemService;

    @Mock
    private ReactiveTransactionManager transactionManager;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(
            orderRepository,
            orderItemRepository,
            itemService,
            transactionManager
        );
    }

    @Test
    void getOrderById_shouldReturnOrder() {
        OrderDto expectedOrder = populateOrderDto();

        doReturn(Flux.fromIterable(List.of(populateOrderItem()))).when(orderItemRepository).findAllByOrderId(anyLong());
        doReturn(Mono.just(populateItem())).when(itemService).getItemById(anyLong());

        StepVerifier.create(
            orderService.getOrderById(TEST_ID)
        ).assertNext(actualOrder ->
            assertEquals(expectedOrder, actualOrder)
        ).verifyComplete();

        verify(orderItemRepository, times(1)).findAllByOrderId(TEST_ID);
    }

    @Test
    void getAllOrders_shouldReturnOrdersList() {
        OrderDto expectedOrderDto = populateOrderDto();
        OrderItem expectedOrderItem = populateOrderItem();
        Order expectedOrder = populateOrder();

        doReturn(Flux.fromIterable(List.of(expectedOrderItem))).when(orderItemRepository).findAllByOrderId(anyLong());
        doReturn(Mono.just(populateItem())).when(itemService).getItemById(anyLong());
        doReturn(Flux.fromIterable(List.of(expectedOrder))).when(orderRepository).findAll();

        StepVerifier.create(
            orderService.getAllOrders()
        ).assertNext(actualOrderDto ->
            assertEquals(expectedOrderDto, actualOrderDto)
        ).verifyComplete();

        verify(orderRepository, times(1)).findAll();
        verify(orderItemRepository, times(1)).findAllByOrderId(TEST_ID);
        verify(itemService, times(1)).getItemById(TEST_ID);
    }

    @Test
    void createOrder_shouldCreateOrderAndReturnId() {
        ReactiveTransaction mockTx = mock(ReactiveTransaction.class);

        when(transactionManager.getReactiveTransaction(any()))
            .thenReturn(Mono.just(mockTx));
        when(transactionManager.commit(any())).thenReturn(Mono.empty());

        Order expectedOrder = populateOrder();
        Item expectedItem = populateItem();
        OrderItem expectedOrderItem = populateOrderItem();
        Cart cart = new Cart(List.of(expectedItem), TEST_PRICE);

        doReturn(Mono.just(expectedOrder)).when(orderRepository).save(any(Order.class));
        doReturn(Flux.fromIterable(List.of(expectedItem))).when(itemService).updateItems(anyList());
        doReturn(Flux.fromIterable(List.of(expectedOrderItem))).when(orderItemRepository).saveAll(anyList());

        StepVerifier.create(
            orderService.createOrder(cart)
        ).assertNext(actualId ->
            assertEquals(expectedOrder.getId(), actualId)
        ).verifyComplete();

        verify(orderRepository, times(1)).save(any(Order.class));
        verify(itemService, times(1)).updateItems(cart.getItems());
    }

    private OrderDto populateOrderDto() {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(TEST_ID);
        orderDto.setItems(List.of(populateItemDto()));

        return orderDto;
    }

    private ItemDto populateItemDto() {
        ItemDto item = new ItemDto();

        item.setId(TEST_ID);
        item.setTitle(TEST_TITLE);
        item.setDescription(TEST_DESCRIPTION);
        item.setCount(1);
        item.setPrice(TEST_PRICE);
        item.setImgPath(TEST_IMG_PATH);

        return item;
    }

    private Item populateItem() {
        Item item = populateItemNoId();
        item.setId(TEST_ID);

        return item;
    }

    private OrderItem populateOrderItem() {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(TEST_ID);
        orderItem.setId(TEST_ID);
        orderItem.setItemId(TEST_ID);
        orderItem.setCount(1);
        orderItem.setPrice(TEST_PRICE);

        return orderItem;
    }

    private Order populateOrder() {
        Order order = new Order();
        order.setId(TEST_ID);

        return order;
    }


}
