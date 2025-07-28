package ewm.categories;

import ewm.categories.dto.CategoryCreateDto;
import ewm.categories.dto.CategoryDto;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public Category categoryCreateDtoToModel(CategoryCreateDto dto) {
        return Category.builder()
                .name(dto.getName())
                .build();
    }

    public CategoryDto modelToDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }


}
