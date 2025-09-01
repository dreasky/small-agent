package com.qik.agent.utility.translation;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 腾讯翻译API配置属性类
 * 用于封装腾讯翻译API的配置信息
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.tencent.translator")
public class TencentTranslationProperties {

    /**
     * 腾讯翻译API应用ID
     */
    private String secretId;

    /**
     * 腾讯翻译API密钥
     */
    private String secretKey;

    /**
     * 源语言，支持：
     * auto：自动识别（识别为一种语言）
     * zh：简体中文
     * zh-TW：繁体中文
     * en：英语
     * ja：日语
     * ko：韩语
     * fr：法语
     * es：西班牙语
     * it：意大利语
     * de：德语
     * tr：土耳其语
     * ru：俄语
     * pt：葡萄牙语
     * vi：越南语
     * id：印尼语
     * th：泰语
     * ms：马来西亚语
     * ar：阿拉伯语
     * hi：印地语
     */
    private String sourceLanguage = "auto";

    /**
     * 目标语言，各源语言的目标语言支持列表如下
     *
     * <li> zh（简体中文）：zh-TW（繁体中文）、en（英语）、ja（日语）、ko（韩语）、fr（法语）、es（西班牙语）、it（意大利语）、de（德语）、tr（土耳其语）、ru（俄语）、pt（葡萄牙语）、vi（越南语）、id（印尼语）、th（泰语）、ms（马来语）、ar（阿拉伯语）</li>
     * <li>zh-TW（繁体中文）：zh（简体中文）、en（英语）、ja（日语）、ko（韩语）、fr（法语）、es（西班牙语）、it（意大利语）、de（德语）、tr（土耳其语）、ru（俄语）、pt（葡萄牙语）、vi（越南语）、id（印尼语）、th（泰语）、ms（马来语）、ar（阿拉伯语）</li>
     * <li>en（英语）：zh（中文）、zh-TW（繁体中文）、ja（日语）、ko（韩语）、fr（法语）、es（西班牙语）、it（意大利语）、de（德语）、tr（土耳其语）、ru（俄语）、pt（葡萄牙语）、vi（越南语）、id（印尼语）、th（泰语）、ms（马来语）、ar（阿拉伯语）、hi（印地语）</li>
     * <li>ja（日语）：zh（中文）、zh-TW（繁体中文）、en（英语）、ko（韩语）</li>
     * <li>ko（韩语）：zh（中文）、zh-TW（繁体中文）、en（英语）、ja（日语）</li>
     * <li>fr（法语）：zh（中文）、zh-TW（繁体中文）、en（英语）、es（西班牙语）、it（意大利语）、de（德语）、tr（土耳其语）、ru（俄语）、pt（葡萄牙语）</li>
     * <li>es（西班牙语）：zh（中文）、zh-TW（繁体中文）、en（英语）、fr（法语）、it（意大利语）、de（德语）、tr（土耳其语）、ru（俄语）、pt（葡萄牙语）</li>
     * <li>it（意大利语）：zh（中文）、zh-TW（繁体中文）、en（英语）、fr（法语）、es（西班牙语）、de（德语）、tr（土耳其语）、ru（俄语）、pt（葡萄牙语）</li>
     * <li>de（德语）：zh（中文）、zh-TW（繁体中文）、en（英语）、fr（法语）、es（西班牙语）、it（意大利语）、tr（土耳其语）、ru（俄语）、pt（葡萄牙语）</li>
     * <li>tr（土耳其语）：zh（中文）、zh-TW（繁体中文）、en（英语）、fr（法语）、es（西班牙语）、it（意大利语）、de（德语）、ru（俄语）、pt（葡萄牙语）</li>
     * <li>ru（俄语）：zh（中文）、zh-TW（繁体中文）、en（英语）、fr（法语）、es（西班牙语）、it（意大利语）、de（德语）、tr（土耳其语）、pt（葡萄牙语）</li>
     * <li>pt（葡萄牙语）：zh（中文）、zh-TW（繁体中文）、en（英语）、fr（法语）、es（西班牙语）、it（意大利语）、de（德语）、tr（土耳其语）、ru（俄语）</li>
     * <li>vi（越南语）：zh（中文）、zh-TW（繁体中文）、en（英语）</li>
     * <li>id（印尼语）：zh（中文）、zh-TW（繁体中文）、en（英语）</li>
     * <li>th（泰语）：zh（中文）、zh-TW（繁体中文）、en（英语）</li>
     * <li>ms（马来语）：zh（中文）、zh-TW（繁体中文）、en（英语）</li>
     * <li>ar（阿拉伯语）：zh（中文）、zh-TW（繁体中文）、en（英语）</li>
     * <li>hi（印地语）：en（英语）</li>
     */
    private String targetLanguage = "zh";

}