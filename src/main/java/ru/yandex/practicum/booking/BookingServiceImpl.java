package ru.yandex.practicum.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.enums.BookingState;
import ru.yandex.practicum.enums.Status;
import ru.yandex.practicum.expection.ConditionsNotMetException;
import ru.yandex.practicum.expection.NotFoundException;
import ru.yandex.practicum.item.Item;
import ru.yandex.practicum.item.ItemRepositoryDb;
import ru.yandex.practicum.user.User;
import ru.yandex.practicum.user.UserRepositoryDb;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final UserRepositoryDb userRepositoryDb;

    private final ItemRepositoryDb itemRepositoryDb;

    private final BookingMapper bookingMapper;


    @Override
    public BookingResponseDto createBooking(long userId, BookingDto bookingDto) {
        User booker = userRepositoryDb.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Item item = itemRepositoryDb.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (bookingDto.getBookingStart().equals(bookingDto.getBookingEnd())) {
            throw new ConditionsNotMetException("Дата начала и окончания не могут совпадать");
        }
        if (bookingDto.getBookingEnd().isBefore(bookingDto.getBookingStart())) {
            throw new ConditionsNotMetException("Дата окончания должна быть позже даты начала");
        }

        Booking booking = Booking.builder()
                .bookingStart(bookingDto.getBookingStart())
                .bookingEnd(bookingDto.getBookingEnd())
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .build();

        Booking save = bookingRepository.save(booking);

        return bookingMapper.mapToBookingResponseDto(save);
    }

    @Override
    public BookingResponseDto updateApprovalStatus(long bookingId, long ownerId, boolean approved) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование отсутствует"));

        User owner = booking.getItem().getOwner();
        if (!Objects.equals(owner.getId(), ownerId)) {
            throw new ConditionsNotMetException("Пользователь не является владельцем вещи");
        }

        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }

        Booking save = bookingRepository.save(booking);

        return bookingMapper.mapToBookingResponseDto(save);
    }

    @Override
    public BookingResponseDto getBookingById(long bookingId, long userId) {
        userRepositoryDb.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование отсутствует"));

        User owner = booking.getItem().getOwner();

        if (!Objects.equals(booking.getBooker().getId(), userId) &&
                !Objects.equals(owner.getId(), userId)) {
            throw new NotFoundException("Пользователь не имеет доступа к бронированию");
        }

        Booking save = bookingRepository.save(booking);

        return bookingMapper.mapToBookingResponseDto(save);
    }

    @Override
    public List<BookingResponseDto> getBookingsByBooker(long userId, BookingState state) {
        userRepositoryDb.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        List<Booking> bookings = getBookingsByStateBooker(userId, state);
        return bookingMapper.mapToBookingListDto(bookings);
    }

    private List<Booking> getBookingsByStateBooker(long userId, BookingState state) {
        return switch (state) {
            case CURRENT -> bookingRepository.getBookingsCurrent(userId);
            case PAST -> bookingRepository.getBookingsPast(userId);
            case FUTURE -> bookingRepository.getBookingsFuture(userId);
            case WAITING -> bookingRepository.getBookingsWaiting(userId);
            case REJECTED -> bookingRepository.getBookingsRejected(userId);
            default -> bookingRepository.getBookingsAll(userId);
        };
    }

    @Override
    public List<BookingResponseDto> getBookingsByOwner(long userId, BookingState state) {
        userRepositoryDb.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        List<Booking> bookings = getBookingsByStateOwner(userId, state);
        return bookingMapper.mapToBookingListDto(bookings);
    }

    private List<Booking> getBookingsByStateOwner(long userId, BookingState state) {
        return switch (state) {
            case CURRENT -> bookingRepository.getBookingsCurrentByOwner(userId);
            case PAST -> bookingRepository.getBookingsPastByOwner(userId);
            case FUTURE -> bookingRepository.getBookingsFutureByOwner(userId);
            case WAITING -> bookingRepository.getBookingsWaitingByOwner(userId);
            case REJECTED -> bookingRepository.getBookingsRejectedByOwner(userId);
            default -> bookingRepository.getBookingsAllByOwner(userId);
        };
    }
}
