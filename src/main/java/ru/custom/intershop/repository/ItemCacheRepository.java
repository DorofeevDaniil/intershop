package ru.custom.intershop.repository;

import reactor.core.publisher.Mono;
import ru.custom.intershop.dto.ItemDto;
import ru.custom.intershop.dto.ItemListDto;

import java.util.List;

public interface ItemCacheRepository {
    Mono<List<ItemListDto>> findAll();
    Mono<List<ItemListDto>> saveAll(List<ItemListDto> list);
    Mono<ItemDto> findById(Long id);
    Mono<Long> deleteList();
    Mono<Boolean> saveItem(Long id, ItemDto item);
}
