package com.example.service.impl;

import com.aliyun.alimt20181012.models.TranslateImageRequest;
import com.aliyun.alimt20181012.models.TranslateImageResponse;
import com.example.entity.dto.ImageTranslateDTO;
import com.example.entity.vo.ImageTranslateResult;
import com.example.service.ImageTranslateService;
import com.example.thirdparty.TranslateAliClient;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class ImageTranslateServiceImpl implements ImageTranslateService {

    private final TranslateAliClient aliClient;

    // 最大允许 Base64 原始数据大小（字节）——阿里说 10MB，Base64 会比原始大约增加 33%
    // 所以我们检查 Base64 字符串解码后的大小是否 <= 10 * 1024 * 1024
    private static final long MAX_BYTES = 10L * 1024L * 1024L;

    @Override
    public ImageTranslateResult translateImage(ImageTranslateDTO dto) throws Exception {

        // --- 基础校验（DTO 上也有注解，但这里再兜底） ---
        if (dto == null) {
            throw new IllegalArgumentException("请求体不能为空");
        }
        if (!StringUtils.hasText(dto.getImageBase64())) {
            throw new IllegalArgumentException("imageBase64 不能为空");
        }
        if (!StringUtils.hasText(dto.getSourceLanguage())) {
            throw new IllegalArgumentException("sourceLanguage 不能为空");
        }
        if (!StringUtils.hasText(dto.getTargetLanguage())) {
            throw new IllegalArgumentException("targetLanguage 不能为空");
        }

        // --- 检查大小（防止超 10M） ---
        try {
            byte[] decoded = Base64.decodeBase64(dto.getImageBase64().getBytes(StandardCharsets.UTF_8));
            if (decoded == null) {
                throw new IllegalArgumentException("无法解析 Base64 图片数据");
            }
            if (decoded.length > MAX_BYTES) {
                throw new IllegalArgumentException("图片大小超过 10MB 限制");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("无效的 Base64 图片数据: " + e.getMessage(), e);
        }

        // --- 构造阿里云请求 ---
        TranslateImageRequest request = new TranslateImageRequest()
                .setImageBase64(dto.getImageBase64())
                .setSourceLanguage(dto.getSourceLanguage())
                .setTargetLanguage(dto.getTargetLanguage())
                .setField(dto.getField() == null ? "general" : dto.getField())
                // 我们不需要编辑器数据，设为 false
                .setExt("{\"needEditorData\":\"false\"}");

        // --- 调用阿里云 ---
        TranslateImageResponse response = aliClient.translateImage(request);

        if (response == null || response.getBody() == null) {
            throw new RuntimeException("阿里云返回为空");
        }

        var body = response.getBody();

        // 处理阿里云返回的 Code（按 SDK 习惯，body 里有 code/message/data）
        Integer code = body.getCode();
        String message = body.getMessage();
        String requestId = body.getRequestId();

        if (code == null) {
            // 有些 SDK 可能把 code 放在 response.getCode()，做兜底
            // 不强依赖，若没有 code，则继续尝试读取 data
        }

        // 如果返回非 200，抛出异常（Controller 可以统一捕获并返回友好信息）
        if (code != null && code != 200) {
            throw new RuntimeException("阿里云图片翻译失败: code=" + code + ", message=" + message);
        }

        var data = body.getData();
        if (data == null) {
            throw new RuntimeException("阿里云返回结果中 data 为空: " + message);
        }

        // data 中应包含 FinalImageUrl / InPaintingUrl / TemplateJson
        String finalImageUrl = data.getFinalImageUrl();
        String inPaintingUrl = data.getInPaintingUrl();
        String templateJson = data.getTemplateJson();

        ImageTranslateResult result = new ImageTranslateResult();
        result.setRequestId(requestId);
        result.setCode(code);
        result.setMessage(message);
        result.setFinalImageUrl(finalImageUrl);
        result.setInPaintingUrl(inPaintingUrl);
        result.setTemplateJson(templateJson);

        return result;
    }
}
