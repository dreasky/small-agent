package com.qik.agent.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康测试接口
 *
 * @author : Qik 2025/7/29 16:12
 */
@RestController
@RequestMapping("/health")
public class HealthController {

    @GetMapping
    public String health() {
        return "ok";
    }
}
