package ewm.compilations;

import ewm.compilations.dto.CompilationDto;
import ewm.compilations.dto.CreateCompilationDto;
import org.springframework.stereotype.Component;

@Component
public class CompilationMapper {
    public Compilation toCompilationEntity(CreateCompilationDto createCompilationDto) {
        return new Compilation(
                createCompilationDto.getTitle(),
                createCompilationDto.getPinned()
        );
    }

    public CompilationDto toCompilationDto(Compilation compilation) {
        return new CompilationDto(
                compilation.getId(),
                compilation.getPinned(),
                compilation.getTitle()
        );
    }
}
