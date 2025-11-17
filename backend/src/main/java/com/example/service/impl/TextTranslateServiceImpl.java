package com.example.service.impl;

import com.aliyun.alimt20181012.models.TranslateGeneralRequest;
import com.aliyun.alimt20181012.models.TranslateGeneralResponse;
import com.example.entity.dto.TextTranslateDTO;
import com.example.entity.vo.TextTranslateVO;
import com.example.service.TextTranslateService;
import com.example.thirdparty.TranslateAliClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TextTranslateServiceImpl implements TextTranslateService {

    private final TranslateAliClient client;

    @Override
    public TextTranslateVO translate(TextTranslateDTO dto) {

        try {
            TranslateGeneralRequest request = new TranslateGeneralRequest()
                    .setFormatType(dto.getFormatType())
                    .setSourceLanguage(dto.getSourceLanguage())
                    .setTargetLanguage(dto.getTargetLanguage())
                    .setSourceText(dto.getText())
                    .setScene(dto.getScene())
                    .setContext(dto.getContext());

            TranslateGeneralResponse response = client.translateText(request);

            String translated = response.getBody().getData().getTranslated();
            Integer wordCount = Integer.valueOf(response.getBody().getData().getWordCount());
            String detectedLanguage = response.getBody().getData().getDetectedLanguage();

            TextTranslateVO vo = new TextTranslateVO();
            vo.setTranslatedText(translated);
            vo.setWordCount(wordCount);
            vo.setDetectedLanguage(detectedLanguage);

            return vo;

        } catch (Exception e) {
            log.error("阿里云翻译请求失败: {}", e.getMessage(), e);
            throw new RuntimeException("翻译失败，请稍后再试");
        }
    }
}
