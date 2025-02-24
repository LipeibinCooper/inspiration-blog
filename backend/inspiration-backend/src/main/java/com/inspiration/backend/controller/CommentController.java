package com.inspiration.backend.controller;


import com.inspiration.backend.dto.request.CommentRequest;
import com.inspiration.backend.entity.Comment;
import com.inspiration.backend.model.Result;
import com.inspiration.backend.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;

import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import java.util.List;

@Tag(name = "评论管理", description = "评论的增删改查接口")
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor(onConstructor_ = {@Lazy})

public class CommentController extends BaseController {

    private final CommentService commentService;

    @Operation(summary = "创建评论")
    @PostMapping
    public Result<Comment> create(@Valid @RequestBody CommentRequest request) {
        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setInspirationId(request.getInspirationId());
        comment.setUserId(getCurrentUserId());
        return success(commentService.create(comment));
    }

    @Operation(summary = "获取灵感的评论列表")
    @GetMapping("/inspiration/{inspirationId}")
    public Result<List<Comment>> getByInspirationId(
            @Parameter(description = "灵感ID") @PathVariable Long inspirationId) {
        return success(commentService.findByInspirationId(inspirationId));
    }

    @Operation(summary = "获取用户的评论列表")
    @GetMapping("/user/{userId}")
    public Result<List<Comment>> getByUserId(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        return success(commentService.findByUserId(userId));
    }

    @Operation(summary = "删除评论")
    @DeleteMapping("/{id}")
    public Result<?> delete(@Parameter(description = "评论ID") @PathVariable Long id) {
        commentService.delete(id, getCurrentUserId());
        return success();
    }
} 