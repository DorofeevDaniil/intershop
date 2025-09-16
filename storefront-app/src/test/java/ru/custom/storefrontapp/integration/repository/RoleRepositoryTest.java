package ru.custom.storefrontapp.integration.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class RoleRepositoryTest extends BaseRepositoryTest {
    @Test
    public void findById_shouldReturnRole() {
        StepVerifier.create(
                roleRepository.findById(TEST_ROLE_ID)
        ).assertNext(actual -> {
            assertEquals(TEST_ROLE_ID, actual.getId());
            assertEquals(TEST_ROLE_NAME, actual.getName());
        }).verifyComplete();
    }

    @Test
    public void findByName_shouldReturnRole() {
        StepVerifier.create(
                roleRepository.findByName(TEST_ROLE_NAME)
        ).assertNext(actual -> {
            assertEquals(TEST_ROLE_ID, actual.getId());
            assertEquals(TEST_ROLE_NAME, actual.getName());
        }).verifyComplete();
    }
}
