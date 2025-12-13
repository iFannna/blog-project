package com.sau.controller;

import com.sau.pojo.entity.Result;
import com.sau.pojo.entity.Tag;
import com.sau.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 标签相关接口
 */
@Slf4j
@RestController
@RequestMapping("/tags")
public class TagController {

    @Autowired
    private TagService tagService;

    /**
     * 查询所有标签
     */
    @Cacheable(cacheNames = "tagCache")
    @GetMapping
    public Result<List<Tag>> list() {
        log.info("查询所有标签");
        return Result.success(tagService.list());
    }

    /**
     * 新增标签
     */
    @CacheEvict(cacheNames = "tagCache", allEntries = true)
    @PostMapping
    public Result save(@RequestBody Tag tag) {
        log.info("新增标签：{}", tag.getName());
        tagService.save(tag);
        return Result.success();
    }
    /**
     * 根据id删除标签
     */
    @CacheEvict(cacheNames = "tagCache", allEntries = true)
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        log.info("删除标签：{}", id);
        tagService.delete(id);
        return Result.success();
    }
}
