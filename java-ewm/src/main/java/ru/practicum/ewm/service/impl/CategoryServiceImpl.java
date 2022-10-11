package ru.practicum.ewm.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.entity.Category;
import ru.practicum.ewm.exception.AlreadyExistsException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.CategoryMapper;
import ru.practicum.ewm.other.Pager;
import ru.practicum.ewm.service.CategoryService;
import ru.practicum.ewm.storage.CategoryRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService, Pager {

    private final CategoryRepository repository;
    private final CategoryMapper mapper;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.repository = categoryRepository;
        this.mapper = categoryMapper;
    }

    @Override
    public CategoryDto create(CategoryDto categoryDto) {
        try {
            Category category = repository.save(mapper.categoryDtoToCategory(categoryDto));
            log.info("Создана категория: {}", category.toString());
            return mapper.categoryToCategoryDto(category);
        } catch (DataIntegrityViolationException e) {
            throw new AlreadyExistsException("Категория с названием '" +
                    categoryDto.getName() + "' уже существует!");
        }
    }

    @Override
    public CategoryDto update(CategoryDto categoryDto) {
        Category category = getCategory(categoryDto.getId());
        log.info("Обновляем категорию: {}", category.toString());
        if (categoryDto.getName() != null) {
            category.setName(categoryDto.getName());
        }
        category = repository.save(category);
        log.info("Обновлена категория: {}", category.toString());
        return mapper.categoryToCategoryDto(category);
    }

    @Override
    public void delete(Long categoryId) {
        try {
            repository.deleteById(categoryId);
            log.info("Категория с ID={} удалена", categoryId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Category with id=" + categoryId + " was not found.");
        }
    }

    @Override
    public CategoryDto getCategoryById(Long id) {
        return mapper.categoryToCategoryDto(getCategory(id));
    }

    @Override
    public Collection<CategoryDto> getCategories(Integer from, Integer size) {
        Pageable page = getPage(from, size, "id", Sort.Direction.ASC);
        return repository.findAll(page).stream()
                .map(mapper::categoryToCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public Category getCategory(Long categoryId) {
        Category category = repository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Категория с ID=" + categoryId + " не найдена!"));
        return category;
    }
}
