package com.example.thirdparty;

import com.aliyun.alimt20181012.Client;
import com.aliyun.alimt20181012.models.TranslateGeneralRequest;
import com.aliyun.alimt20181012.models.TranslateGeneralResponse;
import com.aliyun.teaopenapi.models.Config;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TextTranslateAliClient {

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

    public TranslateGeneralResponse translate(TranslateGeneralRequest request) throws Exception {
        return client.translateGeneral(request);
    }
}
