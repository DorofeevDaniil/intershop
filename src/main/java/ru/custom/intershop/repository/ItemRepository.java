package ru.custom.intershop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.custom.intershop.model.Item;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
//    Optional<ItemModel> findByEmail(String email);
    List<Item> findAllByOrderByIdAsc();
    List<Item> findAllByCountGreaterThan(Integer cnt);
}