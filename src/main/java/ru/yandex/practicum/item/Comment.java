package ru.yandex.practicum.item;

import jakarta.persistence.*;
import lombok.Builder;
import ru.yandex.practicum.user.User;

import java.time.LocalDateTime;

@Table(name = "comments")
@Entity
@Builder
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(nullable = false)
    private String text;

    @Column(nullable = false)
    private LocalDateTime created;
}

