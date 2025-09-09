package ru.custom.storefrontapp.service;

import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.custom.storefrontapp.model.Role;
import ru.custom.storefrontapp.repository.RoleRepository;
import ru.custom.storefrontapp.repository.UserRepository;
import ru.custom.storefrontapp.repository.UserRoleRepository;

@Service
@Primary
public class UserDetailsService implements ReactiveUserDetailsService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private UserRoleRepository userRoleRepository;

    public UserDetailsService(
        UserRepository userRepository,
        RoleRepository roleRepository,
        UserRoleRepository userRoleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByUsername(username)
            .flatMap(user ->
                userRoleRepository.findByUserId(user.getId())
                    .flatMap(userRole ->
                        roleRepository.findById(userRole.getRoleId())
                    )
                    .map(Role::getName)
                    .collectList()
                    .map(roles -> User
                        .withUsername(user.getUsername())
                        .password(user.getPassword())
                        .roles(roles.toArray(new String[0]))
                        .disabled(!user.getEnabled())
                        .build()
                    )
            );
    }
}
