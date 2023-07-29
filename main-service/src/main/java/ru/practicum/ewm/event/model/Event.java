package ru.practicum.ewm.event.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(nullable = false, length = 120)
    String title;
    @Column(nullable = false, length = 2000)
    String annotation;
    @Column(nullable = false, length = 7000)
    String description;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    EventState state = EventState.PENDING;
    @CreationTimestamp
    LocalDateTime createdOn;
    LocalDateTime publishedOn;
    LocalDateTime eventDate;
    @ManyToOne
    @JoinColumn(name = "initiator_id", referencedColumnName = "id")
    User initiator;
    Float lat;
    Float lon;
    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    Category category;
    Boolean paid;
    @Column(name = "moderation")
    Boolean requestModeration;
    Long participantLimit;
    Long confirmedRequests;
    long views;
}
