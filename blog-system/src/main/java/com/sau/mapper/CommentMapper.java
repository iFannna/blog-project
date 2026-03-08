package com.sau.mapper;

import com.sau.pojo.DTO.CommentQueryDTO;
import com.sau.pojo.DTO.CommentReplyQueryDTO;
import com.sau.pojo.entity.Comment;
import com.sau.pojo.entity.CommentReply;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 评论数据访问层。
 */
@Mapper
public interface CommentMapper {

    /**
     * 分页查询评论列表。
     */
    List<Comment> selectPageComments(CommentQueryDTO commentQueryDTO);

    /**
     * 分页查询评论回复列表。
     */
    List<CommentReply> selectPageCommentReplies(CommentReplyQueryDTO commentReplyQueryDTO);

    /**
     * 查询评论归属信息。
     */
    @Select("select id, user_id from comment where id = #{id}")
    Comment selectOwnershipById(Integer id);

    /**
     * 根据 ID 删除评论。
     */
    @Delete("delete from comment where id = #{id}")
    void deleteById(Integer id);
}