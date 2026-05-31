package com.ruoyi.system.prompt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class FallbackPrompts {

    private static final Map<Pattern, String> promptMap = new HashMap<>();
    private static final List<String> defaultResponses = new ArrayList<>();

    static {
        // 门店营业时间相关
        promptMap.put(Pattern.compile("营业时间|几点开门|几点关门|什么时候营业", Pattern.CASE_INSENSITIVE),
                "我们的门店营业时间为：\n- 周一至周五：9:00 - 24:00\n- 周末及节假日：24小时营业\n建议提前预约以确保有位哦！");

        promptMap.put(Pattern.compile("地址|在哪里|位置", Pattern.CASE_INSENSITIVE),
                "我们的门店位于市中心繁华地段，具体地址请咨询前台获取详细信息。您也可以通过地图导航搜索「麻将馆」找到我们。");

        // 会员权益相关
        promptMap.put(Pattern.compile("会员|等级|银卡|金卡|钻卡", Pattern.CASE_INSENSITIVE),
                "我们的会员等级分为四个等级：\n• 普通会员：享受基础服务\n• 银卡会员：消费满1000元升级，享9折优惠\n• 金卡会员：消费满5000元升级，享8折优惠\n• 钻卡会员：消费满10000元升级，享7折优惠\n会员生日当天可获赠免费包间体验！");

        promptMap.put(Pattern.compile("积分|积分兑换|兑换礼品", Pattern.CASE_INSENSITIVE),
                "积分规则：消费1元积1分\n积分兑换：\n• 100积分 = 10元消费抵扣\n• 500积分 = 免费茶水套餐\n• 1000积分 = 免费2小时包间\n• 2000积分 = 精美礼品一份\n如需兑换，请联系前台办理。");

        // 预约规则相关
        promptMap.put(Pattern.compile("预约|订位|订包间", Pattern.CASE_INSENSITIVE),
                "预约规则：\n• 可提前1-7天预约\n• 预约需提前30分钟到场\n• 如需取消预约，请提前2小时告知\n• 包间保留15分钟，超时将自动取消\n预约方式：电话预约或到店预约");

        promptMap.put(Pattern.compile("取消预约|退订", Pattern.CASE_INSENSITIVE),
                "取消预约请提前2小时联系我们，否则可能影响您下次预约。感谢您的理解！");

        // 优惠活动相关
        promptMap.put(Pattern.compile("优惠|活动|折扣|促销", Pattern.CASE_INSENSITIVE),
                "当前优惠活动：\n• 新客优惠：首次到店享8折\n• 工作日特惠：周一至周四全天9折\n• 夜间特惠：22:00后包间费8折\n• 团购优惠：美团/大众点评搜索「麻将馆」享更多优惠\n更多活动请关注我们的官方公众号！");

        // 消费流程相关
        promptMap.put(Pattern.compile("怎么收费|价格|多少钱|台费", Pattern.CASE_INSENSITIVE),
                "收费标准：\n• 大厅散台：30-50元/小时\n• 普通包间：60-80元/小时\n• VIP包间：100-150元/小时\n• 茶水另计：10-30元/杯\n节假日可能有调整，以到店实际价格为准。");

        promptMap.put(Pattern.compile("付款|结账|买单", Pattern.CASE_INSENSITIVE),
                "支持多种付款方式：\n• 现金支付\n• 微信支付\n• 支付宝支付\n• 会员卡支付\n• 团购券支付\n结账时请告知服务员您的桌号即可。");

        // 麻将规则相关
        promptMap.put(Pattern.compile("麻将规则|怎么玩|胡牌", Pattern.CASE_INSENSITIVE),
                "我们提供多种麻将玩法：\n• 四川麻将：血战到底、血流成河\n• 广东麻将：推倒胡\n• 国标麻将：竞技规则\n• 本地麻将：特色玩法\n每桌配有详细规则说明，服务员也可现场讲解。");

        // 其他常见问题
        promptMap.put(Pattern.compile("电话|联系方式", Pattern.CASE_INSENSITIVE),
                "客服热线：400-XXX-XXXX\n营业时间内均有专人接听，欢迎咨询！");

        promptMap.put(Pattern.compile("停车|车位", Pattern.CASE_INSENSITIVE),
                "门店周边有停车场，消费满2小时可免费停车1小时，详情请咨询前台。");

        promptMap.put(Pattern.compile("包间|房间", Pattern.CASE_INSENSITIVE),
                "我们有多种类型包间：\n• 标准间：适合4人\n• 豪华间：适合6人\n• VIP间：适合8人及以上\n所有包间均配备空调、新风系统和独立卫生间。");

        // 默认回复
        defaultResponses.add("感谢您的咨询！如有其他问题，请随时联系我们的工作人员。");
        defaultResponses.add("很高兴为您服务！如需更多帮助，欢迎到店咨询。");
        defaultResponses.add("我们会不断完善服务，期待您的光临！");
    }

    public String getFallbackResponse(String userInput) {
        if (userInput == null || userInput.trim().isEmpty()) {
            return null;
        }
        
        for (Map.Entry<Pattern, String> entry : promptMap.entrySet()) {
            if (entry.getKey().matcher(userInput).find()) {
                return entry.getValue();
            }
        }
        return null;
    }

    public String getDefaultResponse() {
        int index = (int) (System.currentTimeMillis() % defaultResponses.size());
        return defaultResponses.get(index);
    }
}
