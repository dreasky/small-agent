package com.qik.agent.model.love;

import java.util.Map;

/**
 * 用户信息
 *
 * @param userName   用户名
 * @param userGender 性别
 */
public record UserInfo(String userName, String userGender) {
    public Map<String, Object> userInfo() {
        return Map.of(
                "userName", userName,
                "userGender", userGender
        );
    }
}