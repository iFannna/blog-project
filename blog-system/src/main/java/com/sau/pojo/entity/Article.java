package com.sau.pojo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章信息封装类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Article {
    // 文章ID
    private Integer id;
    // 文章标题
    private String title;
    // 文章摘要
    private String summary;
    // 文章链接
    private String url;
    // 封面图片URL
    private String coverImage;
    // 分类
    private List<Category> categories;
    // 标签
    private List<Tag> tags;
    // 作者ID
    private Integer authorId;
    // 作者名称
    private String authorName;
    // 作者头像URL
    private String authorAvatar;
    // 浏览量
    private Integer viewCount;
    // 点赞数
    private Integer likeCount;
    // 收藏数
    private Integer starCount;
    // 转发数
    private Integer shareCount;
    // 评论数
    private Integer commentCount;
    // 文章类型（1：普通文章；2：引用类型文章 ......）
    private Integer type;
    // 内容
    private String Content;
    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    // 更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}