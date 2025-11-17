package com.example.entity.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ImageTranslateResult {

    private String finalImageUrl;
    private String inPaintingUrl;
    private String templateJson;
    private String requestId;
    private Integer code;
    private String message;
}
