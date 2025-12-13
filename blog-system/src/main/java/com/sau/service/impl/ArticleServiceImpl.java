package com.sau.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sau.mapper.ArticleCategoryMapper;
import com.sau.mapper.ArticleMapper;
import com.sau.mapper.ArticleTagMapper;
import com.sau.pojo.DTO.ArticleQueryDTO;
import com.sau.pojo.entity.*;
import com.sau.service.ArticleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ArticleTagMapper articleTagMapper;
    @Autowired
    private ArticleCategoryMapper articleCategoryMapper;

    /**
     * 分页查询文章列表
     */
    @Override
    public PageResult<Article> page(ArticleQueryDTO articleQueryDTO) {
        //1.设置分页参数
        try (Page<Article> page = PageHelper.startPage(articleQueryDTO.getPage(), articleQueryDTO.getPageSize())) {
            //2.执行分页查询
            List<Article> articleList = articleMapper.list(articleQueryDTO);
            //3.解析封装结果
            return new PageResult<Article>(page.getTotal(), page.getResult());
        }
    }

    /**
     * 根据ID查询文章信息
     */
    @Override
    public Article getById(Integer id) {
        return articleMapper.getById(id);
    }

    /**
     * 新增文章信息
     */
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void save(Article article) {
        // 插入文章基本信息
        article.setCreateTime(LocalDateTime.now());
        article.setUpdateTime(LocalDateTime.now());
        articleMapper.save(article);

        // 2. 处理文章标签关联表
        List<Tag> tags = article.getTags();
        List<ArticleTag> articleTags = new ArrayList<>();
        // 判空后遍历
        if (!CollectionUtils.isEmpty(tags)) {
            for (Tag tag : tags) {
                ArticleTag articleTag = new ArticleTag();
                articleTag.setArticleId(article.getId());
                articleTag.setTagId(tag.getId());
                articleTag.setCreateTime(LocalDateTime.now());
                articleTag.setUpdateTime(LocalDateTime.now());
                articleTags.add(articleTag); // 添加到关联表列表
            }
        }
        // 批量插入标签关联数据
        if (!CollectionUtils.isEmpty(articleTags)) {
            articleTagMapper.save(articleTags);
        }

        // 3. 处理文章分类关联表
        List<Category> categories = article.getCategories();
        List<ArticleCategory> articleCategories = new ArrayList<>();
        // 判空后遍历
        if (!CollectionUtils.isEmpty(categories)) {
            for (Category category : categories) {
                ArticleCategory articleCategory = new ArticleCategory();
                articleCategory.setArticleId(article.getId());
                articleCategory.setCategoryId(category.getId());
                articleCategory.setCreateTime(LocalDateTime.now());
                articleCategory.setUpdateTime(LocalDateTime.now());
                articleCategories.add(articleCategory); // 添加到关联表列表
            }
        }
        // 批量插入分类关联数据
        if (!CollectionUtils.isEmpty(articleCategories)) {
            articleCategoryMapper.save(articleCategories);
        }

    }

    /**
     * 批量删除文章信息
     */
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void delete(List<Integer> ids) {
        // 批量删除文章基本信息
        articleMapper.delete(ids);
        // 批量删除文章标签关联表
        articleTagMapper.deleteByArticleIds(ids);
        // 批量删除文章分类关联表
        articleCategoryMapper.deleteByArticleIds(ids);
    }

    /**
     * 获取热门文章信息
     */
    @Override
    public List<Article> listHot() {
        return articleMapper.listHot();
    }

    /**
     * 获取最赞的文章信息
     */
    @Override
    public List<Article> listMostLike() {
        return articleMapper.listMostLike();
    }

    /**
     * 获取最Star的文章信息
     */
    @Override
    public List<Article> listMostStar() {
        return articleMapper.listMostStar();
    }

    /**
     * 获取最分享的文章信息
     */
    @Override
    public List<Article> listMostShare() {
        return articleMapper.listMostShare();
    }

}
