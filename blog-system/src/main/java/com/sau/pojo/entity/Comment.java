package com.sau.pojo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 文章评论封装类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    // 评论ID
    private Integer id;
    // 关联文章ID
    private Integer articleId;
    // 评论用户ID
    private Integer userId;
    // 评论内容
    private String content;
    // 评论点赞数
    private Integer likeCount;
    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    // 更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}