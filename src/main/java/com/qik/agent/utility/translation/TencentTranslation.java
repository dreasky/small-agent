package com.qik.agent.utility.translation;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.tmt.v20180321.TmtClient;
import com.tencentcloudapi.tmt.v20180321.models.TextTranslateRequest;
import com.tencentcloudapi.tmt.v20180321.models.TextTranslateResponse;
import org.springframework.stereotype.Component;

/**
 * 腾讯云翻译API查询转换器
 * 用于将查询文本翻译为目标语言
 */
@Component
public class TencentTranslation {

    private final Credential cred;
    private final String targetLanguage;
    private final String sourceLanguage;

    public TencentTranslation(TencentTranslationProperties properties) {
        this.cred = new Credential(properties.getSecretId(), properties.getSecretKey());
        this.targetLanguage = properties.getTargetLanguage();
        this.sourceLanguage = properties.getSourceLanguage();
    }

    /**
     * 调用腾讯云翻译API进行翻译
     */
    public String translate(String text) {
        try {
            TmtClient client = new TmtClient(cred, "ap-guangzhou");

            // 实例化翻译请求对象
            TextTranslateRequest req = new TextTranslateRequest();
            req.setSourceText(text);
            req.setSource(sourceLanguage); // 源语言
            req.setTarget(targetLanguage); // 目标语言
            req.setProjectId(0L); // 项目ID，默认为0

            // 发送请求并获取响应
            TextTranslateResponse resp = client.TextTranslate(req);

            // 返回翻译结果
            return resp.getTargetText();
        } catch (TencentCloudSDKException e) {
            throw new RuntimeException("腾讯云翻译API调用失败: " + e.getMessage(), e);
        }
    }
}