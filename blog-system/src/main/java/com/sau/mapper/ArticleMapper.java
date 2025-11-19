package com.sau.mapper;

import com.sau.pojo.entity.Article;
import com.sau.pojo.entity.ArticleQueryParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ArticleMapper {

    /**
     * 分页查询文章列表
     */
    List<Article> list(ArticleQueryParam articleQueryParam);

}
