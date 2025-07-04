package ru.custom.intershop.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import ru.custom.intershop.model.Item;
import ru.custom.intershop.repository.ItemRepository;

import java.util.List;

@Service
public class ItemService {
    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public void save(Item item) {
        itemRepository.save(item);
    }

    public List<Item> getAll() {
        return itemRepository.findAllByOrderByIdAsc();
    }

    public Long getTotalCount() {
        return itemRepository.count();
    }

    public Item getItemById(Long id) {
        return itemRepository.getReferenceById(id);
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
            case "DELETE" -> {
                updatetingItem.setCount(0);
            }
        }
        save(updatetingItem);
    }
}
