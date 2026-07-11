package ru.yandex.practicum.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.exceptions.NotFoundException;
import ru.yandex.practicum.item.ItemServiceImpl;
import ru.yandex.practicum.user.User;
import ru.yandex.practicum.user.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper mapper;
    private final ItemServiceImpl itemServiceImpl;

    @Transactional
    @Override
    public ItemRequestResponseDto create(long userId, ItemRequestDto itemRequestDto) {
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if (!itemServiceImpl.searchByText(itemRequestDto.getDescription()).isEmpty()) {
            throw new ConditionsNotMetException("Эта вещь уже существует");
        }

        ItemRequest itemRequest = ItemRequest.builder()
                .requestorId(requestor)
                .description(itemRequestDto.getDescription())
                .created(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();

        itemRequestRepository.save(itemRequest);

        return mapper.mapToItemItemRequestResponseDto(itemRequest);
    }

    @Override
    public Collection<ItemRequestResponseDto> get(long userId) {
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        return itemRequestRepository.findAllByRequestorId(requestor).stream()
                .map(mapper::mapToItemItemRequestResponseDto)
                .sorted(Comparator.comparing(ItemRequestResponseDto::getCreated).reversed())
                .toList();
    }

    @Override
    public Collection<ItemRequestResponseDto> getAll() {
        return itemRequestRepository.findAllByOrderByCreatedDesc().stream()
                .map(mapper::mapToItemItemRequestResponseDto)
                .toList();
    }

    @Override
    public ItemRequestResponseDto getById(long userId, long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос на вещь не найден"));

        return mapper.mapToItemItemRequestResponseDto(request);
    }

}
