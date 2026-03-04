package com.sau.service;


import com.sau.pojo.DTO.CommentQueryDTO;
import com.sau.pojo.DTO.CommentReplyQueryDTO;
import com.sau.pojo.entity.Comment;
import com.sau.pojo.entity.CommentReply;
import com.sau.pojo.entity.PageResult;

public interface CommentService {
    /**
     * 分页查询文章评论
     */
    PageResult<Comment> pageListComments(CommentQueryDTO commentQueryDTO);

    /**
     * 分页查询文章评论回复
     */
    PageResult<CommentReply> pageListCommentReplies(CommentReplyQueryDTO commentReplyQueryDTO);

    /**
     * 删除文章评论
     */
    void delete(Integer id);
}
