package ru.practicum.ewm.category.service;


import ru.practicum.ewm.category.dto.CategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto createCategory(CategoryDto newCategoryRequest);

    CategoryDto updateCategory(CategoryDto categoryDto);

    void deleteCategory(Long id);

    CategoryDto getCategoryById(Long id);

    List<CategoryDto> getAllCategories(Integer from, Integer size);
}