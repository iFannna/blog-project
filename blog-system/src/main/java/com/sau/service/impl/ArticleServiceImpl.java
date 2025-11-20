package com.sau.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sau.mapper.ArticleCatecoryMapper;
import com.sau.mapper.ArticleMapper;
import com.sau.mapper.ArticleTagMapper;
import com.sau.mapper.TagMapper;
import com.sau.pojo.entity.*;
import com.sau.service.ArticleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ArticleTagMapper articleTagMapper;
    @Autowired
    private ArticleCatecoryMapper articleCatecoryMapper;

    /**
     * 分页查询文章列表
     */
    @Override
    public PageResult<Article> page(ArticleQueryParam articleQueryParam) {
        //1.设置分页参数
        try (Page<Article> page = PageHelper.startPage(articleQueryParam.getPage(), articleQueryParam.getPageSize())) {
            //2.执行分页查询
            List<Article> articleList = articleMapper.list(articleQueryParam);
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
    public void add(Article article) {
        // 插入文章基本信息
        article.setCreateTime(LocalDateTime.now());
        article.setUpdateTime(LocalDateTime.now());
        articleMapper.add(article);
        // 插入文章标签关联表
        List<ArticleTag> tags = article.getTags();
        // 循环遍历tags集合，为每个Tag对象设置文章Id 以及创建和修改时间属性
        for (ArticleTag tag : tags) {
            tag.setArticleId(article.getId());
            tag.setCreateTime(LocalDateTime.now());
            tag.setUpdateTime(LocalDateTime.now());
        }
        if(!CollectionUtils.isEmpty(tags)){
            articleTagMapper.add(tags);
        }
        // 插入文章分类关联表
        List<ArticleCatecory> categories = article.getCategories();
        for (ArticleCatecory category : categories) {
            category.setArticleId(article.getId());
            category.setCreateTime(LocalDateTime.now());
            category.setUpdateTime(LocalDateTime.now());
        }
        if(!CollectionUtils.isEmpty(categories)){
            articleCatecoryMapper.add(categories);
        }

    }

}
