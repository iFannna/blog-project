package com.sau.controller;

import com.sau.pojo.DTO.CommentQueryDTO;
import com.sau.pojo.DTO.CommentReplyQueryDTO;
import com.sau.pojo.entity.Comment;
import com.sau.pojo.entity.CommentReply;
import com.sau.pojo.entity.PageResult;
import com.sau.pojo.entity.Result;
import com.sau.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 评论相关接口
 */

@Slf4j
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 分页查询文章评论
     */
    @GetMapping()
    public Result<PageResult<Comment>> pageListComments(CommentQueryDTO commentQueryDTO){
        log.info("分页查询,参数:{}", commentQueryDTO);
        PageResult<Comment> pageResult = commentService.pageListComments(commentQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 分页查询文章评论回复
     */
    @GetMapping("/reply")
    public Result<PageResult<CommentReply>> pageListCommentReplies(CommentReplyQueryDTO commentReplyQueryDTO){
        log.info("分页查询,参数:{}", commentReplyQueryDTO);
        PageResult<CommentReply> pageResult = commentService.pageListCommentReplies(commentReplyQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据id删除评论
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id){
        log.info("删除评论,参数:{}", id);
        commentService.delete(id);
        return Result.success();
    }
}
