package ru.custom.storefrontapp.unit.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.ReactiveTransaction;
import org.springframework.transaction.ReactiveTransactionManager;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.custom.storefrontapp.domain.PaymentDto;
import ru.custom.storefrontapp.dto.CartDto;
import ru.custom.storefrontapp.dto.ItemDto;
import ru.custom.storefrontapp.dto.OrderDto;
import ru.custom.storefrontapp.model.Order;
import ru.custom.storefrontapp.model.OrderItem;
import ru.custom.storefrontapp.repository.OrderItemRepository;
import ru.custom.storefrontapp.repository.OrderRepository;
import ru.custom.storefrontapp.service.CartService;
import ru.custom.storefrontapp.service.ItemService;
import ru.custom.storefrontapp.service.OrderService;
import ru.custom.storefrontapp.service.PaymentService;

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
    private CartService cartService;

    @Mock
    private ItemService itemService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private ReactiveTransactionManager transactionManager;

    @InjectMocks
    private OrderService orderService;

    @Test
    void getOrderById_shouldReturnOrder() {
        OrderDto expectedOrder = populateOrderDto();

        doReturn(Flux.fromIterable(List.of(populateOrderItem()))).when(orderItemRepository).findAllByOrderId(anyLong());
        doReturn(Mono.just(populateItemDto())).when(itemService).getItemCardById(anyLong());

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
        doReturn(Mono.just(populateItemDto())).when(itemService).getItemCardById(anyLong());
        doReturn(Flux.fromIterable(List.of(expectedOrder))).when(orderRepository).findAll();

        StepVerifier.create(
            orderService.getAllOrders()
        ).assertNext(actualOrderDto ->
            assertEquals(expectedOrderDto, actualOrderDto)
        ).verifyComplete();

        verify(orderRepository, times(1)).findAll();
        verify(orderItemRepository, times(1)).findAllByOrderId(TEST_ID);
        verify(itemService, times(1)).getItemCardById(TEST_ID);
    }

    @Test
    void createOrder_shouldCreateOrderAndReturnId() {
        ReactiveTransaction mockTx = mock(ReactiveTransaction.class);

        when(transactionManager.getReactiveTransaction(any()))
            .thenReturn(Mono.just(mockTx));
        when(transactionManager.commit(any())).thenReturn(Mono.empty());

        Order expectedOrder = populateOrder();
        ItemDto expectedItem = populateItemDto();
        OrderItem expectedOrderItem = populateOrderItem();
        CartDto cart = new CartDto(List.of(expectedItem), TEST_PRICE);
        PaymentDto expectedPaymentDto = new PaymentDto();
        expectedPaymentDto.setAmount(cart.getTotal().doubleValue());

        doReturn(Mono.just(expectedOrder)).when(orderRepository).save(any(Order.class));
        doReturn(Mono.empty()).when(paymentService).postPayment(any(PaymentDto.class));
        doReturn(Mono.empty()).when(cartService).cleanCart();
        doReturn(Flux.fromIterable(List.of(expectedOrderItem))).when(orderItemRepository).saveAll(anyList());

        StepVerifier.create(
            orderService.createOrder(cart)
        ).assertNext(actualId ->
            assertEquals(expectedOrder.getId(), actualId)
        ).verifyComplete();

        verify(orderRepository, times(1)).save(any(Order.class));
        verify(paymentService, times(1)).postPayment(expectedPaymentDto);
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
