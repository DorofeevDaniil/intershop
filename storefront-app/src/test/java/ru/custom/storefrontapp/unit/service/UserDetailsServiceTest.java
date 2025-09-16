package ru.custom.storefrontapp.unit.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.custom.storefrontapp.model.Role;
import ru.custom.storefrontapp.model.User;
import ru.custom.storefrontapp.model.UserRole;
import ru.custom.storefrontapp.repository.RoleRepository;
import ru.custom.storefrontapp.repository.UserRepository;
import ru.custom.storefrontapp.repository.UserRoleRepository;
import ru.custom.storefrontapp.service.UserDetailsService;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.list;
import static org.mockito.Mockito.doReturn;


class UserDetailsServiceTest extends BaseServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @InjectMocks
    private UserDetailsService userDetailsService;

    @Test
    void findByUsername_shouldReturnUserDetailsWithRoles() {
        User user = new User();
        user.setId(TEST_USER_ID);
        user.setUsername(TEST_USER_NAME);
        user.setPassword("secret");
        user.setEnabled(true);

        UserRole userRole = new UserRole();
        userRole.setUserId(TEST_USER_ID);
        userRole.setRoleId(10L);

        Role role = new Role();
        role.setId(10L);
        role.setName("ADMIN");

        doReturn(Mono.just(user)).when(userRepository).findByUsername(TEST_USER_NAME);
        doReturn(Flux.fromIterable(List.of(userRole))).when(userRoleRepository).findByUserId(TEST_USER_ID);
        doReturn(Mono.just(role)).when(roleRepository).findById(10L);

        StepVerifier.create(userDetailsService.findByUsername(TEST_USER_NAME))
                .assertNext(userDetails -> {
                    assertThat(userDetails.getUsername()).isEqualTo(TEST_USER_NAME);
                    assertThat(userDetails.getPassword()).isEqualTo("secret");
                    assertThat(userDetails.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .toList()
                    ).asInstanceOf(list(String.class))
                            .containsExactly("ROLE_ADMIN");
                    assertThat(userDetails.isEnabled()).isTrue();
                })
                .verifyComplete();
    }

    @Test
    void findByUsername_shouldReturnEmptyWhenUserNotFound() {
        doReturn(Mono.empty()).when(userRepository).findByUsername("unknown");

        StepVerifier.create(userDetailsService.findByUsername("unknown"))
                .verifyComplete();
    }
}