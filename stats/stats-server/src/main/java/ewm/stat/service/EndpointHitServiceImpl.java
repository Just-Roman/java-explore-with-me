package ewm.stat.service;

import dto.EndpointHitDto;
import dto.ViewStatsDto;
import ewm.exception.BadRequestException;
import ewm.stat.EndpointHit;
import ewm.stat.EndpointHitMapper;
import ewm.stat.EndpointHitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class EndpointHitServiceImpl implements EndpointHitService {
    private final EndpointHitRepository repository;
    private final EndpointHitMapper mapper;

    @Override
    public void create(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = mapper.dtoToModel(endpointHitDto);
        repository.save(endpointHit);
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (start.isAfter(end)) {
            throw new BadRequestException("Время указанно не верно");
        }
        if (unique) {
            if (uris != null) {
                return repository.getHitsWithUrisWithUniqueIp(uris, start, end);
            }
            return repository.getHitsWithoutUrisWithUniqueIp(start, end);
        } else {
            if (uris != null) {
                return repository.getAllHitsWithUris(uris, start, end);
            }
            return repository.getAllHitsWithoutUris(start, end);
        }
    }


}
