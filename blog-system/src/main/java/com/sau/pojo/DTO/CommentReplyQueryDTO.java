package com.sau.pojo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentReplyQueryDTO {
    // 页码 默认为 1
    private Integer page = 1;
    // 每页展示记录数 默认为 10
    private Integer pageSize = 10;
    // 评论ID
    private Integer commentId;
}
