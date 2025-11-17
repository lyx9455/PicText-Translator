package com.example.service;

import com.example.entity.dto.TextTranslateDTO;
import com.example.entity.vo.TextTranslateVO;

public interface TextTranslateService {

    TextTranslateVO translate(TextTranslateDTO dto);
}
