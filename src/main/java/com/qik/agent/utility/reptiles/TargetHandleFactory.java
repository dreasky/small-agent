package com.qik.agent.utility.reptiles;

import cn.hutool.core.util.StrUtil;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.chromium.ChromiumDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

import java.io.IOException;
import java.time.Duration;

/**
 * 浏览器驱动配置类
 *
 * @author : Qik 2025/8/7 14:40
 */
public class TargetHandleFactory {

    public static TargetHandle jsoupTargetHandle() {
        Connection connection = Jsoup.newSession()
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/138.0.0.0 Safari/537.36 Edg/138.0.0.0")
                .timeout(30 * 1000)
                .maxBodySize(5 * 1024 * 1024);

        return url -> {
            try {
                return connection.url(url).get().html();
            } catch (TimeoutException e) {
                throw new RuntimeException("Timeout waiting for page to load: " + url, e);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load HTML from URL: " + url, e);
            }
        };
    }

    /**
     * 基于浏览器驱动的目标处理器
     *
     * @return 目标url处理器
     */
    public static TargetHandle edgeDriverTargetHandle() {
        // 配置本地浏览器引擎路径
        System.setProperty(
                "webdriver.edge.driver",
                "D:\\Tool\\edgedriver_win64\\msedgedriver.exe"
        );
        ChromiumDriver edgeDriver = getEdgeDriver();

        return url -> {
            if (StrUtil.isBlank(url)) return "";
            try {
                // 使用浏览器驱动访问URL
                edgeDriver.get(url);

                // 获取页面源代码
                return edgeDriver.getPageSource();
            } catch (TimeoutException e) {
                throw new RuntimeException("Timeout waiting for page to load: " + url, e);
            } catch (Exception e) {
                throw new RuntimeException("Failed to load HTML from URL: " + url, e);
            } finally {
                // 注意：这里每次调用都会关闭驱动，可能影响性能和重用性
                // 考虑在应用关闭时统一关闭驱动，或者使用驱动池
                // driver.quit();
            }
        };
    }


    private static ChromiumDriver getEdgeDriver() {
        // 配置EdgeDriver选项
        EdgeOptions options = new EdgeOptions();

        // 基础配置 - 界面开关
        options.addArguments("--headless"); // 无头模式，不显示浏览器窗口
        options.addArguments("--disable-gpu"); // 禁用GPU加速，避免某些环境下的渲染问题
        // UI和窗口配置
//        options.addArguments("--window-size=1920,1080"); // 设置窗口大小

        // 性能优化配置
        options.addArguments("--disable-extensions"); // 禁用扩展，提高启动速度
        options.addArguments("--disable-plugins-discovery"); // 禁用插件发现
        options.addArguments("--disable-dev-shm-usage"); // 禁用共享内存使用，解决内存不足问题
        options.addArguments("--no-sandbox"); // 禁用沙箱模式，在某些Linux环境中可能需要

        // 网络和安全配置
        options.addArguments("--ignore-certificate-errors"); // 忽略证书错误
        options.addArguments("--allow-insecure-localhost"); // 允许不安全的本地主机连接

        // 页面行为配置
        options.addArguments("--disable-popup-blocking"); // 禁用弹窗阻止
        options.addArguments("--disable-notifications"); // 禁用通知

        // 日志配置
        options.addArguments("--enable-logging"); // 启用日志
        options.addArguments("--v=1"); // 设置日志详细级别 (1-3)

        // 高级配置：设置用户代理
        options.addArguments(
                "--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/138.0.0.0 Safari/537.36 Edg/138.0.0.0");

        // 高级配置：启用隐身模式
        // options.addArguments("--incognito");

        // 设置加载超时
        options.setPageLoadTimeout(Duration.ofSeconds(30));

        return new EdgeDriver(options);
    }
}
