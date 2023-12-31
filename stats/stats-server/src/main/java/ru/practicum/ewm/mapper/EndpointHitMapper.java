package ru.practicum.ewm.mapper;

import ru.practicum.ewm.EndpointHitDto;
import ru.practicum.ewm.model.EndpointHit;

public class EndpointHitMapper {
    public static EndpointHit mapToEndpointHit(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = new EndpointHit();
        endpointHit.setApp(endpointHitDto.getApp());
        endpointHit.setUri(endpointHitDto.getUri());
        endpointHit.setIp(endpointHitDto.getIp());
        endpointHit.setTimestamp(endpointHitDto.getTimestamp());
        return endpointHit;
    }

    public static EndpointHitDto mapToEndpointHitDto(EndpointHit endpointHit) {
        EndpointHitDto answerDTO = new EndpointHitDto();
        answerDTO.setApp(endpointHit.getApp());
        answerDTO.setUri(endpointHit.getUri());
        answerDTO.setIp(endpointHit.getIp());
        answerDTO.setTimestamp(endpointHit.getTimestamp());
        return answerDTO;
    }
}
