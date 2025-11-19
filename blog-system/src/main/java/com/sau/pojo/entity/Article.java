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
    private List<String> categories;
    // 标签
    private List<String> tags;
    // 作者ID
    private Integer authorId;
    // 作者名称
    private String authorName;
    // 作者头像URL
    private String authorAvatar;
    // 发布时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishAt;
    // 修改时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateAt;
    // 浏览量
    private Integer viewCount;
    // 点赞数
    private Integer likeCount;
    // 评论数
    private Integer commentCount;
    // 文章类型（1：普通文章；2：引用类型文章）
    private Integer type;
    // 引用内容（仅quote类型文章有值）
    private String quoteContent;
    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    // 更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}