package ewm.categories.controller;

import ewm.categories.dto.CategoryDto;
import ewm.categories.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoriesPublicController {
    private final CategoryService service;

    @GetMapping
    public List<CategoryDto> getCategories(
            @RequestParam(value = "from", defaultValue = "0") Integer from,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return service.getCategories(from, size);
    }

    @GetMapping("/{catId}")
    public CategoryDto getById(@PathVariable Long catId) {
        return service.getById(catId);
    }

}
