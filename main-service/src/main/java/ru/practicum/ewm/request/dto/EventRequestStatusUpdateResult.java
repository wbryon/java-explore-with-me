package ru.practicum.ewm.request.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Результат подтверждения/отклонения заявок на участие в событии
 */
@Data
public class EventRequestStatusUpdateResult {
    private List<RequestDto> confirmedRequests = new ArrayList<>();
    private List<RequestDto> rejectedRequests = new ArrayList<>();
}