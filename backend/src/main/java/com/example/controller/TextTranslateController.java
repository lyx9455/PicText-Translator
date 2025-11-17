package com.example.controller;

import com.example.common.RestBean;
import com.example.entity.dto.TextTranslateDTO;
import com.example.entity.vo.TextTranslateVO;
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

    /**
     * 文本翻译接口
     * POST /api/translate/text
     */
    @PostMapping("/text")
    public RestBean<TextTranslateVO> translateText(@Valid @RequestBody TextTranslateDTO dto,
                                                   HttpServletRequest request) {

        // 获取客户端 IP，用于限流
        String ip = request.getRemoteAddr();
        boolean allowed = flowUtils.limitOnceCheck("translate:text:" + ip, 1);
        if (!allowed) {
            return RestBean.failure(429, "请求过于频繁，请稍后再试");
        }

        // 调用服务进行翻译
        TextTranslateVO vo = textTranslateService.translate(dto);

        return RestBean.success(vo);
    }
}
