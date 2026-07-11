package ru.yandex.practicum.request;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.user.User;

import java.util.Collection;
import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequestorId(User requestor);

    Collection<ItemRequest> findAllByOrderByCreatedDesc();
}
