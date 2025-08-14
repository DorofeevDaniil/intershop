package ru.custom.storefrontapp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;
import ru.custom.storefrontapp.dto.ItemDto;
import ru.custom.storefrontapp.dto.ItemListDto;
import ru.custom.storefrontapp.mapper.ItemMapper;
import ru.custom.storefrontapp.model.Item;
import ru.custom.storefrontapp.repository.ItemCacheRepository;
import ru.custom.storefrontapp.repository.ItemRepository;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

@Slf4j
@Service
public class ItemService {
    @Value("${app.upload-dir}")
    private String relativePath;
    private ItemCacheRepository itemCacheRepository;
    private final ItemRepository itemRepository;

    public ItemService(ItemCacheRepository itemCacheRepository, ItemRepository itemRepository) {
        this.itemCacheRepository = itemCacheRepository;
        this.itemRepository = itemRepository;
    }

    public Mono<ItemDto> getItemCardById(Long id) {
        return itemCacheRepository.findById(id)
            .switchIfEmpty(Mono.defer(() -> loadItemCardFromDbAndCache(id)));
    }

    private Mono<ItemDto> loadItemCardFromDbAndCache(Long id) {
        return itemRepository.findById(id)
            .map(ItemMapper::toItemDto)
            .flatMap(dto -> itemCacheRepository.saveItem(id, dto)
                .thenReturn(dto));
    }

    public Mono<List<ItemListDto>> getItemsListCached() {
        return itemCacheRepository.findAll()
            .switchIfEmpty(Mono.defer(this::loadItemsListFromDbAndCache));
    }

    private Mono<List<ItemListDto>> loadItemsListFromDbAndCache() {
        return itemRepository.findAll()
            .map(item -> new ItemListDto(
                item.getId(),
                item.getTitle(),
                item.getDescription(),
                item.getPrice()
            ))
            .collectList()
            .flatMap(list -> itemCacheRepository.saveAll(list));
    }

    public Mono<Tuple2<List<ItemDto>, Long>> searchAndPaginate(int page, int pageSize, String sort, String searchText) {
        return getItemsListCached()
            .flatMap(items -> {
                Stream<ItemListDto> stream = items.stream()
                    .filter(i -> {
                        if (searchText == null || searchText.isBlank()) return true;
                        String q = searchText.toLowerCase();
                        return (i.getTitle() != null && i.getTitle().toLowerCase().contains(q))
                            || (i.getDescription() != null && i.getDescription().toLowerCase().contains(q));
                    });

                Comparator<ItemListDto> comparator = switch (sort.toUpperCase()) {
                    case "ALPHA" -> Comparator.comparing(i -> i.getTitle() == null ? "" : i.getTitle(), String.CASE_INSENSITIVE_ORDER);
                    case "PRICE" -> Comparator.comparing(i -> i.getPrice() == null ? BigDecimal.ZERO : i.getPrice());
                    default -> Comparator.comparing(ItemListDto::getId);
                };

                List<ItemListDto> filteredSorted = stream.sorted(comparator).toList();

                long total = filteredSorted.size();
                int from = Math.min((page - 1) * pageSize, filteredSorted.size());
                int to = Math.min(from + pageSize, filteredSorted.size());

                List<ItemListDto> pageList = filteredSorted.subList(from, to);

                return Flux.fromIterable(pageList)
                    .flatMap(dto -> getItemCardById(dto.getId()))
                    .collectList()
                    .map(list -> Tuples.of(list, total));
            });
    }

    private Mono<ItemDto> save(ItemDto item) {
        return saveToDb(ItemMapper.toItem(item))
            .flatMap(saved -> {
                ItemDto cardDto = ItemMapper.toItemDto(saved);

                return itemCacheRepository.saveItem(saved.getId(), cardDto)
                    .then(evictListCache())
                    .thenReturn(cardDto);
            });
    }

    public Mono<Item> saveToDb(Item item) {
        return itemRepository.save(item);
    }

    public Mono<Long> getTotalCount() {
        return itemRepository.count();
    }

    public Mono<ItemDto> addItem(ItemDto item, FilePart image) {
        Path uploadDir = Paths.get(relativePath).toAbsolutePath();
        Path fullPath = uploadDir.resolve(image.filename());

        return Mono.fromCallable(() -> {
                Files.createDirectories(uploadDir);
                return fullPath;
            })
            .subscribeOn(Schedulers.boundedElastic())
            .flatMap(path -> image.transferTo(path).thenReturn(item))
            .flatMap(this::save)
            .onErrorResume(e -> {
                log.error("Ошибка при сохранении файла или Item", e);
                return Mono.empty();
            });
    }

    private Mono<Boolean> evictListCache() {
        return itemCacheRepository.deleteList()
            .map(deletedCount -> deletedCount > 0);
    }
}