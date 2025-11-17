package com.example.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TextTranslateDTO {

    @NotBlank
    private String sourceLanguage;

    @NotBlank
    private String targetLanguage;

    @NotBlank
    private String text;

    // 可选
    private String formatType = "text";

    private String scene = "general";

    private String context;
}
