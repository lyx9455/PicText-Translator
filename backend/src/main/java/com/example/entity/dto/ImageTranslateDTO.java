package com.example.entity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ImageTranslateDTO {
    @NotBlank
    private String imageBase64;
    @NotBlank
    private String sourceLanguage;
    @NotBlank
    private String targetLanguage;
    // 默认通用图片翻译
    private String field = "general";
}
