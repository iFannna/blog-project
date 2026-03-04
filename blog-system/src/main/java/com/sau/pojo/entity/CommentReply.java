package com.sau.pojo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 评论回复封装类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentReply {
    // 回复ID
    private Integer id;
    // 关联评论ID
    private Integer commentId;
    // 回复用户ID
    private Integer userId;
    // 被回复的用户ID
    private Integer replyToUserId;
    // 回复内容
    private String content;
    // 回复点赞数
    private Integer likeCount;
    // 回复状态：0=被举报，1=正常
    private Integer status;
    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    // 更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}