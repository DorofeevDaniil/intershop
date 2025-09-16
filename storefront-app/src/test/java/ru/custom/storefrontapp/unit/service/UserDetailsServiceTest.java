package ru.custom.storefrontapp.unit.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

@ExtendWith(MockitoExtension.class)
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
        user.setId(1L);
        user.setUsername("testUser");
        user.setPassword("secret");
        user.setEnabled(true);

        UserRole userRole = new UserRole();
        userRole.setUserId(1L);
        userRole.setRoleId(10L);

        Role role = new Role();
        role.setId(10L);
        role.setName("ADMIN");

        doReturn(Mono.just(user)).when(userRepository).findByUsername("testUser");
        doReturn(Flux.fromIterable(List.of(userRole))).when(userRoleRepository).findByUserId(1L);
        doReturn(Mono.just(role)).when(roleRepository).findById(10L);

        StepVerifier.create(userDetailsService.findByUsername("testUser"))
                .assertNext(userDetails -> {
                    assertThat(userDetails.getUsername()).isEqualTo("testUser");
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