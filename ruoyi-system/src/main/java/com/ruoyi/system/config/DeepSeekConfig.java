package com.ruoyi.system.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "deepseek")
public class DeepSeekConfig {

    private static final Logger logger = LoggerFactory.getLogger(DeepSeekConfig.class);

    private String apiKey;
    private String apiUrl = "https://api.deepseek.com/chat/completions";
    private String model = "deepseek-chat";
    private int timeout = 30000;
    private int maxRetries = 3;
    private double temperature = 0.7;
    private int maxTokens = 1500;

    @PostConstruct
    public void init() {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            logger.error("DeepSeek API Key 未配置！请在 application.yml 中配置 deepseek.api-key");
        } else {
            logger.info("DeepSeek API 配置完成 - URL: {}, Model: {}", apiUrl, model);
        }
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public int getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(int maxTokens) {
        this.maxTokens = maxTokens;
    }

    public boolean isValidApiKey() {
        return apiKey != null && !apiKey.trim().isEmpty();
    }
}
