package com.sau.service.impl;

import lombok.RequiredArgsConstructor;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sau.mapper.CommentMapper;
import com.sau.pojo.DTO.CommentQueryDTO;
import com.sau.pojo.DTO.CommentReplyQueryDTO;
import com.sau.pojo.entity.Comment;
import com.sau.pojo.entity.CommentReply;
import com.sau.pojo.entity.PageResult;
import com.sau.service.CommentService;
import com.sau.service.DataPermissionService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 评论服务实现类
 */
@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {
    private final CommentMapper commentMapper;
    private final DataPermissionService dataPermissionService;

    /**
     * 分页查询评论
     */
    @Override
    public PageResult<Comment> pageQueryComments(CommentQueryDTO commentQueryDTO) {
        // 开启分页并查询评论列表
        try (Page<Comment> page = PageHelper.startPage(commentQueryDTO.getPage(), commentQueryDTO.getPageSize())) {
            List<Comment> commentList = commentMapper.selectPageComments(commentQueryDTO);
            return new PageResult<>(page.getTotal(), commentList);
        }
    }

    /**
     * 分页查询评论回复
     */
    @Override
    public PageResult<CommentReply> pageQueryCommentReplies(CommentReplyQueryDTO commentReplyQueryDTO) {
        // 开启分页并查询评论回复列表
        try (Page<CommentReply> page = PageHelper.startPage(commentReplyQueryDTO.getPage(), commentReplyQueryDTO.getPageSize())) {
            List<CommentReply> commentReplyList = commentMapper.selectPageCommentReplies(commentReplyQueryDTO);
            return new PageResult<>(page.getTotal(), commentReplyList);
        }
    }

    /**
     * 根据 ID 删除评论
     */
    @Override
    public void deleteById(Integer id) {
        // 先校验评论归属权限，再执行删除
        dataPermissionService.assertAdminOrCommentOwner(id);
        commentMapper.deleteById(id);
    }
}
