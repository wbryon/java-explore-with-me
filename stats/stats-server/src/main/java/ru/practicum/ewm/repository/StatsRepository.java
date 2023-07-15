package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.ViewStats;
import ru.practicum.ewm.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndpointHit, Long> {
    @Query("select new ru.practicum.ewm.ViewStats(e.app, e.uri, count(e.ip))" +
            "from EndpointHit e " +
            "where e.timestamp between ?1 and ?2 " +
            "and e.uri in ?3 " +
            "group by e.app, e.uri " +
            "order by count(e.ip) desc")
    List<ViewStats> getStats(LocalDateTime timestampStart, LocalDateTime timestampEnd, Collection<String> uris);

    @Query("select new ru.practicum.ewm.ViewStats(s.app, s.uri, count(distinct s.ip))" +
            "from EndpointHit s " +
            "where s.timestamp between ?1 and ?2 " +
            "and s.uri in ?3 " +
            "group by s.app, s.uri " +
            "order by count(s.ip) desc")
    List<ViewStats> getDistinctStats(LocalDateTime timestampStart, LocalDateTime timestampEnd, Collection<String> uris);

    @Query("select new ru.practicum.ewm.ViewStats(s.app, s.uri, count(s.ip))" +
            "from EndpointHit s " +
            "where s.timestamp between ?1 and ?2 " +
            "group by s.app, s.uri " +
            "order by count(s.ip) desc")
    List<ViewStats> getAllStats(LocalDateTime timestampStart, LocalDateTime timestampEnd);
}
