package com.sau.controller;

import com.sau.pojo.entity.Article;
import com.sau.pojo.entity.ArticleQueryParam;
import com.sau.pojo.entity.PageResult;
import com.sau.pojo.entity.Result;
import com.sau.service.ArticleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 文章相关接口
 */
@Slf4j
@RestController
@RequestMapping("/articles")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    /**
     * 分页查询文章信息
     */
    @GetMapping
    public Result<PageResult<Article>> page(ArticleQueryParam articleQueryParam) {
        log.info("分页查询,参数:{}", articleQueryParam);
        PageResult<Article> pageResult = articleService.page(articleQueryParam);
        return Result.success(pageResult);
    }

    /**
     * 根据id查询文章信息
     */
    @GetMapping("/{id}")
    public Result<Article> getById(@PathVariable Integer id) {
        log.info("根据id查询文章,参数:{}", id);
        Article article = articleService.getById(id);
        return Result.success(article);
    }

    /**
     * 新增文章信息
     */
    @PostMapping
    public Result add(@RequestBody Article article) {
        log.info("新增文章,参数:{}", article);
        articleService.add(article);
        return Result.success();
    }


}
