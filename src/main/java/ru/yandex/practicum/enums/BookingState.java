package ru.yandex.practicum.enums;

public enum BookingState {
    ALL("Все"),
    CURRENT("Текущие"),
    PAST("Завершенные"),
    FUTURE("Будущие"),
    WAITING("Ожидающие подтверждения"),
    REJECTED("Отклоненные");

    BookingState(String description) {
    }
}
