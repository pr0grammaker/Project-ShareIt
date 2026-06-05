package ru.yandex.practicum.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.expection.NotFoundException;
import ru.yandex.practicum.user.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;

    @Override
    public Item create(long userId, ItemDto itemDto) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("User not found");
        }

        Item item = itemMapper.mapToItem(itemDto);
        item.setOwnerId(userId);

        return itemRepository.save(item);
    }

    @Override
    public Item update(long userId, long itemId, ItemDto itemDto) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        if (!Objects.equals(item.getOwnerId(), userId)) {
            throw new NotFoundException("User is not owner of item");
        }

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        return itemRepository.update(item);
    }

    @Override
    public Item getItemById(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));
    }

    @Override
    public Collection<Item> getItemsByOwner(long userId) {
        return itemRepository.findItemsByOwnerId(userId);
    }

    @Override
    public Collection<Item> searchByText(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        return itemRepository.searchItemsByText(text);
    }
}
