package ru.custom.storefrontapp.integration.repository;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserRepositoryTest extends BaseRepositoryTest {
    @Test
    public void findByUsername_shouldReturnUser() {
        StepVerifier.create(
                userRepository.findByUsername(TEST_USER_NAME)
        ).assertNext(actual -> {
            assertEquals(TEST_USER_ID, actual.getId());
            assertEquals(TEST_USER_NAME, actual.getUsername());
            assertEquals("user", actual.getPassword());
            assertEquals(true, actual.getEnabled());
        }).verifyComplete();
    }
}
