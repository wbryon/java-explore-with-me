package ru.practicum.ewm.service;

import org.springframework.stereotype.Service;
import ru.practicum.ewm.EndpointHitDto;
import ru.practicum.ewm.ViewStats;
import ru.practicum.ewm.exception.WrongRequestException;
import ru.practicum.ewm.mapper.EndpointHitMapper;
import ru.practicum.ewm.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    public StatsServiceImpl(StatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

    @Override
    public void saveHit(EndpointHitDto hitDto) {
        statsRepository.save(EndpointHitMapper.mapToEndpointHit(hitDto));
    }

    @Override
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (end.isBefore(start))
            throw new WrongRequestException("Неверные параметры времени начала и конца");
        if (!unique) {
            if (uris.isEmpty())
                return statsRepository.getAllStats(start, end)
                    .stream().map(StatsServiceImpl::setViewStats).collect(Collectors.toList());
            return statsRepository.getStats(start, end, uris)
                    .stream().map(StatsServiceImpl::setViewStats).collect(Collectors.toList());
        }
        return statsRepository.getDistinctStats(start, end, uris)
                .stream().map(StatsServiceImpl::setViewStats).collect(Collectors.toList());
    }

    private static ViewStats setViewStats(ViewStats viewStats) {
        ViewStats stats = new ViewStats();
        stats.setApp(viewStats.getApp());
        stats.setUri(viewStats.getUri());
        stats.setHits(viewStats.getHits());
        return stats;
    }
}
