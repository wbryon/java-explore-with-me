package ru.practicum.ewm.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.EndpointHitDto;
import ru.practicum.ewm.EndpointHitReturnDto;
import ru.practicum.ewm.ViewStats;
import ru.practicum.ewm.service.StatsService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
public class StatsController {
    private static final String DTF = "yyyy-MM-dd HH:mm:ss";
    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public EndpointHitReturnDto saveHit(@RequestBody @Valid EndpointHitDto endpointHitDto) {
        return statsService.saveHit(endpointHitDto);
    }

    @GetMapping("/stats")
    public List<ViewStats> getStats(@RequestParam(name = "start") @DateTimeFormat(pattern = DTF) LocalDateTime start,
                                    @RequestParam(name = "end") @DateTimeFormat(pattern = DTF) LocalDateTime end,
                                    @RequestParam(name = "uris", required = false, defaultValue = "") List<String> uris,
                                    @RequestParam(name = "unique", required = false, defaultValue = "false") Boolean unique) {
        return statsService.getAllStats(start, end, uris, unique);
    }
}
