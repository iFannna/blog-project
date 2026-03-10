package com.sau.service;

import com.sau.pojo.DTO.CommentQueryDTO;
import com.sau.pojo.DTO.CommentReplyQueryDTO;
import com.sau.pojo.entity.Comment;
import com.sau.pojo.entity.CommentReply;
import com.sau.pojo.entity.PageResult;

/**
 * 评论服务接口
 */
public interface CommentService {

    /**
     * 分页查询评论
     */
    PageResult<Comment> pageQueryComments(CommentQueryDTO commentQueryDTO);

    /**
     * 分页查询评论回复
     */
    PageResult<CommentReply> pageQueryCommentReplies(CommentReplyQueryDTO commentReplyQueryDTO);

    /**
     * 根据 ID 删除评论
     */
    void deleteById(Integer id);
}
