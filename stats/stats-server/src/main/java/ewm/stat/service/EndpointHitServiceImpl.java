package ewm.stat.service;

import dto.CreateDto;
import dto.ViewStatsDto;
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
public class EndpointHitServiceImpl implements  EndpointHitService{
    private final EndpointHitRepository repository;
    private final EndpointHitMapper mapper;

    @Override
    public void create(CreateDto createDto) {
        EndpointHit endpointHit = mapper.dtoToModel(createDto);
        repository.save(endpointHit);
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (unique) {
            return repository.getUniqueStats(start, end, uris);
        } else {
            return repository.getStats(start, end, uris);
        }
    }


}
