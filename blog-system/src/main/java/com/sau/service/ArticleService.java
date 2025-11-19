package com.sau.service;

import com.sau.pojo.entity.Article;
import com.sau.pojo.entity.ArticleQueryParam;
import com.sau.pojo.entity.PageResult;

public interface ArticleService {
    /**
     * 分页查询文章列表
     */
    PageResult<Article> page(ArticleQueryParam articleQueryParam);

}
