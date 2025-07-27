package ewm.categories.service;

import ewm.categories.Category;
import ewm.categories.CategoryMapper;
import ewm.categories.CategoryRepository;
import ewm.categories.dto.CategoryCreateDto;
import ewm.categories.dto.CategoryDto;
import ewm.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository repository;
    private final CategoryMapper mapper;

    @Override
    public CategoryDto create(CategoryCreateDto createDto) {
        Category category = mapper.categoryCreateDtoToModel(createDto);
        return mapper.modelToDto(repository.save(category));
    }

    @Override
    public CategoryDto update(long categoryId, CategoryCreateDto createDto) {
        Category savedCategory = checkAndReturnCategory(categoryId);
        savedCategory.setName(createDto.getName());
        return mapper.modelToDto(repository.save(savedCategory));
    }

    @Override
    public void deleteById(long categoryId) {
        checkAndReturnCategory(categoryId);
        repository.deleteById(categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return repository.findAll(pageable).map(mapper::modelToDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getById(long categoryId) {
        Category category = checkAndReturnCategory(categoryId);
        return mapper.modelToDto(category);
    }

    public Category checkAndReturnCategory(long id) {
        return repository.findById(id).orElseThrow(() ->
                new NotFoundException("Category with id=" + id + " was not found"));
    }

}
