package com.example.thirdparty;

import com.aliyun.alimt20181012.Client;
import com.aliyun.alimt20181012.models.*;
import com.aliyun.teaopenapi.models.Config;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TranslateAliClient {

    @Value("${aliyun.translate.accessKey}")
    private String accessKey;

    @Value("${aliyun.translate.secretKey}")
    private String secretKey;

    @Value("${aliyun.translate.endpoint:mt.cn-hangzhou.aliyuncs.com}")
    private String endpoint;

    private Client client;

    @PostConstruct
    public void init() throws Exception {
        Config config = new Config()
                .setAccessKeyId(accessKey)
                .setAccessKeySecret(secretKey)
                .setEndpoint(endpoint);

        this.client = new Client(config);
    }

    /** ------------------------- 通用文本翻译 ------------------------- **/
    public TranslateGeneralResponse translateText(TranslateGeneralRequest request) throws Exception {
        return client.translateGeneral(request);
    }

    /** ------------------------- 图片翻译（你即将写） ------------------------- **/
    public TranslateImageResponse translateImage(TranslateImageRequest request) throws Exception {
        return client.translateImage(request);
    }

//    /** ------------------------- 文档翻译（可选） ------------------------- **/
//    public TranslateDocumentResponse translateDocument(TranslateDocumentRequest request) throws Exception {
//        return client.translateDocument(request);
//    }
}
