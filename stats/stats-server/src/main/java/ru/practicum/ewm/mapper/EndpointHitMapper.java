package ru.practicum.ewm.mapper;

import ru.practicum.ewm.EndpointHitDto;
import ru.practicum.ewm.EndpointHitReturnDto;
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

    public static EndpointHitReturnDto mapToEndpointHitDto(EndpointHit endpointHit) {
        EndpointHitReturnDto answerDTO = new EndpointHitReturnDto();
        answerDTO.setApp(endpointHit.getApp());
        answerDTO.setUri(endpointHit.getUri());
        answerDTO.setIp(endpointHit.getIp());
        answerDTO.setTimestamp(endpointHit.getTimestamp());
        return answerDTO;
    }
}
