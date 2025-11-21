package com.sau.mapper;

import com.sau.pojo.entity.Article;
import com.sau.pojo.DTO.ArticleQueryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArticleMapper {

    /**
     * 分页查询文章信息列表
     */
    List<Article> list(ArticleQueryDTO articleQueryDTO);

    /**
     * 根据ID查询文章信息
     */
    Article getById(Integer id);

    /**
     * 新增文章信息
     */
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void add(Article article);

    /**
     * 批量删除文章信息
     */
    void delete(@Param("ids") List<Integer> ids);
}
