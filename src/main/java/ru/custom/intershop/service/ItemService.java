package ru.custom.intershop.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.custom.intershop.model.Item;
import ru.custom.intershop.repository.ItemRepository;

import java.io.IOException;
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

    public void save(Item item) {
        itemRepository.save(item);
    }

    public List<Item> getPage(Integer page, Integer pageSize, String sort) {
        switch (sort) {
            case "ALPHA" -> {
                return itemRepository.findAll(
                    PageRequest.of(
                        page - 1, pageSize, Sort.by("title").ascending()
                    )
                ).stream().toList();
            }
            case "PRICE" -> {
                return itemRepository.findAll(
                    PageRequest.of(
                        page - 1, pageSize, Sort.by("price").ascending()
                    )
                ).stream().toList();
            }
            default -> {
                return itemRepository.findAllByOrderByIdAsc(PageRequest.of(page - 1, pageSize));
            }
        }
    }

    public List<Item> findBySearchParams(Integer page, Integer pageSize, String sort, String searchText) {
        switch (sort) {
            case "ALPHA" -> {
               return  itemRepository.searchByText(
                    searchText.toUpperCase(),
                    PageRequest.of(page - 1, pageSize, Sort.by("title").ascending())
                );
            }
            case "PRICE" -> {
                return itemRepository.searchByText(
                    searchText.toUpperCase(),
                    PageRequest.of(page - 1, pageSize, Sort.by("price").ascending())
                );
            }
            default -> {
                return itemRepository.searchByText(searchText.toUpperCase(), PageRequest.of(page - 1, pageSize));
            }
        }
    }

    public Long getTotalCount() {
        return itemRepository.count();
    }

    public Long getTotalSearchedElements(String text) {
        return itemRepository.getFilteredCount(text.toUpperCase());
    }

    public Item getItemById(Long id) {
        return itemRepository.findById(id).orElseGet(Item::new);
    }

    public List<Item> getAllInCart() {
        return itemRepository.findAllByCountGreaterThan(0);
    }

    public void changeAmount(Long id, String action) {
        Item updatetingItem = getItemById(id);
        Integer currCount = updatetingItem.getCount();

        switch (action.toUpperCase()) {
            case "PLUS" -> updatetingItem.setCount(currCount + 1);
            case "MINUS" -> {
                if (currCount > 1) {
                    updatetingItem.setCount(currCount - 1);
                } else {
                    updatetingItem.setCount(0);
                }
            }
            case "DELETE" -> updatetingItem.setCount(0);

        }
        save(updatetingItem);
    }

    public void addItem(Item item, MultipartFile image) {
        Path uploadDir = Paths.get(relativePath).toAbsolutePath();

        try {
            Files.createDirectories(uploadDir);

            Path fullPath = uploadDir.resolve(image.getOriginalFilename());
            image.transferTo(fullPath.toFile());
        } catch (IOException e) {
            log.error("Failed to load image file", e.getMessage());
        }

        save(item);
    }

    public void updateItems(List<Item> items) {
        itemRepository.saveAll(items);
    }
}
