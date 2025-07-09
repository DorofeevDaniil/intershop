package ru.custom.intershop.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.custom.intershop.model.Cart;
import ru.custom.intershop.model.Item;
import ru.custom.intershop.model.Order;
import ru.custom.intershop.model.OrderItem;
import ru.custom.intershop.repository.OrderRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    private OrderRepository orderRepository;
    private ItemService itemService;

    public OrderService(OrderRepository orderRepository, ItemService itemService) {
        this.orderRepository = orderRepository;
        this.itemService = itemService;
    }

    @Transactional
    public Long createOrder(Cart cart) {
        Order order = populateOrder(cart);
        Long id = orderRepository.save(order).getId();

        cleanCartItems(cart);

        return id;
    }

    public Order getOrderById(Long id) {
        return orderRepository.getReferenceById(id);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    private Order populateOrder(Cart cart) {
        Order order = new Order();

        List<OrderItem> orderItems = new ArrayList<>();
        for (Item item : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setItem(item);
            orderItem.setCount(item.getCount());
            orderItem.setPrice(item.getPrice());

            orderItems.add(orderItem);
        }
        order.setItems(orderItems);

        return order;
    }

    private void cleanCartItems(Cart cart) {
        cart.getItems().forEach(itm -> itm.setCount(0));
        itemService.updateItems(cart.getItems());
    }
}
