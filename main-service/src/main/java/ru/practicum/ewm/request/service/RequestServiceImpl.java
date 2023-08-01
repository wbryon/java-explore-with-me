package ru.practicum.ewm.request.service;

import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.WrongRequestException;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public RequestServiceImpl(RequestRepository requestRepository, UserRepository userRepository, EventRepository eventRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public RequestDto createRequest(Long userId, Long eventId) {
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        Event event = eventRepository.findEventById(eventId);
        if (!requestRepository.getByRequesterIdAndEventId(userId, eventId).isEmpty())
            throw new ConflictException("Запрос для события с id: " + eventId + " уже создан ранее");
        if (!event.getState().equals(EventState.PUBLISHED))
            throw new ConflictException("Событие не опубликовано");
        if (event.getInitiator().equals(requester))
            throw new ConflictException("Автор запроса является инициатором события");
        if (event.getParticipantLimit().equals(event.getConfirmedRequests()) && event.getParticipantLimit() > 0)
            throw new ConflictException("Лимит участников события уже выбран");

        Request request = new Request();
        request.setRequester(requester);
        request.setEvent(event);
        request.setCreated(LocalDateTime.now());
        request.setStatus(RequestStatus.PENDING);

        if (!event.getRequestModeration()) {
            request.setStatus(RequestStatus.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        }
        if (event.getParticipantLimit() == 0 && !event.getRequestModeration()) {
            request.setStatus(RequestStatus.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        }
        if (event.getRequestModeration() && event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        }
        eventRepository.save(event);
        return RequestMapper.toRequestDto(requestRepository.save(request));
    }


    @Override
    public List<RequestDto> getRequestsByUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        return requestRepository.getAllByRequesterId(userId)
                .stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        Event event = eventRepository.findEventById(eventId);
        if (!user.getId().equals(event.getInitiator().getId()))
            throw new WrongRequestException(
                    "Пользователь с id: " + user.getId() + " не является создателем события с id: " + event.getId());
        EventRequestStatusUpdateResult responseDto = new EventRequestStatusUpdateResult();
        List<Request> requestsFromStorage = requestRepository.getAllByIdIn(eventRequestStatusUpdateRequest.getRequestIds());

        for (Request request : requestsFromStorage) {
            if (event.getParticipantLimit() == 0 || !event.getRequestModeration())
                break;
            if (request.getStatus().equals(RequestStatus.CONFIRMED) &&
                    eventRequestStatusUpdateRequest.getStatus().equals(RequestStatus.REJECTED))
                throw new ConflictException("Запрос уже подтверждён");

            switch (eventRequestStatusUpdateRequest.getStatus()) {
                case REJECTED:
                    request.setStatus(RequestStatus.REJECTED);
                    responseDto.getRejectedRequests().add((RequestMapper.toRequestDto(request)));
                    break;
                case CONFIRMED:
                    if (event.getParticipantLimit() == 0 || event.getConfirmedRequests() < event.getParticipantLimit()) {
                        request.setStatus(RequestStatus.CONFIRMED);
                        responseDto.getConfirmedRequests().add(RequestMapper.toRequestDto(request));
                        event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                    } else {
                        request.setStatus(RequestStatus.REJECTED);
                        responseDto.getRejectedRequests().add(RequestMapper.toRequestDto(request));
                        requestRepository.save(request);
                        throw new ConflictException("Лимит участников события уже выбран");
                    }
            }
        }
        eventRepository.save(event);
        requestRepository.saveAll(requestsFromStorage);
        return responseDto;
    }

    @Override
    public RequestDto cancelRequest(Long userId, Long requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id: " + requestId + " не найден"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        if (!request.getRequester().equals(user))
            throw new WrongRequestException("Пользователь не является создателем запроса");
        request.setStatus(RequestStatus.CANCELED);
        return RequestMapper.toRequestDto(requestRepository.save(request));
    }
}
