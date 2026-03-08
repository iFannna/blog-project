package com.sau.service;

import com.sau.pojo.DTO.ArticleQueryDTO;
import com.sau.pojo.entity.Article;
import com.sau.pojo.entity.PageResult;

import java.util.List;

/**
 * 文章服务接口。
 */
public interface ArticleService {

    /**
     * 创建文章。
     */
    void create(Article article);

    /**
     * 批量删除文章。
     */
    void deleteByIds(List<Integer> ids);

    /**
     * 分页查询文章。
     */
    PageResult<Article> pageQueryArticles(ArticleQueryDTO articleQueryDTO);

    /**
     * 根据 ID 查询文章详情。
     */
    Article getById(Integer id);

    /**
     * 查询热门文章。
     */
    List<Article> listHot();

    /**
     * 查询最多点赞文章。
     */
    List<Article> listMostLike();

    /**
     * 查询最多收藏文章。
     */
    List<Article> listMostStar();

    /**
     * 查询最多分享文章。
     */
    List<Article> listMostShare();
}