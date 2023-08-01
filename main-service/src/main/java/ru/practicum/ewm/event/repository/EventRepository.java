package ru.practicum.ewm.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> getByInitiatorId(Long userId, Pageable pageable);

    List<Event> getFirstByCategoryId(Long categoryId);

    @Query("SELECT e FROM Event e " +
            "where (:ids IS NULL OR e.initiator.id in :ids) " +
            "and (:states IS NULL OR e.state in :states) " +
            "and (:categoriesIds IS NULL OR e.category.id in :categoriesIds) " +
            "and (CAST(:eventDateStart AS timestamp) IS NULL OR e.eventDate >= :eventDateStart) " +
            "and (CAST(:eventDateEnd AS timestamp) IS NULL OR e.eventDate <= :eventDateEnd) " +
            "order by e.id ASC")
    List<Event> getAllEventsByAdmin(List<Long> ids,
                                    List<EventState> states,
                                    List<Long> categoriesIds,
                                    LocalDateTime eventDateStart,
                                    LocalDateTime eventDateEnd,
                                    Pageable pageable);

    @Query("SELECT e FROM Event e " +
            "WHERE (UPPER(e.annotation) LIKE UPPER(CONCAT('%',:text,'%')) " +
            "OR UPPER(e.description) LIKE UPPER(CONCAT('%',:text,'%')) OR :text IS NULL) " +
            "AND (:categories IS NULL OR e.category.id IN (:categories)) " +
            "AND (:paid IS NULL OR e.paid = :paid) " +
            "and (CAST(:rangeStart AS timestamp) IS NULL OR e.eventDate >= :rangeStart) " +
            "and (CAST(:rangeEnd AS timestamp) IS NULL OR e.eventDate <= :rangeEnd)")
    List<Event> getEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);
}

