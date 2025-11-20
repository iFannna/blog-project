package com.sau.controller;

import com.sau.pojo.entity.Category;
import com.sau.pojo.entity.Result;
import com.sau.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类相关接口
 */
@Slf4j
@RestController
@RequestMapping("/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    /**
     * 查询所有分类
     */
    @GetMapping
    public Result<List<Category>> list() {
        log.info("查询所有分类");
        return Result.success(categoryService.list());
    }

    /**
     * 新增分类
     */
    @PostMapping
    public Result add(@RequestBody Category category) {
        log.info("新增分类：{}", category.getName());
        categoryService.add(category);
        return Result.success();
    }

    /**
     * 根据id删除分类
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        log.info("删除分类：{}", id);
        categoryService.delete(id);
        return Result.success();
    }

}
