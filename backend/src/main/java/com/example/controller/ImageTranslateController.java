package com.example.controller;

import com.example.common.RestBean;
import com.example.entity.dto.ImageTranslateDTO;
import com.example.entity.vo.ImageTranslateResult;
import com.example.service.ImageTranslateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/translate")
@RequiredArgsConstructor
public class ImageTranslateController {

    private final ImageTranslateService imageTranslateService;

    @PostMapping("/image")
    public RestBean<ImageTranslateResult> translateImage(@RequestBody ImageTranslateDTO dto) throws Exception {
        ImageTranslateResult result = imageTranslateService.translateImage(dto);
        return RestBean.success(result);
    }
}