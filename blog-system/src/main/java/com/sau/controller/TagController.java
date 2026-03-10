package com.sau.controller;

import com.sau.pojo.entity.Result;
import com.sau.pojo.entity.Tag;
import com.sau.service.TagService;
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
 * 标签相关接口
 */
@Slf4j
@RestController
@RequestMapping("/tag")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    /**
     * 查询全部标签
     */
    @Cacheable(cacheNames = "tagCache")
    @GetMapping
    public Result<List<Tag>> list() {
        log.info("查询全部标签");
        return Result.success(tagService.list());
    }

    /**
     * 创建标签
     */
    @PreAuthorize("hasAuthority('tag:create')")
    @CacheEvict(cacheNames = "tagCache", allEntries = true)
    @PostMapping
    public Result create(@RequestBody Tag tag) {
        log.info("创建标签, 参数:{}", tag.getName());
        tagService.create(tag);
        return Result.success();
    }

    /**
     * 删除标签
     */
    @PreAuthorize("hasAuthority('tag:delete')")
    @CacheEvict(cacheNames = "tagCache", allEntries = true)
    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable Integer id) {
        log.info("删除标签, 参数:{}", id);
        tagService.deleteById(id);
        return Result.success();
    }
}
