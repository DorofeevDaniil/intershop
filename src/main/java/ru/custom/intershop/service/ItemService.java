package ru.custom.intershop.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.custom.intershop.model.Item;
import ru.custom.intershop.repository.ItemRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@Service
public class ItemService {
    @Value("${app.upload-dir}")
    private String relativePath;
    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public Mono<Item> save(Item item) {
        return itemRepository.save(item);
    }

    public Flux<Item> getPage(Integer page, Integer pageSize, String sort) {
        return switch (sort.toUpperCase()) {
            case "ALPHA" -> itemRepository.findAllByOrderByTitleAsc(PageRequest.of(page - 1, pageSize));
            case "PRICE" -> itemRepository.findAllByOrderByPriceAsc(PageRequest.of(page - 1, pageSize));
            default -> itemRepository.findAllByOrderByIdAsc(PageRequest.of(page - 1, pageSize));
        };
    }

    public Flux<Item> findBySearchParams(Integer page, Integer pageSize, String sort, String searchText) {
        return switch (sort) {
            case "ALPHA" -> itemRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                searchText.toUpperCase(),
                searchText.toUpperCase(),
                PageRequest.of(page - 1, pageSize, Sort.by("title").ascending())
            );

            case "PRICE" -> itemRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                searchText.toUpperCase(),
                searchText.toUpperCase(),
                PageRequest.of(page - 1, pageSize, Sort.by("price").ascending())
            );

            default -> itemRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                searchText.toUpperCase(),
                searchText.toUpperCase(),
                PageRequest.of(page - 1, pageSize)
            );
        };
    }

    public Mono<Long> getTotalCount() {
        return itemRepository.count();
    }

    public Mono<Long> getTotalSearchedElements(String text) {
        return itemRepository.countByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(text.toUpperCase(), text.toUpperCase());
    }

    public Mono<Item> getItemById(Long id) {
        return itemRepository.findById(id);
    }

    public Flux<Item> getAllInCart() {
        return itemRepository.findAllByCountGreaterThan(0);
    }

    public Mono<Item> changeAmount(Long id, String action) {
        return getItemById(id)
            .flatMap(item -> {
                int count = item.getCount() != null ? item.getCount() : 0;

                switch (action.toUpperCase()) {
                    case "PLUS" -> item.setCount(count + 1);
                    case "MINUS" -> item.setCount(Math.max(0, count - 1));
                    case "DELETE" -> item.setCount(0);
                    default -> {/*empty while have no default methods*/}
                }

                return save(item);
            });
    }

    public Mono<Item> addItem(Item item, FilePart image) {
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

    public Flux<Item> updateItems(List<Item> items) {
        return itemRepository.saveAll(items);
    }
}
