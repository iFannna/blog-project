package com.sau.service;

import com.sau.pojo.entity.Article;
import com.sau.pojo.DTO.ArticleQueryDTO;
import com.sau.pojo.entity.PageResult;

import java.util.List;

public interface ArticleService {
    /**
     * 分页查询文章信息列表
     */
    PageResult<Article> page(ArticleQueryDTO articleQueryDTO);

    /**
     * 根据ID查询文章信息
     */
    Article getById(Integer id);

    /**
     * 新增文章信息
     */
    void add(Article article);

    /**
     * 批量删除文章信息
     */
    void delete(List<Integer> ids);
}
