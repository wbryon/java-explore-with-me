package ru.practicum.ewm.request.dto;

import lombok.Data;
import ru.practicum.ewm.request.model.RequestStatus;

import java.util.List;

/**
 * Изменение статуса запроса на участие в событии текущего пользователя
 */
@Data
public class EventRequestStatusUpdateRequest {
    private List<Long> requestIds;
    private RequestStatus status;
}