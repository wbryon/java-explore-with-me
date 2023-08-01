package ru.practicum.ewm.request.service;

import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.RequestDto;

import java.util.List;

public interface RequestService {
    RequestDto createRequest(Long userId, Long eventId);

    List<RequestDto> getRequestsByUser(Long userId);

    EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest statusUpdateRequest);

    RequestDto cancelRequest(Long userId, Long requestId);
}