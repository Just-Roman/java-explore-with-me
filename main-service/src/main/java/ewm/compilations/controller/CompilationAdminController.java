package ewm.compilations.controller;

import ewm.compilations.dto.CompilationDto;
import ewm.compilations.dto.CreateCompilationDto;
import ewm.compilations.dto.UpdateCompilationRequest;
import ewm.compilations.service.CompilationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
public class CompilationAdminController {
    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public CompilationDto addCompilation(@RequestBody @Valid CreateCompilationDto createCompilationDto) {
        return compilationService.addCompilation(createCompilationDto);
    }

    @PatchMapping("/{compilationId}")
    public CompilationDto updateCompilation(@PathVariable Long compilationId,
                                            @RequestBody @Valid UpdateCompilationRequest updateCompilation) {
        return compilationService.updateCompilation(compilationId, updateCompilation);
    }

    @DeleteMapping("/{compilationId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable long compilationId) {
        compilationService.deleteCompilation(compilationId);
    }
}