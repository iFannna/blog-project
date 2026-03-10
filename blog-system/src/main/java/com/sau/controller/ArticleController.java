package com.sau.controller;

import lombok.RequiredArgsConstructor;

import com.sau.annotation.CacheEvictByPrefix;
import com.sau.pojo.DTO.ArticleQueryDTO;
import com.sau.pojo.entity.Article;
import com.sau.pojo.entity.PageResult;
import com.sau.pojo.entity.Result;
import com.sau.service.ArticleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 文章相关接口
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/article")
public class ArticleController {
    private final ArticleService articleService;

    /**
     * 分页查询文章
     */
    @Cacheable(cacheNames = "articlePageCache", key = "#articleQueryDTO")
    @GetMapping
    public Result<PageResult<Article>> pageQueryArticles(ArticleQueryDTO articleQueryDTO) {
        log.info("分页查询文章, 参数:{}", articleQueryDTO);
        PageResult<Article> pageResult = articleService.pageQueryArticles(articleQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据 ID 查询文章详情
     */
    @Cacheable(cacheNames = "articleCache", key = "#id")
    @GetMapping("/{id}")
    public Result<Article> getById(@PathVariable Integer id) {
        log.info("根据 id 查询文章, 参数:{}", id);
        Article article = articleService.getById(id);
        return Result.success(article);
    }

    /**
     * 创建文章
     */
    @PreAuthorize("hasAuthority('article:create')")
    @CacheEvictByPrefix("article")
    @PostMapping
    public Result create(@RequestBody Article article) {
        log.info("创建文章, 参数:{}", article);
        articleService.create(article);
        return Result.success();
    }

    /**
     * 批量删除文章
     */
    @PreAuthorize("hasAuthority('article:delete')")
    @CacheEvictByPrefix("article")
    @DeleteMapping
    public Result deleteByIds(@RequestParam("ids") List<Integer> ids) {
        log.info("批量删除文章, 参数:{}", ids);
        articleService.deleteByIds(ids);
        return Result.success();
    }

    /**
     * 查询热门文章
     */
    @Cacheable(cacheNames = "articleHotCache")
    @GetMapping("/hot")
    public Result<List<Article>> listHot() {
        return Result.success(articleService.listHot());
    }

    /**
     * 查询最多点赞文章
     */
    @Cacheable(cacheNames = "articleMostLikeCache")
    @GetMapping("/mostLike")
    public Result<List<Article>> listMostLike() {
        return Result.success(articleService.listMostLike());
    }

    /**
     * 查询最多收藏文章
     */
    @Cacheable(cacheNames = "articleMostStarCache")
    @GetMapping("/mostStar")
    public Result<List<Article>> listMostStar() {
        return Result.success(articleService.listMostStar());
    }

    /**
     * 查询最多分享文章
     */
    @Cacheable(cacheNames = "articleMostShareCache")
    @GetMapping("/mostShare")
    public Result<List<Article>> listMostShare() {
        return Result.success(articleService.listMostShare());
    }
}
