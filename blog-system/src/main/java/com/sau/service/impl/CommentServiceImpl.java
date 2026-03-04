package com.sau.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sau.mapper.CommentMapper;
import com.sau.pojo.DTO.CommentQueryDTO;
import com.sau.pojo.DTO.CommentReplyQueryDTO;
import com.sau.pojo.entity.Comment;
import com.sau.pojo.entity.CommentReply;
import com.sau.pojo.entity.PageResult;
import com.sau.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    /**
     * 分页查询评论
     */
    @Override
    public PageResult<Comment> pageListComments(CommentQueryDTO commentQueryDTO) {
        //1.设置分页参数
        try (Page<Comment> page = PageHelper.startPage(commentQueryDTO.getPage(), commentQueryDTO.getPageSize())) {
            //2.执行分页查询
            List<Comment> commentList = commentMapper.pageListComments(commentQueryDTO);
            //3.解析封装结果
            return new PageResult<Comment>(page.getTotal(), page.getResult());
        }
    }

    /**
     * 分页查询评论回复
     */
    @Override
    public PageResult<CommentReply> pageListCommentReplies(CommentReplyQueryDTO commentReplyQueryDTO) {
        //1.设置分页参数
        try (Page<CommentReply> page = PageHelper.startPage(commentReplyQueryDTO.getPage(), commentReplyQueryDTO.getPageSize())) {
            //2.执行分页查询
            List<CommentReply> commentReplyList = commentMapper.pageListCommentReplies(commentReplyQueryDTO);
            //3.解析封装结果
            return new PageResult<CommentReply>(page.getTotal(), page.getResult());
        }
    }

    /**
     * 删除评论
     */
    @Override
    public void delete(Integer id) {
        commentMapper.delete(id);
    }
}
