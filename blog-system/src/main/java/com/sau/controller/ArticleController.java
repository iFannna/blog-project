package com.sau.controller;

import com.sau.pojo.entity.Article;
import com.sau.pojo.entity.ArticleQueryParam;
import com.sau.pojo.entity.PageResult;
import com.sau.pojo.entity.Result;
import com.sau.service.ArticleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 文章类接口
 */
@Slf4j
@RestController
@RequestMapping("/articles")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    /**
     * 分页查询文章
     */
    @GetMapping
    public Result<PageResult<Article>> page(ArticleQueryParam articleQueryParam){
        log.info("分页查询,参数:{}", articleQueryParam);
        PageResult<Article> pageResult = articleService.page(articleQueryParam);
        return Result.success(pageResult);
    }



}
