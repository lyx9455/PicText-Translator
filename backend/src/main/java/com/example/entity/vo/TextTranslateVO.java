package com.example.entity.vo;

import lombok.Data;

@Data
public class TextTranslateVO {

    private String translatedText;

    private Integer wordCount;

    private String detectedLanguage;
}
