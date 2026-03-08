package com.sau.controller;

import com.sau.pojo.entity.Category;
import com.sau.pojo.entity.Result;
import com.sau.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 分类相关接口。
 */
@Slf4j
@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 查询全部分类。
     */
    @Cacheable(cacheNames = "categoryCache")
    @GetMapping
    public Result<List<Category>> list() {
        log.info("查询全部分类");
        return Result.success(categoryService.list());
    }

    /**
     * 创建分类。
     */
    @PreAuthorize("hasAuthority('category:create')")
    @CacheEvict(cacheNames = "categoryCache", allEntries = true)
    @PostMapping
    public Result create(@RequestBody Category category) {
        log.info("创建分类, 参数:{}", category.getName());
        categoryService.create(category);
        return Result.success();
    }

    /**
     * 删除分类。
     */
    @PreAuthorize("hasAuthority('category:delete')")
    @CacheEvict(cacheNames = "categoryCache", allEntries = true)
    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable Integer id) {
        log.info("删除分类, 参数:{}", id);
        categoryService.deleteById(id);
        return Result.success();
    }
}