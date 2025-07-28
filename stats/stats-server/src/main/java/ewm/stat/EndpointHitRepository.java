package ewm.stat;

import dto.ViewStatsDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {

    @Query("SELECT new dto.ViewStatsDto(h.app, h.uri, COUNT(h.uri)) " +
            "FROM EndpointHit AS h " +
            "WHERE h.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT (h.uri) DESC")
    List<ViewStatsDto> getAllHitsWithoutUris(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new dto.ViewStatsDto(h.app, h.uri, COUNT(h.uri)) " +
            "FROM EndpointHit AS h " +
            "WHERE h.uri IN (?1) AND h.timestamp BETWEEN ?2 AND ?3 " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT (h.uri) DESC")
    List<ViewStatsDto> getAllHitsWithUris(List<String> uris, LocalDateTime start, LocalDateTime end);

    @Query("SELECT new dto.ViewStatsDto(h.app, h.uri, COUNT(DISTINCT h.ip)) " +
            "FROM EndpointHit AS h " +
            "WHERE h.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(DISTINCT h.ip) DESC")
    List<ViewStatsDto> getHitsWithoutUrisWithUniqueIp(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new dto.ViewStatsDto(h.app, h.uri, COUNT(DISTINCT h.ip)) " +
            "FROM EndpointHit AS h " +
            "WHERE h.uri IN (?1) AND h.timestamp BETWEEN ?2 AND ?3 " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(DISTINCT h.ip) DESC")
    List<ViewStatsDto> getHitsWithUrisWithUniqueIp(List<String> uris, LocalDateTime start, LocalDateTime end);

}
