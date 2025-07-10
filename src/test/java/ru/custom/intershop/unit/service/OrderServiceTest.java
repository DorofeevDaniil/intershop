package ru.custom.intershop.unit.service;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockReset;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.custom.intershop.model.Cart;
import ru.custom.intershop.model.Item;
import ru.custom.intershop.model.Order;
import ru.custom.intershop.model.OrderItem;
import ru.custom.intershop.repository.OrderRepository;
import ru.custom.intershop.service.ItemService;
import ru.custom.intershop.service.OrderService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = OrderService.class)
class OrderServiceTest {
    @MockitoBean(reset = MockReset.BEFORE)
    private OrderRepository orderRepository;

    @MockitoBean(reset = MockReset.BEFORE)
    private ItemService itemService;

    @Autowired
    private OrderService orderService;

    private static final Long TEST_ID = 1L;
    private static final String TEST_TITLE = "test title";
    private static final String TEST_DESCRIPTION = "test description";
    private static final BigDecimal TEST_PRICE = BigDecimal.valueOf(100);
    private static final String TEST_IMG_PATH = "/test/path.png";

    @Test
    void getOrderById_shouldReturnOrder() {
        Order expectedOrder = populateOrder();

        doReturn(Optional.of(expectedOrder)).when(orderRepository).findById(anyLong());

        Order actualOrder = orderService.getOrderById(TEST_ID);

        verify(orderRepository, times(1)).findById(TEST_ID);
        assertEquals(expectedOrder, actualOrder);
    }

    @Test
    void getAllOrders_shouldReturnOrdersList() {
        Order expectedOrder = populateOrder();

        doReturn(List.of(expectedOrder)).when(orderRepository).findAll();

        List<Order> orders = orderService.getAllOrders();

        verify(orderRepository, times(1)).findAll();
        assertEquals(1, orders.size());
        assertTrue(orders.contains(expectedOrder));
    }

    @Test
    void createOrder_shouldCreateOrderAndReturnId() {
        Order expectedOrder = populateOrder();
        Cart cart = new Cart(List.of(expectedOrder.getItems().get(0).getItem()), TEST_PRICE);

        doReturn(expectedOrder).when(orderRepository).save(any(Order.class));
        doNothing().when(itemService).updateItems(anyList());

        Long actualId = orderService.createOrder(cart);

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository, times(1)).save(captor.capture());
        Order actualSavedOrder = captor.getValue();

        assertNull(actualSavedOrder.getId());

        verify(itemService, times(1)).updateItems(cart.getItems());
        assertEquals(expectedOrder.getId(), actualId);
    }

    private Item populateItem() {
        Item item = new Item();

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
        orderItem.setOrder(new Order());
        orderItem.setId(TEST_ID);
        orderItem.setItem(populateItem());
        orderItem.setCount(1);
        orderItem.setPrice(TEST_PRICE);

        return orderItem;
    }

    private Order populateOrder() {
        Order order = new Order();
        order.setId(TEST_ID);
        order.setItems(List.of(populateOrderItem()));

        return order;
    }


}
