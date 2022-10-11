package ru.practicum.ewm.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.dto.NewCategoryDto;
import ru.practicum.ewm.facade.UserEventFacade;
import ru.practicum.ewm.mapper.CategoryMapper;
import ru.practicum.ewm.service.CategoryService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(path = "/admin/categories")
public class AdminCategoryController {
    private final CategoryService categoryService;

    private final CategoryMapper categoryMapper;

    private final UserEventFacade userEventFacade;

    @Autowired
    public AdminCategoryController(CategoryService categoryService, CategoryMapper categoryMapper,
                                   UserEventFacade userEventFacade) {
        this.categoryService = categoryService;
        this.categoryMapper = categoryMapper;
        this.userEventFacade = userEventFacade;
    }

    @PostMapping()
    public CategoryDto create(@Valid @RequestBody NewCategoryDto newCategoryDto, HttpServletRequest request) {
        log.info("{}: Запрос к эндпоинту '{}' на добавление категории {}",
                request.getRemoteAddr(),
                request.getRequestURI(),
                newCategoryDto.toString());
        return categoryService.create(categoryMapper.newCategoryDtoToCategoryDto(newCategoryDto));
    }

    @PatchMapping()
    public CategoryDto update(@Valid @RequestBody CategoryDto categoryDto, HttpServletRequest request) {
        log.info("{}: Запрос к эндпоинту '{}' на изменение категории {}",
                request.getRemoteAddr(),
                request.getRequestURI(),
                categoryDto.toString());
        return categoryService.update(categoryDto);
    }

    @DeleteMapping("/{categoryId}")
    public void delete(@PathVariable Long categoryId, HttpServletRequest request) {
        log.info("{}: Запрос к эндпоинту '{}' на удаление категории с ID={}",
                request.getRemoteAddr(),
                request.getRequestURI(),
                categoryId);
        userEventFacade.deleteCategory(categoryId);
    }
}
