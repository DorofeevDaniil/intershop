package ru.custom.storefrontapp.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.custom.storefrontapp.model.Item;

@Repository
public interface ItemRepository extends R2dbcRepository<Item, Long> {
    Flux<Item> findAllByOrderByIdAsc(Pageable pageRequest);
    Flux<Item> findAllByOrderByTitleAsc(Pageable pageable);
    Flux<Item> findAllByOrderByPriceAsc(Pageable pageable);
    Flux<Item> findAllByCountGreaterThan(Integer cnt);
    Flux<Item> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description, Pageable pageable);
    Mono<Long> countByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description);
}