package ru.custom.storefrontapp.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import ru.custom.storefrontapp.model.Role;

@Repository
public interface RoleRepository extends R2dbcRepository<Role, Long> {
    Mono<Role> findById(Long id);
    Mono<Role> findByName(String user);
}
