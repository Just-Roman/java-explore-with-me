package ewm.categories.controller;

import ewm.categories.dto.CategoryCreateDto;
import ewm.categories.dto.CategoryDto;
import ewm.categories.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
public class CategoriesAdminController {
    private final CategoryService service;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public CategoryDto create(@RequestBody @Valid CategoryCreateDto createDto) {
        return service.create(createDto);
    }

    @PatchMapping("/{catId}")
    public CategoryDto update(@PathVariable Long catId,
                              @RequestBody @Valid CategoryCreateDto createDto) {
        return service.update(catId, createDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long catId) {
        service.deleteById(catId);
    }

}
