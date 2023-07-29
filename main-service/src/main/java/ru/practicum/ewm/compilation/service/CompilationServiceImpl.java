package ru.practicum.ewm.compilation.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.WrongRequestException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    public CompilationServiceImpl(CompilationRepository compilationRepository, EventRepository eventRepository) {
        this.compilationRepository = compilationRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public CompilationDto createCompilation(NewCompilationDto compilationDto) {
        List<Event> events;
        if (compilationDto.getEvents() == null)
            events = eventRepository.findAll();
        else
            events = eventRepository.findAllById(compilationDto.getEvents());
        List<EventShortDto> shortEvents = events.stream()
                .map(EventMapper::toEventShortDto).collect(Collectors.toList());
        return CompilationMapper.toCompilationDto(compilationRepository
                .save(CompilationMapper.toCompilation(compilationDto, events)), shortEvents);
    }

    @Override
    public CompilationDto updateCompilation(Long compId, NewCompilationDto request) {
        Compilation compilation = compilationRepository.findCompilationById(compId);
        if (request.getTitle() != null && request.getTitle().length() > 50)
            throw new WrongRequestException("Имя запроса длиннее 50 знаков");
        compilation.setPinned(request.isPinned());
        if (request.getTitle() != null)
            compilation.setTitle(request.getTitle());
        if (request.getEvents() != null && !request.getEvents().isEmpty())
            compilation.setEvents(eventRepository.findAllById(request.getEvents()));
        List<EventShortDto> shortEvents = compilation.getEvents().stream()
                .map(EventMapper::toEventShortDto).collect(Collectors.toList());
        return CompilationMapper.toCompilationDto(compilationRepository.save(compilation), shortEvents);
    }

    @Override
    public void deleteCompilation(Long compId) {
        compilationRepository.deleteById(compId);
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return compilationRepository
                .getAllByPinned(pinned, pageable).stream()
                .map(c -> CompilationMapper.toCompilationDto(c, c.getEvents()
                        .stream()
                        .map(EventMapper::toEventShortDto)
                        .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = compilationRepository.findCompilationById(compId);
        List<EventShortDto> shortEvents = compilation
                .getEvents()
                .stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
        return CompilationMapper.toCompilationDto(compilation, shortEvents);
    }
}
