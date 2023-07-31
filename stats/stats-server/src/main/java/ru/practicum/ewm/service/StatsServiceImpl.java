package ru.practicum.ewm.service;

import org.springframework.stereotype.Service;
import ru.practicum.ewm.EndpointHitDto;
import ru.practicum.ewm.EndpointHitReturnDto;
import ru.practicum.ewm.ViewStats;
import ru.practicum.ewm.exception.WrongRequestException;
import ru.practicum.ewm.mapper.EndpointHitMapper;
import ru.practicum.ewm.model.EndpointHit;
import ru.practicum.ewm.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    public StatsServiceImpl(StatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

    @Override
    public EndpointHitReturnDto saveHit(EndpointHitDto hitDto) {
        EndpointHit endpointHit = EndpointHitMapper.mapToEndpointHit(hitDto);
        return EndpointHitMapper.mapToEndpointHitDto(statsRepository.save(endpointHit));
    }

    @Override
    public List<ViewStats> getAllStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (end.isBefore(start))
            throw new WrongRequestException("Неверные параметры времени начала и конца");
        if (!unique) {
            if (uris.isEmpty()) {
                List<ViewStats> viewStatsList = new ArrayList<>();
                for (ViewStats viewStats : statsRepository.getAllStats(start, end)) {
                    ViewStats stats = setViewStats(viewStats);
                    viewStatsList.add(stats);
                }
                return viewStatsList;
            }
            List<ViewStats> viewStatsList = new ArrayList<>();
            for (ViewStats viewStats : statsRepository.getStats(start, end, uris)) {
                ViewStats stats = setViewStats(viewStats);
                viewStatsList.add(stats);
            }
            return viewStatsList;
        }
        List<ViewStats> viewStatsList = new ArrayList<>();
        for (ViewStats viewStats : statsRepository.getDistinctStats(start, end, uris)) {
            ViewStats stats = setViewStats(viewStats);
            viewStatsList.add(stats);
        }
        return viewStatsList;
    }

    private static ViewStats setViewStats(ViewStats viewStats) {
        ViewStats stats = new ViewStats();
        stats.setApp(viewStats.getApp());
        stats.setUri(viewStats.getUri());
        stats.setHits(viewStats.getHits());
        return stats;
    }
}
