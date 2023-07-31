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
        EndpointHitReturnDto returnDto = new EndpointHitReturnDto();
        returnDto.setApp(endpointHit.getApp());
        returnDto.setUri(endpointHit.getUri());
        returnDto.setIp(endpointHit.getIp());
        returnDto.setTimestamp(endpointHit.getTimestamp());
        return returnDto;
    }
}
