package ewm.compilations.service;

import ewm.compilations.dto.CompilationDto;
import ewm.compilations.dto.CreateCompilationDto;
import ewm.compilations.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDto getCompilationById(Long compilationId);

    CompilationDto addCompilation(CreateCompilationDto newCompilationDto);

    CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilation);

    void deleteCompilation(Long compilationId);
}
