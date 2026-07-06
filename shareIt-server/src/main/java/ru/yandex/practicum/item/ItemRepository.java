package ru.yandex.practicum.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwnerId(Long ownerId);

    @Query(
            value = """
                    SELECT *
                    FROM items i
                    WHERE i.available = true
                    AND (LOWER(i.name) LIKE LOWER(CONCAT('%', :text, '%'))
                    OR LOWER(i.description) LIKE LOWER(CONCAT('%', :text, '%')))
                    """,
            nativeQuery = true
    )
    List<Item> searchItemsByText(@Param("text") String text);


}
