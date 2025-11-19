package com.sau.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sau.mapper.ArticleMapper;
import com.sau.pojo.entity.Article;
import com.sau.pojo.entity.ArticleQueryParam;
import com.sau.pojo.entity.PageResult;
import com.sau.service.ArticleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

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
}
