package ru.custom.storefrontapp.integration.repository;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderItemRepositoryTest extends BaseRepositoryTest{
    @Test
    void findAllByCountGreaterThan_shouldReturnGreaterValues() {
        StepVerifier.create(
            orderItemRepository.findAllByOrderId(1L)
        ).assertNext(item ->
            assertEquals(1L, item.getOrderId())
        ).assertNext(item ->
            assertEquals(1L, item.getOrderId())
        ).verifyComplete();
    }
}
