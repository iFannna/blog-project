package com.sau.controller;

import lombok.RequiredArgsConstructor;

import com.sau.pojo.DTO.CommentQueryDTO;
import com.sau.pojo.DTO.CommentReplyQueryDTO;
import com.sau.pojo.entity.Comment;
import com.sau.pojo.entity.CommentReply;
import com.sau.pojo.entity.PageResult;
import com.sau.pojo.entity.Result;
import com.sau.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 评论相关接口
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;

    /**
     * 分页查询评论
     */
    @GetMapping
    public Result<PageResult<Comment>> pageQueryComments(CommentQueryDTO commentQueryDTO) {
        log.info("分页查询评论, 参数:{}", commentQueryDTO);
        PageResult<Comment> pageResult = commentService.pageQueryComments(commentQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 分页查询评论回复
     */
    @GetMapping("/reply")
    public Result<PageResult<CommentReply>> pageQueryCommentReplies(CommentReplyQueryDTO commentReplyQueryDTO) {
        log.info("分页查询评论回复, 参数:{}", commentReplyQueryDTO);
        PageResult<CommentReply> pageResult = commentService.pageQueryCommentReplies(commentReplyQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据 ID 删除评论
     */
    @PreAuthorize("hasAuthority('comment:delete')")
    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable Integer id) {
        log.info("删除评论, 参数:{}", id);
        commentService.deleteById(id);
        return Result.success();
    }
}
