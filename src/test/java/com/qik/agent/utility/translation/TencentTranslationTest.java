package com.qik.agent.utility.translation;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


/**
 * @author : Qik 2025/9/1 15:48
 */
@SpringBootTest
class TencentTranslationTest {

    @Resource
    private TencentTranslation translation;

    @Test
    void translate() {
        String str = "A high-performing open embedding model with a large token context window.";

        String translate = translation.translate(str);
        System.out.println(translate);
        Assertions.assertNotNull(translate);
    }
}