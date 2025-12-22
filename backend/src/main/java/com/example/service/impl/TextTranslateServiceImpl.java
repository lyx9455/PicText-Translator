package com.example.service.impl;

import com.aliyun.alimt20181012.models.TranslateGeneralRequest;
import com.aliyun.alimt20181012.models.TranslateGeneralResponse;
import com.aliyun.alimt20181012.models.TranslateGeneralResponseBody;
import com.example.entity.dto.TextTranslateDTO;
import com.example.entity.vo.TextTranslateVO;
import com.example.exception.TranslateException;
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
        // 阿里云限制一次最多 5000 字符
        if (dto.getText().length() > 5000) {
            throw new TranslateException(10008, "文本长度过长，请不要超过5000字符");
        }

        try {
            TranslateGeneralRequest request = new TranslateGeneralRequest()
                    .setFormatType(dto.getFormatType())
                    .setSourceLanguage(dto.getSourceLanguage())
                    .setTargetLanguage(dto.getTargetLanguage())
                    .setSourceText(dto.getText())
                    .setScene(dto.getScene())
                    .setContext(dto.getContext());

            TranslateGeneralResponse response = client.translateText(request);
            TranslateGeneralResponseBody body = response.getBody();

            if (body == null) {
                throw new TranslateException(19999, "翻译失败，返回为空");
            }

            // 阿里云官方 Code 判断
            if (!Integer.valueOf(200).equals(body.getCode())) {
                throw new TranslateException(body.getCode() != null ? body.getCode() : 19999,
                        body.getMessage() != null ? body.getMessage() : "翻译失败");
            }

            TranslateGeneralResponseBody.TranslateGeneralResponseBodyData data = body.getData();
            if (data == null || data.getTranslated() == null) {
                throw new TranslateException(10007, "翻译结果为空");
            }

            TextTranslateVO vo = new TextTranslateVO();
            vo.setTranslatedText(data.getTranslated());
            vo.setWordCount(Integer.valueOf(data.getWordCount()));
            vo.setDetectedLanguage(data.getDetectedLanguage());
            return vo;

        } catch (TranslateException e) {
            // 已经是自定义异常，直接抛
            throw e;
        } catch (Exception e) {
            log.error("阿里云翻译请求失败", e);
            throw new TranslateException(19999, "翻译失败，请稍后再试");
        }
    }

}
