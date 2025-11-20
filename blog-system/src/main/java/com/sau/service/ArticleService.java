package com.sau.service;

import com.sau.pojo.entity.Article;
import com.sau.pojo.entity.ArticleQueryParam;
import com.sau.pojo.entity.PageResult;

public interface ArticleService {
    /**
     * 分页查询文章信息列表
     */
    PageResult<Article> page(ArticleQueryParam articleQueryParam);

    /**
     * 根据ID查询文章信息
     */
    Article getById(Integer id);

    /**
     * 新增文章信息
     */
    void add(Article article);
}
