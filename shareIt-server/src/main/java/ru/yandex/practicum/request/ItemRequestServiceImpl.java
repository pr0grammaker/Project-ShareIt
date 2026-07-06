package ru.yandex.practicum.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.exceptions.NotFoundException;
import ru.yandex.practicum.item.ItemServiceImpl;
import ru.yandex.practicum.user.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper mapper;
    private final ItemServiceImpl itemServiceImpl;


    @Override
    public ItemRequestResponseDto create(long userId, ItemRequestDto itemRequestDto) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!itemServiceImpl.searchByText(itemRequestDto.getDescription()).isEmpty()) {
            throw new ConditionsNotMetException("This item already exist");
        }

        ItemRequest itemRequest = ItemRequest.builder()
                .requestorId(userId)
                .description(itemRequestDto.getDescription())
                .created(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();

        itemRequestRepository.save(itemRequest);

        return mapper.mapToItemItemRequestResponseDto(itemRequest);
    }

    @Override
    public Collection<ItemRequestResponseDto> get(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return itemRequestRepository.findAllByRequestorId(userId).stream()
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
    public ItemRequestResponseDto getById(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        ItemRequest request = itemRequestRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("ItemRequest not found"));

        return mapper.mapToItemItemRequestResponseDto(request);
    }

}
