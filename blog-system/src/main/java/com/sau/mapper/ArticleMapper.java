package com.sau.mapper;

import com.sau.pojo.DTO.ArticleQueryDTO;
import com.sau.pojo.entity.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文章数据访问层
 */
@Mapper
public interface ArticleMapper {

    /**
     * 分页查询文章列表
     */
    List<Article> selectPageArticles(ArticleQueryDTO articleQueryDTO);

    /**
     * 根据 ID 查询文章详情
     */
    Article selectById(Integer id);

    /**
     * 新增文章
     */
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Article article);

    /**
     * 批量删除文章
     */
    void deleteByIds(@Param("ids") List<Integer> ids);

    /**
     * 查询文章归属信息
     */
    List<Article> selectOwnershipByIds(@Param("ids") List<Integer> ids);

    /**
     * 查询热门文章
     */
    List<Article> listHot();

    /**
     * 查询最多点赞文章
     */
    List<Article> listMostLike();

    /**
     * 查询最多收藏文章
     */
    List<Article> listMostStar();

    /**
     * 查询最多分享文章
     */
    List<Article> listMostShare();
}
