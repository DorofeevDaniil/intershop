package ru.custom.storefrontapp.unit.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.custom.storefrontapp.dto.RegisterRequest;
import ru.custom.storefrontapp.model.Role;
import ru.custom.storefrontapp.model.User;
import ru.custom.storefrontapp.model.UserRole;
import ru.custom.storefrontapp.repository.RoleRepository;
import ru.custom.storefrontapp.repository.UserRepository;
import ru.custom.storefrontapp.repository.UserRoleRepository;
import ru.custom.storefrontapp.service.UserManagementService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class UserManagementServiceTest extends BaseServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private UserRoleRepository userRoleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserManagementService userManagementService;

    @Test
    public void registerUser_shouldRegisterUser() {
        doReturn(Mono.just(getRole())).when(roleRepository).findByName(TEST_ROLE_NAME);
        doReturn(Mono.just(getUser())).when(userRepository).save(any(User.class));
        doReturn(Mono.just(getUserRole())).when(userRoleRepository).save(any(UserRole.class));

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername(TEST_USER_NAME);
        registerRequest.setPassword(TEST_USER_NAME);

        StepVerifier.create(
                userManagementService.registerUser(registerRequest)
        ).verifyComplete();

        verify(roleRepository, times(1)).findByName(anyString());
        verify(userRepository, times(1)).save(any(User.class));
        verify(userRoleRepository, times(1)).save(any(UserRole.class));
    }

    @Test
    public void saveRole_shouldSaveRole() {
        doReturn(Mono.just(getRole())).when(roleRepository).save(any(Role.class));

        StepVerifier.create(
                userManagementService.saveRole(TEST_ROLE_NAME)
        ).assertNext(actual -> {
            assertEquals(TEST_ROLE_NAME, actual.getName());
            assertEquals(TEST_ROLE_ID, actual.getId());
        }).verifyComplete();

        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    public void findRoleByName_shouldReturnRole() {
        doReturn(Mono.just(getRole())).when(roleRepository).findByName(TEST_ROLE_NAME);

        StepVerifier.create(
                userManagementService.findRoleByName(TEST_ROLE_NAME)
        ).assertNext(actual -> {
            assertEquals(TEST_ROLE_NAME, actual.getName());
            assertEquals(TEST_ROLE_ID, actual.getId());
        }).verifyComplete();

        verify(roleRepository, times(1)).findByName(anyString());
    }

    @Test
    public void saveUser_shouldSaveUser() {
        doReturn(Mono.just(getUser())).when(userRepository).save(any(User.class));

        StepVerifier.create(
                userManagementService.saveUser(TEST_USER_NAME, TEST_USER_NAME)
        ).assertNext(actual -> {
            assertEquals(TEST_USER_NAME, actual.getUsername());
            assertEquals(TEST_USER_ID, actual.getId());
            assertEquals(TEST_USER_NAME, actual.getPassword());
            assertEquals(true, actual.getEnabled());
        }).verifyComplete();

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void findUserByName_shouldReturnUser() {
        doReturn(Mono.just(getUser())).when(userRepository).findByUsername(TEST_USER_NAME);

        StepVerifier.create(
                userManagementService.findUserByName(TEST_USER_NAME)
        ).assertNext(actual -> {
            assertEquals(TEST_USER_NAME, actual.getUsername());
            assertEquals(TEST_USER_ID, actual.getId());
            assertEquals(TEST_USER_NAME, actual.getPassword());
            assertEquals(true, actual.getEnabled());
        }).verifyComplete();

        verify(userRepository, times(1)).findByUsername(anyString());
    }

    @Test
    public void saveUserRole_shouldSaveUserRole() {
        doReturn(Mono.just(getUserRole())).when(userRoleRepository).save(any(UserRole.class));

        StepVerifier.create(
                userManagementService.saveUserRole(TEST_USER_ID, TEST_ROLE_ID)
        ).assertNext(actual -> {
            assertEquals(TEST_USER_ID, actual.getUserId());
            assertEquals(TEST_ROLE_ID, actual.getRoleId());
        }).verifyComplete();

        verify(userRoleRepository, times(1)).save(any(UserRole.class));
    }

    @Test
    public void findUserRoleByIds_shouldSaveUserRole() {
        doReturn(Mono.just(getUserRole())).when(userRoleRepository).findAllByUserIdAndRoleId(TEST_USER_ID, TEST_ROLE_ID);

        StepVerifier.create(
                userManagementService.findUserRoleByIds(TEST_USER_ID, TEST_ROLE_ID)
        ).assertNext(actual -> {
            assertEquals(TEST_USER_ID, actual.getUserId());
            assertEquals(TEST_ROLE_ID, actual.getRoleId());
        }).verifyComplete();

        verify(userRoleRepository, times(1)).findAllByUserIdAndRoleId(anyLong(), anyLong());
    }

    private UserRole getUserRole() {
        UserRole userRole = new UserRole();
        userRole.setUserId(TEST_USER_ID);
        userRole.setRoleId(TEST_ROLE_ID);

        return userRole;
    }

    private Role getRole() {
        Role role = new Role();
        role.setId(TEST_ROLE_ID);
        role.setName(TEST_ROLE_NAME);

        return role;
    }

    private User getUser() {
        User user = new User();
        user.setId(TEST_USER_ID);
        user.setUsername(TEST_USER_NAME);
        user.setPassword(TEST_USER_NAME);
        user.setEnabled(true);

        return user;
    }
}
