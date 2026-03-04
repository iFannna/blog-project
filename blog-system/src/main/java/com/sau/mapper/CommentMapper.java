package com.sau.mapper;

import com.sau.pojo.DTO.CommentQueryDTO;
import com.sau.pojo.DTO.CommentReplyQueryDTO;
import com.sau.pojo.entity.Comment;
import com.sau.pojo.entity.CommentReply;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {
    /**
     * 分页查询文章评论
     */
    List<Comment> pageListComments(CommentQueryDTO commentQueryDTO);

    /**
     * 分页查询文章评论回复
     */
    List<CommentReply> pageListCommentReplies(CommentReplyQueryDTO commentReplyQueryDTO);

    /**
     * 删除文章评论
     */
    @Delete("delete from comment where id = #{id}")
    void delete(Integer id);
}
