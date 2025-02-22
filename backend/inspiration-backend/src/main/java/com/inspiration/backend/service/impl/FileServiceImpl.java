package com.inspiration.backend.service.impl;

import com.aliyun.oss.OSS;
import com.inspiration.backend.config.OssConfig;
import com.inspiration.backend.exception.BusinessException;
import com.inspiration.backend.service.FileService;
import com.inspiration.backend.message.EventMessage;
import com.inspiration.backend.message.MessageProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final OSS ossClient;
    private final OssConfig ossConfig;
    private final MessageProducer messageProducer;

    @Override
    public String uploadFile(MultipartFile file, String directory) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new BusinessException("文件名不能为空");
        }

        // 验证文件类型
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        if (!Arrays.asList(".jpg", ".jpeg", ".png", ".gif").contains(extension)) {
            throw new BusinessException("只支持jpg、jpeg、png、gif格式的图片");
        }

        // 验证文件大小（5MB）
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new BusinessException("文件大小不能超过5MB");
        }

        String objectName = directory + "/" + UUID.randomUUID() + extension;

        try {
            ossClient.putObject(ossConfig.getBucketName(), objectName, file.getInputStream());
            String fileUrl = ossConfig.getUrlPrefix() + objectName;

            // 发送图片上传事件
            messageProducer.sendEvent(EventMessage.builder()
                    .type(EventMessage.EventType.IMAGE_UPLOAD)
                    .data(fileUrl)
                    .timestamp(LocalDateTime.now())
                    .build());

            return fileUrl;
        } catch (IOException e) {
            throw new BusinessException("文件上传失败：" + e.getMessage());
        }
    }

    @Override
    public void deleteFile(String fileUrl) {
        if (fileUrl != null && fileUrl.startsWith(ossConfig.getUrlPrefix())) {
            String objectName = fileUrl.substring(ossConfig.getUrlPrefix().length());
            ossClient.deleteObject(ossConfig.getBucketName(), objectName);
        }
    }
} 