package ru.practicum.ewm.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.request.model.Request;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> getAllByRequesterId(Long requesterId);

    List<Request> getByRequesterIdAndEventId(Long requesterId, Long eventId);

    List<Request> getAllByIdIn(List<Long> requestIds);

    List<Request> getAllByEventId(Long eventId);
}
