package ru.custom.storefrontapp.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.custom.storefrontapp.dto.RegisterRequest;
import ru.custom.storefrontapp.model.Role;
import ru.custom.storefrontapp.model.User;
import ru.custom.storefrontapp.model.UserRole;
import ru.custom.storefrontapp.repository.RoleRepository;
import ru.custom.storefrontapp.repository.UserRepository;
import ru.custom.storefrontapp.repository.UserRoleRepository;

@Service
public class UserManagementService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private UserRoleRepository userRoleRepository;
    private PasswordEncoder passwordEncoder;

    public UserManagementService(
        UserRepository userRepository,
        RoleRepository roleRepository,
        UserRoleRepository userRoleRepository,
        PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Mono<Void> registerUser(RegisterRequest registerRequest) {
        return saveUser(registerRequest.getUsername(), registerRequest.getPassword())
            .flatMap(savedUser ->
                findRoleByName("USER")
                    .flatMap(role ->
                            saveUserRole(savedUser.getId(), role.getId())
                                    .thenReturn(savedUser))
            )
            .then();
    }

    public Mono<Role> saveRole(String roleName) {
        Role userRole = new Role();
        userRole.setName(roleName);

        return roleRepository.save(userRole);
    }

    public Mono<Role> findRoleByName(String roleName) {
        return roleRepository.findByName(roleName);
    }

    public Mono<User> saveUser(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEnabled(true);

        return userRepository.save(user);
    }

    public Mono<User> findUserByName(String username) {
        return userRepository.findByUsername(username);
    }

    public Mono<UserRole> saveUserRole(Long userId, Long roleId) {
        UserRole userRole = new UserRole();
        userRole.setRoleId(roleId);
        userRole.setUserId(userId);

        return userRoleRepository.save(userRole);
    }

    public Mono<UserRole> findUserRoleByIds(Long userId, Long roleId) {
        return userRoleRepository.findAllByUserIdAndRoleId(userId, roleId);
    }
}
