package com.sau.service.impl;

import lombok.RequiredArgsConstructor;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sau.mapper.ArticleCategoryMapper;
import com.sau.mapper.ArticleMapper;
import com.sau.mapper.ArticleTagMapper;
import com.sau.mapper.SysUserMapper;
import com.sau.pojo.DTO.ArticleQueryDTO;
import com.sau.pojo.entity.Article;
import com.sau.pojo.entity.ArticleCategory;
import com.sau.pojo.entity.ArticleTag;
import com.sau.pojo.entity.Category;
import com.sau.pojo.entity.PageResult;
import com.sau.pojo.entity.SysUser;
import com.sau.pojo.entity.Tag;
import com.sau.service.ArticleService;
import com.sau.service.DataPermissionService;
import com.sau.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 文章服务实现类
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ArticleServiceImpl implements ArticleService {
    private final ArticleMapper articleMapper;
    private final ArticleTagMapper articleTagMapper;
    private final ArticleCategoryMapper articleCategoryMapper;
    private final SysUserMapper sysUserMapper;
    private final DataPermissionService dataPermissionService;

    /**
     * 分页查询文章
     */
    @Override
    public PageResult<Article> pageQueryArticles(ArticleQueryDTO articleQueryDTO) {
        // 开启分页并查询当前条件下的文章列表
        try (Page<Article> page = PageHelper.startPage(articleQueryDTO.getPage(), articleQueryDTO.getPageSize())) {
            List<Article> articleList = articleMapper.selectPageArticles(articleQueryDTO);
            return new PageResult<>(page.getTotal(), articleList);
        }
    }

    /**
     * 根据 ID 查询文章详情
     */
    @Override
    public Article getById(Integer id) {
        return articleMapper.selectById(id);
    }

    /**
     * 创建文章
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void create(Article article) {
        // 获取当前登录用户，并校验作者身份是否有效
        Integer currentUserId = SecurityUtils.requireCurrentUserId();
        SysUser currentUser = sysUserMapper.selectById(currentUserId);
        if (currentUser == null) {
            throw new AccessDeniedException("当前用户不存在或已失效");
        }

        // 回填作者信息并保存文章主体
        article.setAuthorId(currentUserId);
        article.setAuthorName(currentUser.getNickname());
        article.setAuthorAvatar(currentUser.getAvatar());
        articleMapper.insert(article);

        // 组装文章和标签的关联关系
        List<Tag> tags = article.getTags();
        List<ArticleTag> articleTags = new ArrayList<>();
        if (!CollectionUtils.isEmpty(tags)) {
            for (Tag tag : tags) {
                ArticleTag articleTag = new ArticleTag();
                articleTag.setArticleId(article.getId());
                articleTag.setTagId(tag.getId());
                articleTags.add(articleTag);
            }
        }
        if (!CollectionUtils.isEmpty(articleTags)) {
            articleTagMapper.insertBatch(articleTags);
        }

        // 组装文章和分类的关联关系
        List<Category> categories = article.getCategories();
        List<ArticleCategory> articleCategories = new ArrayList<>();
        if (!CollectionUtils.isEmpty(categories)) {
            for (Category category : categories) {
                ArticleCategory articleCategory = new ArticleCategory();
                articleCategory.setArticleId(article.getId());
                articleCategory.setCategoryId(category.getId());
                articleCategories.add(articleCategory);
            }
        }
        if (!CollectionUtils.isEmpty(articleCategories)) {
            articleCategoryMapper.insertBatch(articleCategories);
        }
    }

    /**
     * 批量删除文章
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteByIds(List<Integer> ids) {
        // 先校验文章归属权限，再删除文章和关联数据
        dataPermissionService.assertAdminOrArticleOwner(ids);
        articleMapper.deleteByIds(ids);
        articleTagMapper.deleteByArticleIds(ids);
        articleCategoryMapper.deleteByArticleIds(ids);
    }

    /**
     * 查询热门文章
     */
    @Override
    public List<Article> listHot() {
        return articleMapper.listHot();
    }

    /**
     * 查询最多点赞文章
     */
    @Override
    public List<Article> listMostLike() {
        return articleMapper.listMostLike();
    }

    /**
     * 查询最多收藏文章
     */
    @Override
    public List<Article> listMostStar() {
        return articleMapper.listMostStar();
    }

    /**
     * 查询最多分享文章
     */
    @Override
    public List<Article> listMostShare() {
        return articleMapper.listMostShare();
    }
}
