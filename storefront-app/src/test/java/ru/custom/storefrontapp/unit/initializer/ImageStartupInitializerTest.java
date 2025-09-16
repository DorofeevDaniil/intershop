package ru.custom.storefrontapp.unit.initializer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.custom.storefrontapp.initializer.ImageStartupInitializer;
import ru.custom.storefrontapp.model.Item;
import ru.custom.storefrontapp.model.Role;
import ru.custom.storefrontapp.model.User;
import ru.custom.storefrontapp.model.UserRole;
import ru.custom.storefrontapp.service.ItemService;
import ru.custom.storefrontapp.service.UserManagementService;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageStartupInitializerTest {
    @Mock
    private ItemService itemService;
    @Mock
    private UserManagementService userManagementService;
    @Spy
    @InjectMocks
    private ImageStartupInitializer initializer;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(initializer, "relativePath", "uploads/images");

        lenient().doReturn("mock-image.jpg").when(initializer).saveImage(anyString());
        lenient().doReturn("mock text").when(initializer).getFileText(anyString());
    }

    @Test
    void runItemInitialization_shouldSaveItemsWhenDatabaseIsEmpty() {
        // given
        when(itemService.getTotalCount()).thenReturn(Mono.just(0L));
        when(itemService.saveToDb(any(Item.class))).thenReturn(Mono.just(new Item()));

        // when
        StepVerifier.create(initializer.runItemInitialization())
            .verifyComplete();

        // then
        verify(itemService, atLeastOnce()).saveToDb(any(Item.class));
    }

    @Test
    void runItemInitialization_shouldDoNothingWhenDatabaseIsNotEmpty() {
        // given
        when(itemService.getTotalCount()).thenReturn(Mono.just(10L));

        // when
        StepVerifier.create(initializer.runItemInitialization())
            .verifyComplete();

        // then
        verify(itemService, never()).saveToDb(any(Item.class));
    }

    @Test
    void populateRoleModel_shouldSaveItemsWhenDatabaseIsEmpty() {
        // given
        when(userManagementService.findUserByName(anyString())).thenReturn(Mono.empty());
        when(userManagementService.saveUser(anyString(), anyString())).thenReturn(Mono.just(getUser()));
        when(userManagementService.findRoleByName(anyString())).thenReturn(Mono.empty());
        when(userManagementService.saveRole(anyString())).thenReturn(Mono.just(getRole()));
        when(userManagementService.findUserRoleByIds(anyLong(), anyLong())).thenReturn(Mono.empty());
        when(userManagementService.saveUserRole(anyLong(), anyLong())).thenReturn(Mono.just(getUserRole()));

        // when
        StepVerifier.create(initializer.populateRoleModel())
                .verifyComplete();

        // then
        verify(userManagementService, times(2)).saveUser(anyString(), anyString());
        verify(userManagementService, times(2)).saveRole(anyString());
        verify(userManagementService, times(2)).saveUserRole(anyLong(), anyLong());
    }

    @Test
    void populateRoleModel_shouldDoNothingWhenDatabaseIsNotEmpty() {
        // given
        when(userManagementService.findUserByName(anyString())).thenReturn(Mono.just(getUser()));
        when(userManagementService.findRoleByName(anyString())).thenReturn(Mono.just(getRole()));
        when(userManagementService.findUserRoleByIds(anyLong(), anyLong())).thenReturn(Mono.just(getUserRole()));

        // when
        StepVerifier.create(initializer.populateRoleModel())
                .verifyComplete();

        // then
        verify(userManagementService, times(0)).saveUser(anyString(), anyString());
        verify(userManagementService, times(0)).saveRole(anyString());
        verify(userManagementService, times(0)).saveUserRole(anyLong(), anyLong());
    }

    private UserRole getUserRole() {
        UserRole userRole = new UserRole();
        userRole.setUserId(1L);
        userRole.setRoleId(1L);

        return userRole;
    }

    private Role getRole() {
        Role role = new Role();
        role.setId(1L);

        return role;
    }

    private User getUser() {
        User user = new User();
        user.setId(1L);

        return user;
    }
}
