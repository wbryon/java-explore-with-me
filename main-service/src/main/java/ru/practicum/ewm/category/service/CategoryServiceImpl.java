package ru.practicum.ewm.category.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository, EventRepository eventRepository) {
        this.categoryRepository = categoryRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public CategoryDto createCategory(CategoryDto newCategoryRequest) {
        Category category;
        try {
            category = categoryRepository.save(CategoryMapper.toCategory(newCategoryRequest));
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Имя создаваемой категории должно быть уникальным");
        }
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto) {
        Category category = categoryRepository.findById(categoryDto.getId())
                .orElseThrow(() -> new NotFoundException("Категория с id: " + categoryDto.getId() + " не найдена"));
        category.setName(categoryDto.getName());
        Category updatedCategory;
        try {
            updatedCategory = categoryRepository.save(category);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Имя обновляемой категории должно быть уникальным");
        }
        return CategoryMapper.toCategoryDto(updatedCategory);
    }

    @Override
    public void deleteCategory(Long id) {
        categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Категория с id: " + id + " не найдена"));
        if (!eventRepository.getFirstByCategoryId(id).isEmpty())
            throw new ConflictException("Невозможно удалить категорию, содержащую события");
        categoryRepository.deleteById(id);
    }

    @Override
    public CategoryDto getCategoryById(Long id) {
        return CategoryMapper.toCategoryDto(categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Категория с id: " + id + " не найдена")));
    }

    @Override
    public List<CategoryDto> getAllCategories(Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return categoryRepository.findAll(pageable)
                .stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }
}
