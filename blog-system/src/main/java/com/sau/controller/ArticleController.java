package com.sau.controller;

import com.sau.annotation.CacheEvictByPrefix;
import com.sau.pojo.entity.Article;
import com.sau.pojo.DTO.ArticleQueryDTO;
import com.sau.pojo.entity.PageResult;
import com.sau.pojo.entity.Result;
import com.sau.service.ArticleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文章相关接口
 */
@Slf4j
@RestController
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    /**
     * 分页查询文章信息
     */
    @Cacheable(cacheNames = "articlePageCache", key = "#articleQueryDTO")
    @GetMapping
    public Result<PageResult<Article>> pageListArticles(ArticleQueryDTO articleQueryDTO) {
        log.info("分页查询,参数:{}", articleQueryDTO);
        PageResult<Article> pageResult = articleService.pageListArticles(articleQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据id查询文章信息
     */
    @Cacheable(cacheNames = "articleCache", key = "#id")
    @GetMapping("/{id}")
    public Result<Article> getById(@PathVariable Integer id) {
        log.info("根据id查询文章,参数:{}", id);
        Article article = articleService.getById(id);
        return Result.success(article);
    }

    /**
     * 新增文章信息
     */
    @CacheEvictByPrefix("article")
    @PostMapping
    public Result save(@RequestBody Article article) {
        log.info("新增文章,参数:{}", article);
        articleService.save(article);
        return Result.success();
    }

    /**
     * 批量删除文章信息
     */
    @CacheEvictByPrefix("article")
    @DeleteMapping
    public Result delete(@RequestParam("ids") List<Integer> ids) {
        log.info("批量删除文章,参数:{}", ids);
        articleService.delete(ids);
        return Result.success();
    }

    /**
     * 获取热门文章信息
     */
    @Cacheable(cacheNames = "articleHotCache")
    @GetMapping("/hot")
    public Result<List<Article>> listHot() {
        log.info("查询浏览量高的文章信息");
        List<Article> articleList = articleService.listHot();
        return Result.success(articleList);
    }

    /**
     * 获取点赞数较多的文章信息
     */
    @Cacheable(cacheNames = "articleMostLikeCache")
    @GetMapping("/mostLike")
    public Result<List<Article>> listMostLike() {
        log.info("查询点赞量高的文章信息");
        List<Article> articleList = articleService.listMostLike();
        return Result.success(articleList);
    }

    /**
     * 获取收藏数较多的文章信息
     */
    @Cacheable(cacheNames = "articleMostStarCache")
    @GetMapping("/mostStar")
    public Result<List<Article>> listMostStar() {
        log.info("查询收藏量高的文章信息");
        List<Article> articleList = articleService.listMostStar();
        return Result.success(articleList);
    }

    /**
     * 获取转发数较多的文章信息
     */
    @Cacheable(cacheNames = "articleMostShareCache")
    @GetMapping("/mostShare")
    public Result<List<Article>> listMostShare() {
        log.info("查询转发量高的文章信息");
        List<Article> articleList = articleService.listMostShare();
        return Result.success(articleList);
    }


}
