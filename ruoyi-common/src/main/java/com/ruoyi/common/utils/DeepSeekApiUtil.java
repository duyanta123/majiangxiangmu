package com.ruoyi.common.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

public class DeepSeekApiUtil {

    private static final String API_URL = "https://api.deepseek.com/chat/completions";
    private static final int TIMEOUT_MS = 30000;
    private static final int MAX_RETRIES = 3;

    private static String apiKey;
    private static String basePrompt;

    static {
        apiKey = "sk-your-deepseek-api-key";
        basePrompt = "你是麻将馆智能助手，专门解答关于麻将馆业务的各种问题。\n\n" +
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
    }

    public static void setApiKey(String key) {
        apiKey = key;
    }

    public static void setBasePrompt(String prompt) {
        basePrompt = prompt;
    }

    public static String chat(String userInput, Map<String, String> context) {
        int retries = 0;
        while (retries < MAX_RETRIES) {
            try {
                return doChat(userInput, null, context);
            } catch (Exception e) {
                retries++;
                if (retries >= MAX_RETRIES) {
                    return "抱歉，AI服务暂时不可用，请稍后再试。错误信息：" + e.getMessage();
                }
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        return "抱歉，AI服务暂时不可用。";
    }

    public static String chatWithContext(String userInput, Map<String, Object> playerInfo, Map<String, String> context) {
        int retries = 0;
        while (retries < MAX_RETRIES) {
            try {
                return doChat(userInput, playerInfo, context);
            } catch (Exception e) {
                retries++;
                if (retries >= MAX_RETRIES) {
                    return "抱歉，AI服务暂时不可用，请稍后再试。错误信息：" + e.getMessage();
                }
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        return "抱歉，AI服务暂时不可用。";
    }

    private static String doChat(String userInput, Map<String, Object> playerInfo, Map<String, String> context) throws Exception {
        URL url = new URL(API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + apiKey);
        conn.setDoOutput(true);
        conn.setConnectTimeout(TIMEOUT_MS);
        conn.setReadTimeout(TIMEOUT_MS);

        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "deepseek-chat");

        JSONArray messages = new JSONArray();

        StringBuilder systemContent = new StringBuilder();
        systemContent.append(basePrompt);
        
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
            for (Map.Entry<String, String> entry : context.entrySet()) {
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
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 1500);

        byte[] outputBytes = requestBody.toJSONString().getBytes(StandardCharsets.UTF_8);
        conn.getOutputStream().write(outputBytes);

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            BufferedReader errorReader = new BufferedReader(
                new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
            StringBuilder errorResponse = new StringBuilder();
            String line;
            while ((line = errorReader.readLine()) != null) {
                errorResponse.append(line);
            }
            errorReader.close();
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

        JSONObject jsonResponse = JSON.parseObject(response.toString());
        JSONArray choices = jsonResponse.getJSONArray("choices");
        if (choices != null && !choices.isEmpty()) {
            JSONObject choice = choices.getJSONObject(0);
            JSONObject message = choice.getJSONObject("message");
            return message.getString("content");
        }

        return "抱歉，未能获取AI回复。";
    }

    public static String buildContextPrompt(Long playerId, String playerName, Map<String, Object> playerInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append("用户信息：");
        if (playerName != null) {
            sb.append("姓名：").append(playerName);
        }
        if (playerInfo != null) {
            if (playerInfo.containsKey("totalConsumption")) {
                sb.append("，累计消费：").append(playerInfo.get("totalConsumption")).append("元");
            }
            if (playerInfo.containsKey("memberLevel")) {
                sb.append("，会员等级：").append(playerInfo.get("memberLevel"));
            }
            if (playerInfo.containsKey("points")) {
                sb.append("，可用积分：").append(playerInfo.get("points"));
            }
        }
        return sb.toString();
    }
}