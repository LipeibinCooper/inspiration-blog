package com.inspiration.backend.controller;

import com.inspiration.backend.entity.InspirationNode;
import com.inspiration.backend.model.Result;
import com.inspiration.backend.service.InspirationNodeService;
import com.inspiration.backend.service.FileService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "灵感节点管理", description = "灵感树节点的增删改查接口")
@RestController
@RequestMapping("/api/nodes")
@RequiredArgsConstructor(onConstructor_ = {@Lazy})

public class InspirationNodeController extends BaseController {

    private final InspirationNodeService nodeService;
    private final FileService fileService;

    @Operation(summary = "创建节点")
    @PostMapping
    public Result<InspirationNode> create(@Valid @RequestBody InspirationNode node) {
        return success(nodeService.create(node));
    }

    @Operation(summary = "获取节点详情")
    @GetMapping("/{id}")
    public Result<InspirationNode> getById(@Parameter(description = "节点ID") @PathVariable Long id) {
        return success(nodeService.findById(id));
    }

    @Operation(summary = "获取灵感的所有节点")
    @GetMapping("/inspiration/{inspirationId}")
    public Result<List<InspirationNode>> getByInspirationId(
            @Parameter(description = "灵感ID") @PathVariable Long inspirationId) {
        return success(nodeService.findByInspirationId(inspirationId));
    }

    @Operation(summary = "获取子节点列表")
    @GetMapping("/parent/{parentId}")
    public Result<List<InspirationNode>> getByParentId(
            @Parameter(description = "父节点ID") @PathVariable Long parentId) {
        return success(nodeService.findByParentId(parentId));
    }

    @Operation(summary = "更新节点")
    @PutMapping("/{id}")
    public Result<?> update(
            @Parameter(description = "节点ID") @PathVariable Long id,
            @Valid @RequestBody InspirationNode node) {
        node.setId(id);
        nodeService.update(node);
        return success();
    }

    @Operation(summary = "删除节点")
    @DeleteMapping("/{id}")
    public Result<?> delete(@Parameter(description = "节点ID") @PathVariable Long id) {
        nodeService.delete(id);
        return success();
    }

    @Operation(summary = "上传节点图片")
    @PostMapping("/{id}/images")
    public Result<?> addImage(
            @Parameter(description = "节点ID") @PathVariable Long id,
            @Parameter(description = "图片文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "是否设为缩略图") @RequestParam(value = "isThumbnail", defaultValue = "false") boolean isThumbnail) {
        String imageUrl = fileService.uploadFile(file, "nodes");
        nodeService.addImage(id, imageUrl, isThumbnail);
        return success();
    }

    @Operation(summary = "删除节点图片")
    @DeleteMapping("/{nodeId}/images/{imageId}")
    public Result<?> removeImage(
            @Parameter(description = "节点ID") @PathVariable Long nodeId,
            @Parameter(description = "图片ID") @PathVariable Long imageId) {
        nodeService.removeImage(nodeId, imageId);
        return success();
    }

    @Operation(summary = "设置缩略图")
    @PutMapping("/{nodeId}/images/{imageId}/thumbnail")
    public Result<?> setThumbnail(
            @Parameter(description = "节点ID") @PathVariable Long nodeId,
            @Parameter(description = "图片ID") @PathVariable Long imageId) {
        nodeService.setThumbnail(nodeId, imageId);
        return success();
    }
} 