package ru.yandex.practicum.request;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequestorId(Long requestorId);

    Collection<ItemRequest> findAllByOrderByCreatedDesc();
}
