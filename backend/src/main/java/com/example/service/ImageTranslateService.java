package com.example.service;

import com.example.entity.dto.ImageTranslateDTO;
import com.example.entity.vo.ImageTranslateResult;

public interface ImageTranslateService {

    /**
     * 调用阿里云图片翻译接口
     */
    ImageTranslateResult translateImage(ImageTranslateDTO dto) throws Exception;
}
