package com.inspiration.backend.controller;

import com.inspiration.backend.entity.Inspiration;
import com.inspiration.backend.model.Result;
import com.inspiration.backend.service.InspirationService;
import com.inspiration.backend.service.UserInteractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "灵感管理", description = "灵感的增删改查接口")
@RestController
@RequestMapping("/api/inspirations")
@RequiredArgsConstructor
public class InspirationController extends BaseController {

    private final InspirationService inspirationService;
    private final UserInteractionService interactionService;

    @Operation(summary = "创建灵感")
    @PostMapping
    public Result<Inspiration> create(@Valid @RequestBody InspirationRequest request) {
        Inspiration inspiration = new Inspiration();
        inspiration.setTitle(request.getTitle());
        inspiration.setContent(request.getContent());
        inspiration.setIsPublic(request.getIsPublic());
        inspiration.setUserId(getCurrentUserId());
        return success(inspirationService.create(inspiration));
    }

    @Operation(summary = "获取灵感详情")
    @GetMapping("/{id}")
    public Result<Inspiration> getById(@Parameter(description = "灵感ID") @PathVariable Long id) {
        return success(inspirationService.findById(id));
    }

    @Operation(summary = "获取用户的灵感列表")
    @GetMapping("/user/{userId}")
    public Result<List<Inspiration>> getByUserId(@Parameter(description = "用户ID") @PathVariable Long userId) {
        return success(inspirationService.findByUserId(userId));
    }

    @Operation(summary = "获取公开灵感列表")
    @GetMapping("/public")
    public Result<List<Inspiration>> getPublic() {
        return success(inspirationService.findPublic());
    }

    @Operation(summary = "更新灵感")
    @PutMapping("/{id}")
    public Result<?> update(
            @Parameter(description = "灵感ID") @PathVariable Long id,
            @Valid @RequestBody InspirationRequest request) {
        Inspiration inspiration = new Inspiration();
        inspiration.setId(id);
        inspiration.setUserId(getCurrentUserId());
        inspiration.setTitle(request.getTitle());
        inspiration.setContent(request.getContent());
        inspiration.setIsPublic(request.getIsPublic());
        inspirationService.update(inspiration);
        return success();
    }

    @Operation(summary = "删除灵感")
    @DeleteMapping("/{id}")
    public Result<?> delete(@Parameter(description = "灵感ID") @PathVariable Long id) {
        inspirationService.delete(id);
        return success();
    }

    @Operation(summary = "点赞/取消点赞")
    @PostMapping("/{id}/like")
    public Result<?> toggleLike(@Parameter(description = "灵感ID") @PathVariable Long id) {
        interactionService.toggleLike(getCurrentUserId(), id);
        return success();
    }

    @Operation(summary = "收藏/取消收藏")
    @PostMapping("/{id}/collect")
    public Result<?> toggleCollection(@Parameter(description = "灵感ID") @PathVariable Long id) {
        interactionService.toggleCollection(getCurrentUserId(), id);
        return success();
    }
} 