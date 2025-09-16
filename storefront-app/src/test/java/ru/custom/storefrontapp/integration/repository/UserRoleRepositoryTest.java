package ru.custom.storefrontapp.integration.repository;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserRoleRepositoryTest extends BaseRepositoryTest {
    @Test
    public void findAllByUserIdAndRoleId_shouldReturnUserRole() {
        StepVerifier.create(
                userRoleRepository.findAllByUserIdAndRoleId(TEST_USER_ID, TEST_ROLE_ID)
        ).assertNext(actual -> {
            assertEquals(TEST_ROLE_ID, actual.getRoleId());
            assertEquals(TEST_USER_ID, actual.getUserId());
        }).verifyComplete();
    }

    @Test
    public void findByUserId_shouldReturnUserRole() {
        StepVerifier.create(
                userRoleRepository.findByUserId(TEST_USER_ID)
        ).assertNext(actual -> {
            assertEquals(TEST_ROLE_ID, actual.getRoleId());
            assertEquals(TEST_USER_ID, actual.getUserId());
        }).verifyComplete();
    }
}
