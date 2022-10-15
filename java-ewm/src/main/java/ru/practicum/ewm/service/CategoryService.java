package ru.practicum.ewm.service;

import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.entity.Category;

import java.util.Collection;

@Service
public interface CategoryService {

    CategoryDto create(CategoryDto categoryDto);

    CategoryDto update(CategoryDto categoryDto);

    void delete(Long categoryId);

    CategoryDto getCategoryById(Long categoryId);

    Collection<CategoryDto> getCategories(Integer from, Integer size);

    Category getCategory(Long categoryId);
}
