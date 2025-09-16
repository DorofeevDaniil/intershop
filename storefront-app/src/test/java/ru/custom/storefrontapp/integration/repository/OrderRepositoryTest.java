package ru.custom.storefrontapp.integration.repository;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderRepositoryTest extends BaseRepositoryTest {
    @Test
    public void findAllByUserId_shouldFindAllByUserId() {
        StepVerifier.create(
                orderRepository.findAllByUserId(TEST_USER_ID)
        ).assertNext(actual -> {
                assertEquals(actual.getUserId(), TEST_USER_ID);
                assertEquals(actual.getId(), 1L);
            }
        ).verifyComplete();
    }
}
