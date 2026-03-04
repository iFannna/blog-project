package com.sau.mapper;

import com.sau.pojo.DTO.CommentQueryDTO;
import com.sau.pojo.entity.Article;
import com.sau.pojo.DTO.ArticleQueryDTO;
import com.sau.pojo.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArticleMapper {

    /**
     * 分页查询文章信息列表
     */
    List<Article> pageListArticles(ArticleQueryDTO articleQueryDTO);

    /**
     * 根据ID查询文章信息
     */
    Article getById(Integer id);

    /**
     * 新增文章信息
     */
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void save(Article article);

    /**
     * 批量删除文章信息
     */
    void delete(@Param("ids") List<Integer> ids);

    /**
     * 查询最热门的文章
     */
    List<Article> listHot();

    /**
     * 查询最赞的文章
     */
    List<Article> listMostLike();

    /**
     * 查询最Star的文章
     */
    List<Article> listMostStar();

    /**
     * 获取最分享的文章
     */
    List<Article> listMostShare();


}
