package ru.practicum.ewm.request.mapper;

import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.model.Request;

public class RequestMapper {
    public static RequestDto toRequestDto(Request request) {
        RequestDto requestDto = new RequestDto();
        requestDto.setId(request.getId());
        requestDto.setRequester(request.getRequester().getId());
        requestDto.setCreated(request.getCreated());
        requestDto.setStatus(request.getStatus());
        requestDto.setEvent(request.getEvent().getId());
        return requestDto;
    }
}