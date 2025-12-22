package com.example.controller;

import com.example.common.RestBean;
import com.example.entity.dto.TextTranslateDTO;
import com.example.entity.vo.TextTranslateVO;
import com.example.exception.TranslateException;
import com.example.service.TextTranslateService;
import com.example.utils.FlowUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/translate")
public class TextTranslateController {

    @Resource
    private TextTranslateService textTranslateService;

    @Resource
    private FlowUtils flowUtils;

    @PostMapping("/text")
    public RestBean<TextTranslateVO> translateText(@Valid @RequestBody TextTranslateDTO dto,
                                                   HttpServletRequest request) {

        // 限流
        String ip = request.getRemoteAddr();
        if (!flowUtils.limitOnceCheck("translate:text:" + ip, 1)) {
            return RestBean.failure(429, "请求过于频繁，请稍后再试");
        }

        try {
            TextTranslateVO vo = textTranslateService.translate(dto);
            return RestBean.success(vo);
        } catch (TranslateException e) {
            // 返回统一 RestBean 错误码和消息
            return RestBean.failure(e.getCode(), e.getMessage());
        }
    }
}

