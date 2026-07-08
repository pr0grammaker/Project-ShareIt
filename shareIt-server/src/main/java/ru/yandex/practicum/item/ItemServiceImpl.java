package ru.yandex.practicum.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.booking.BookingMapper;
import ru.yandex.practicum.booking.BookingRepository;
import ru.yandex.practicum.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.exceptions.NotFoundException;
import ru.yandex.practicum.request.ItemRequest;
import ru.yandex.practicum.request.ItemRequestRepository;
import ru.yandex.practicum.user.User;
import ru.yandex.practicum.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService, CommentService {

    private final ItemRepository itemRepositoryDb;
    private final UserRepository userRepositoryDb;
    private final ItemMapper itemMapper;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final ItemRequestRepository itemRequestRepository;

    @Transactional
    @Override
    public ItemDto create(long userId, ItemDto itemDto) {
        if (userRepositoryDb.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }

        User owner = userRepositoryDb.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Item item = itemMapper.mapToItem(itemDto);
        item.setOwner(owner);

        if (itemDto.getRequestId() != null) {
            ItemRequest request = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Request not found"));
            item.setRequest(request);
        }

        Item savedItem = itemRepositoryDb.save(item);

        return itemMapper.mapToItemDto(savedItem);
    }

    @Transactional
    @Override
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        userRepositoryDb.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Item item = itemRepositoryDb.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (!Objects.equals(item.getOwner().getId(), userId)) {
            throw new NotFoundException("Пользователь не является владельцем вещи");
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

        Item saved = itemRepositoryDb.save(item);

        return itemMapper.mapToItemDto(saved);
    }

    @Override
    public ItemDto getItemById(long itemId) {
        Item item = itemRepositoryDb.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        return itemMapper.mapToItemDto(item);
    }

    @Override
    public Collection<ItemDto> getItemsByOwner(long userId) {
        userRepositoryDb.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        List<Item> items = itemRepositoryDb.findByOwnerId(userId);

        return items.stream()
                .map(item -> {

                    ItemDto dto = itemMapper.mapToItemDto(item);

                    dto.setLastBooking(
                            bookingMapper.mapToBookingResponseDto(
                                    bookingRepository.findLastBooking(item.getId(), LocalDateTime.now())
                                            .orElse(null)
                            )
                    );

                    dto.setNextBooking(
                            bookingMapper.mapToBookingResponseDto(
                                    bookingRepository.findNextBooking(item.getId(), LocalDateTime.now())
                                            .orElse(null)
                            )
                    );

                    return dto;
                })
                .toList();
    }

    @Override
    public Collection<ItemDto> searchByText(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        return itemRepositoryDb.searchItemsByText(text)
                .stream()
                .map(itemMapper::mapToItemDto)
                .toList();
    }

    @Transactional
    @Override
    public CommentResponseDto createComment(long userId, long itemId, CommentDto commentDto) {
        User user = userRepositoryDb.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Item item = itemRepositoryDb.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        bookingRepository.findCompletedBookingByUserAndItem(userId, itemId)
                .orElseThrow(() -> new ConditionsNotMetException("Комментарий можно оставить только после бронирования"));

        Comment comment = Comment.builder()
                .text(commentDto.getText())
                .author(user)
                .item(item)
                .created(LocalDateTime.now())
                .build();

        Comment save = commentRepository.save(comment);
        return commentMapper.mapToCommentResponseDto(save);
    }

    @Override
    public Collection<ItemDto> getCommentsByItemId(long userId, long itemId) {
        userRepositoryDb.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Item item = itemRepositoryDb.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        ItemDto itemDto = itemMapper.mapToItemDto(item);

        List<CommentResponseDto> comments = commentRepository.findByItemId(itemId).stream()
                .map(commentMapper::mapToCommentResponseDto)
                .toList();
        itemDto.setComments(comments);

        itemDto.setLastBooking(
                bookingMapper.mapToBookingResponseDto(
                        bookingRepository.findLastBooking(itemId, LocalDateTime.now())
                                .orElse(null)
                )
        );
        itemDto.setNextBooking(
                bookingMapper.mapToBookingResponseDto(
                        bookingRepository.findNextBooking(itemId, LocalDateTime.now())
                                .orElse(null)
                )
        );
        return List.of(itemDto);

    }

}
