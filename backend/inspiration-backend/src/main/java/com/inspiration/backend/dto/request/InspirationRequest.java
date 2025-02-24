package com.inspiration.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class InspirationRequest {
    @NotBlank(message = "标题不能为空")
    @Size(max = 100, message = "标题长度不能超过100个字符")
    private String title;

    @Size(max = 2000, message = "内容长度不能超过2000个字符")
    private String content;

    private Boolean isPublic = true;
} 