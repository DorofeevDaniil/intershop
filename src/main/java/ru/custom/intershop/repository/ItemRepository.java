package ru.custom.intershop.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.custom.intershop.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOrderByIdAsc(Pageable pageRequest);
    List<Item> findAllByCountGreaterThan(Integer cnt);
    @Query("SELECT i FROM Item i WHERE UPPER(i.title) LIKE %:text% OR UPPER(i.description) LIKE %:text%")
    List<Item> searchByText(@Param("text") String text, Pageable pageable);
    @Query("SELECT count(*) FROM Item i WHERE UPPER(i.title) LIKE %:text% OR UPPER(i.description) LIKE %:text%")
    Long getFilteredCount(@Param("text") String text);
}