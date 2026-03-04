package com.sau.service;

import com.sau.pojo.DTO.CommentQueryDTO;
import com.sau.pojo.entity.Article;
import com.sau.pojo.DTO.ArticleQueryDTO;
import com.sau.pojo.entity.Comment;
import com.sau.pojo.entity.PageResult;

import java.util.List;

public interface ArticleService {
    /**
     * 新增文章信息
     */
    void save(Article article);

    /**
     * 批量删除文章信息
     */
    void delete(List<Integer> ids);

    /**
     * 分页查询文章信息列表
     */
    PageResult<Article> pageListArticles(ArticleQueryDTO articleQueryDTO);

    /**
     * 根据ID查询文章信息
     */
    Article getById(Integer id);

    /**
     * 查询热门的文章列表
     */
    List<Article> listHot();

    /**
     * 查询最赞的文章列表
     */
    List<Article> listMostLike();

    /**
     * 查询最Star的文章列表
     */
    List<Article> listMostStar();

    /**
     * 获取最分享的文章列表
     */
    List<Article> listMostShare();


}
