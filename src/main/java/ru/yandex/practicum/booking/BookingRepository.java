package ru.yandex.practicum.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {


    @Query("""
            SELECT b
            FROM Booking b
            WHERE b.item.id = :itemId
              AND b.bookingEnd < :now
            ORDER BY b.bookingEnd DESC
            """)
    Booking findLastBooking(@Param("itemId") long itemId,
                            @Param("now") LocalDateTime now);

    @Query("""
            SELECT b
            FROM Booking b
            WHERE b.item.id = :itemId
              AND b.bookingStart > :now
            ORDER BY b.bookingStart ASC
            """)
    Booking findNextBooking(@Param("itemId") long itemId,
                            @Param("now") LocalDateTime now);

    @Query(value = """
            SELECT b.*
            FROM bookings b
            WHERE b.booker_id = :userId
            ORDER BY b.booking_start DESC
            """, nativeQuery = true)
    List<Booking> getBookingsAll(@Param("userId") long userId);


    @Query(value = """
            SELECT b.*
            FROM bookings b
            WHERE b.booker_id = :userId
              AND CURRENT_TIMESTAMP BETWEEN b.booking_start AND b.booking_end
            ORDER BY b.booking_start DESC
            """, nativeQuery = true)
    List<Booking> getBookingsCurrent(@Param("userId") long userId);


    @Query(value = """
            SELECT b.*
            FROM bookings b
            WHERE b.booker_id = :userId
              AND b.booking_end < CURRENT_TIMESTAMP
            ORDER BY b.booking_start DESC
            """, nativeQuery = true)
    List<Booking> getBookingsPast(@Param("userId") long userId);


    @Query(value = """
            SELECT b.*
            FROM bookings b
            WHERE b.booker_id = :userId
              AND b.booking_start > CURRENT_TIMESTAMP
            ORDER BY b.booking_start DESC
            """, nativeQuery = true)
    List<Booking> getBookingsFuture(@Param("userId") long userId);


    @Query(value = """
            SELECT b.*
            FROM bookings b
            WHERE b.booker_id = :userId
              AND b.status = 'WAITING'
            ORDER BY b.booking_start DESC
            """, nativeQuery = true)
    List<Booking> getBookingsWaiting(@Param("userId") long userId);


    @Query(value = """
            SELECT b.*
            FROM bookings b
            WHERE b.booker_id = :userId
              AND b.status = 'REJECTED'
            ORDER BY b.booking_start DESC
            """, nativeQuery = true)
    List<Booking> getBookingsRejected(@Param("userId") long userId);

    /// ---------------------------------------------------------------------------------------------------------

    @Query(value = """
            SELECT b.*
            FROM bookings b
            JOIN items i ON b.item_id = i.id
            WHERE i.owner_id = :ownerId
            ORDER BY b.booking_start DESC
            """, nativeQuery = true)
    List<Booking> getBookingsAllByOwner(@Param("ownerId") long ownerId);


    @Query(value = """
            SELECT b.*
            FROM bookings b
            JOIN items i ON b.item_id = i.id
            WHERE i.owner_id = :ownerId
              AND CURRENT_TIMESTAMP BETWEEN b.booking_start AND b.booking_end
            ORDER BY b.booking_start DESC
            """, nativeQuery = true)
    List<Booking> getBookingsCurrentByOwner(@Param("ownerId") long ownerId);


    @Query(value = """
            SELECT b.*
            FROM bookings b
            JOIN items i ON b.item_id = i.id
            WHERE i.owner_id = :ownerId
              AND b.booking_end < CURRENT_TIMESTAMP
            ORDER BY b.booking_start DESC
            """, nativeQuery = true)
    List<Booking> getBookingsPastByOwner(@Param("ownerId") long ownerId);


    @Query(value = """
            SELECT b.*
            FROM bookings b
            JOIN items i ON b.item_id = i.id
            WHERE i.owner_id = :ownerId
              AND b.booking_start > CURRENT_TIMESTAMP
            ORDER BY b.booking_start DESC
            """, nativeQuery = true)
    List<Booking> getBookingsFutureByOwner(@Param("ownerId") long ownerId);


    @Query(value = """
            SELECT b.*
            FROM bookings b
            JOIN items i ON b.item_id = i.id
            WHERE i.owner_id = :ownerId
              AND b.status = 'WAITING'
            ORDER BY b.booking_start DESC
            """, nativeQuery = true)
    List<Booking> getBookingsWaitingByOwner(@Param("ownerId") long ownerId);


    @Query(value = """
            SELECT b.*
            FROM bookings b
            JOIN items i ON b.item_id = i.id
            WHERE i.owner_id = :ownerId
              AND b.status = 'REJECTED'
            ORDER BY b.booking_start DESC
            """, nativeQuery = true)
    List<Booking> getBookingsRejectedByOwner(@Param("ownerId") long ownerId);

    @Query(value = "SELECT * FROM bookings b " +
            "WHERE b.booker_id = :userId " +
            "AND b.item_id = :itemId " +
            "AND b.status = 'APPROVED' " +
            "AND b.booking_end < NOW() " +
            "ORDER BY b.booking_end DESC LIMIT 1",
            nativeQuery = true)
    Optional<Booking> findCompletedBookingByUserAndItem(@Param("userId") long userId,
                                                        @Param("itemId") long itemId);

}
