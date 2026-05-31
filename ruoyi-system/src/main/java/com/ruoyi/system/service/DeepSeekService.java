package com.ruoyi.system.service;

import com.ruoyi.system.config.DeepSeekConfig;
import com.ruoyi.system.prompt.FallbackPrompts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

@Service
public class DeepSeekService {

    private static final Logger logger = LoggerFactory.getLogger(DeepSeekService.class);

    private static final String BASE_PROMPT = "你是麻将馆智能助手，专门解答关于麻将馆业务的各种问题。\n\n" +
            "业务范围：\n" +
            "1. 门店查询 - 营业时间（通常为9:00-24:00）、门店地址、联系方式、包间类型\n" +
            "2. 预约咨询 - 如何预约、预约规则（提前30分钟以上）、取消预约流程\n" +
            "3. 会员权益 - 会员等级（普通、银卡、金卡、钻卡）、积分规则（消费1元积1分）、优惠政策（生日优惠、节日活动）\n" +
            "4. 消费账单 - 消费记录查询、费用明细（台费、包间费、茶水费）、付款方式\n" +
            "5. 麻将规则 - 基本玩法、计分规则、胡牌类型\n" +
            "6. 优惠活动 - 限时折扣、满减优惠、积分兑换礼品\n" +
            "7. 积分兑换 - 积分规则、兑换比例、可兑换礼品清单\n" +
            "\n" +
            "回复要求：\n" +
            "- 专业、友好，符合麻将馆服务场景\n" +
            "- 涉及价格收费的问题，按标准收费回答（台费每小时30-80元不等）\n" +
            "- 涉及优惠活动，结合门店实际情况回答\n" +
            "- 如果用户询问个人信息但你无法获取，请提示用户前往门店或联系客服";

    private final DeepSeekConfig config;
    private final FallbackPrompts fallbackPrompts;

    public DeepSeekService(DeepSeekConfig config) {
        this.config = config;
        this.fallbackPrompts = new FallbackPrompts();
        logger.info("DeepSeekService 初始化完成 - API URL: {}, Model: {}", config.getApiUrl(), config.getModel());
    }

    public String chat(String userInput, Map<String, Object> playerInfo, Map<String, String> context) {
        // 优先调用真实API，只有API调用失败时才使用降级回复
        // 检查API Key是否有效
        if (!config.isValidApiKey()) {
            logger.error("DeepSeek API Key无效: {}", config.getApiKey());
            return getFallbackOrDefault(userInput, "AI服务鉴权失败，请联系管理员检查API配置");
        }

        int retries = 0;
        while (retries < config.getMaxRetries()) {
            try {
                String response = doChat(userInput, playerInfo, context);
                if (response != null && !response.isEmpty()) {
                    return response;
                }
                logger.warn("DeepSeek API 返回空内容，重试中...");
            } catch (DeepSeekAuthException e) {
                logger.error("DeepSeek API 鉴权失败: {}", e.getMessage());
                return "AI服务鉴权失败，请联系管理员检查API配置";
            } catch (DeepSeekRateLimitException e) {
                logger.error("DeepSeek API 限流: {}", e.getMessage());
                if (retries >= config.getMaxRetries() - 1) {
                    return "请求过于频繁，请稍后再试";
                }
            } catch (DeepSeekServerException e) {
                logger.error("DeepSeek API 服务端错误: {}", e.getMessage());
                if (retries >= config.getMaxRetries() - 1) {
                    return "AI服务暂时不可用，请稍后再试";
                }
            } catch (DeepSeekNetworkException e) {
                logger.error("DeepSeek API 网络错误: {}", e.getMessage());
                if (retries >= config.getMaxRetries() - 1) {
                    return "网络连接异常，请检查网络后重试";
                }
            } catch (Exception e) {
                logger.error("DeepSeek API 请求异常: {}", e.getMessage(), e);
                if (retries >= config.getMaxRetries() - 1) {
                    return "AI服务暂时不可用，请稍后再试";
                }
            }
            retries++;
            try {
                TimeUnit.SECONDS.sleep(1 << retries);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        // 所有重试失败，返回降级回复
        return getFallbackOrDefault(userInput, "AI服务暂时不可用，请稍后再试");
    }

    private String getFallbackOrDefault(String userInput, String defaultMsg) {
        String fallback = fallbackPrompts.getFallbackResponse(userInput);
        if (fallback != null) {
            return fallback;
        }
        return defaultMsg;
    }

    private String doChat(String userInput, Map<String, Object> playerInfo, Map<String, String> context) throws Exception {
        long startTime = System.currentTimeMillis();
        String apiUrl = config.getApiUrl();

        logger.info("DeepSeek API 请求开始 - URL: {}, 用户输入: {}", apiUrl, userInput.length() > 50 ? userInput.substring(0, 50) + "..." : userInput);

        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + config.getApiKey().trim());
        conn.setDoOutput(true);
        conn.setConnectTimeout(config.getTimeout());
        conn.setReadTimeout(config.getTimeout());

        JSONObject requestBody = new JSONObject();
        requestBody.put("model", config.getModel());

        JSONArray messages = new JSONArray();

        StringBuilder systemContent = new StringBuilder();
        systemContent.append(BASE_PROMPT);

        if (playerInfo != null && !playerInfo.isEmpty()) {
            systemContent.append("\n\n当前用户档案信息：\n");
            systemContent.append("---\n");

            if (playerInfo.containsKey("playerName")) {
                systemContent.append("玩家姓名：").append(playerInfo.get("playerName")).append("\n");
            }
            if (playerInfo.containsKey("memberLevel")) {
                systemContent.append("会员等级：").append(playerInfo.get("memberLevel")).append("\n");
            }
            if (playerInfo.containsKey("totalConsumption")) {
                systemContent.append("累计消费：").append(playerInfo.get("totalConsumption")).append("元\n");
            }
            if (playerInfo.containsKey("points")) {
                systemContent.append("积分余额：").append(playerInfo.get("points")).append("积分\n");
            }
            if (playerInfo.containsKey("consumeCount")) {
                systemContent.append("消费次数：").append(playerInfo.get("consumeCount")).append("次\n");
            }

            systemContent.append("---\n");
            systemContent.append("请根据以上用户档案信息，为用户提供个性化的服务和回答。");
        }

        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", systemContent.toString());
        messages.add(systemMessage);

        if (context != null && !context.isEmpty()) {
            Map<String, String> sortedContext = new LinkedHashMap<>(context);
            for (Map.Entry<String, String> entry : sortedContext.entrySet()) {
                String[] parts = entry.getValue().split("::", 2);
                if (parts.length == 2) {
                    JSONObject msg = new JSONObject();
                    msg.put("role", parts[0]);
                    msg.put("content", parts[1]);
                    messages.add(msg);
                }
            }
        }

        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", userInput);
        messages.add(userMessage);

        requestBody.put("messages", messages);
        requestBody.put("temperature", config.getTemperature());
        requestBody.put("max_tokens", config.getMaxTokens());

        String requestBodyStr = requestBody.toJSONString();
        logger.debug("DeepSeek API 请求体: {}", requestBodyStr.length() > 200 ? requestBodyStr.substring(0, 200) + "..." : requestBodyStr);

        byte[] outputBytes = requestBodyStr.getBytes(StandardCharsets.UTF_8);
        conn.getOutputStream().write(outputBytes);

        int responseCode = conn.getResponseCode();
        long responseTime = System.currentTimeMillis() - startTime;

        logger.info("DeepSeek API 响应状态码: {}, 耗时: {}ms", responseCode, responseTime);

        // 根据HTTP状态码抛出不同异常
        if (responseCode == 401) {
            BufferedReader errorReader = new BufferedReader(
                    new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
            StringBuilder errorResponse = new StringBuilder();
            String line;
            while ((line = errorReader.readLine()) != null) {
                errorResponse.append(line);
            }
            errorReader.close();
            conn.disconnect();
            logger.error("DeepSeek API 401鉴权失败: {}", errorResponse);
            throw new DeepSeekAuthException("401 Unauthorized: " + errorResponse);
        } else if (responseCode == 429) {
            logger.warn("DeepSeek API 429限流");
            throw new DeepSeekRateLimitException("429 Too Many Requests");
        } else if (responseCode >= 500) {
            BufferedReader errorReader = new BufferedReader(
                    new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
            StringBuilder errorResponse = new StringBuilder();
            String line;
            while ((line = errorReader.readLine()) != null) {
                errorResponse.append(line);
            }
            errorReader.close();
            conn.disconnect();
            logger.error("DeepSeek API 服务端错误 {}: {}", responseCode, errorResponse);
            throw new DeepSeekServerException("Server error " + responseCode + ": " + errorResponse);
        } else if (responseCode != 200) {
            BufferedReader errorReader = new BufferedReader(
                    new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
            StringBuilder errorResponse = new StringBuilder();
            String line;
            while ((line = errorReader.readLine()) != null) {
                errorResponse.append(line);
            }
            errorReader.close();
            conn.disconnect();
            logger.error("DeepSeek API 请求失败 {}: {}", responseCode, errorResponse);
            throw new RuntimeException("API request failed with code " + responseCode + ": " + errorResponse);
        }

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        conn.disconnect();

        String responseStr = response.toString();
        logger.debug("DeepSeek API 响应: {}", responseStr.length() > 200 ? responseStr.substring(0, 200) + "..." : responseStr);

        JSONObject jsonResponse = JSON.parseObject(responseStr);
        JSONArray choices = jsonResponse.getJSONArray("choices");
        if (choices != null && !choices.isEmpty()) {
            JSONObject choice = choices.getJSONObject(0);
            JSONObject message = choice.getJSONObject("message");
            String content = message.getString("content");
            logger.info("DeepSeek API 响应成功，回复长度: {} 字符", content != null ? content.length() : 0);
            return content;
        }

        logger.warn("DeepSeek API 响应为空");
        return "抱歉，未能获取AI回复。";
    }

    // 自定义异常类
    private static class DeepSeekAuthException extends Exception {
        public DeepSeekAuthException(String message) {
            super(message);
        }
    }

    private static class DeepSeekRateLimitException extends Exception {
        public DeepSeekRateLimitException(String message) {
            super(message);
        }
    }

    private static class DeepSeekServerException extends Exception {
        public DeepSeekServerException(String message) {
            super(message);
        }
    }

    private static class DeepSeekNetworkException extends Exception {
        public DeepSeekNetworkException(String message) {
            super(message);
        }
    }
}
