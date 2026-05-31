package com.ruoyi.system.service;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Arrays;
import java.util.List;

@Service
public class AiKnowledgeService {
    
    private static final Logger log = LoggerFactory.getLogger(AiKnowledgeService.class);
    
    private static final List<KnowledgeItem> KNOWLEDGE_BASE = Arrays.asList(
        new KnowledgeItem(
            "营业时间",
            "爱麻社营业时间为：周一至周五 10:00-次日02:00，周六周日 09:00-次日03:00。法定节假日营业时间可能调整，请以店内公告为准。",
            Arrays.asList("营业时间", "几点开门", "几点关门", "营业", "营业时间")
        ),
        new KnowledgeItem(
            "会员权益",
            "爱麻社会员权益包括：1) 享受不同等级折扣（普通会员9.5折，银卡会员9折，金卡会员8.5折）；2) 积分返还（每消费1元累积1积分）；3) 会员专享活动；4) 优先预约权。积分可兑换商品、时长或抵扣消费。",
            Arrays.asList("会员权益", "折扣", "打折", "优惠", "会员打折", "金卡", "银卡")
        ),
        new KnowledgeItem(
            "预约规则",
            "预约规则：1) 可通过电话或微信提前预约；2) 预约需支付定金50元；3) 预约保留15分钟，超时自动取消；4) 取消预约需提前2小时，否则定金不退；5) 会员可享优先预约权。",
            Arrays.asList("预约规则", "如何预约", "怎么预约", "预约", "订位")
        ),
        new KnowledgeItem(
            "积分规则",
            "积分规则：1) 每消费1元累积1积分；2) 100积分可兑换1小时麻将时长；3) 500积分可兑换饮料一杯；4) 积分不可折现；5) 会员卡内积分长期有效。",
            Arrays.asList("积分规则", "积分兑换", "怎么获得积分", "积分有什么用", "积分")
        ),
        new KnowledgeItem(
            "收费标准",
            "收费标准：1) 平时时段：每小时30-50元不等；2) 节假日：每小时40-60元不等；3) 包间费另算；4) 具体价格以店内公示为准。支持微信、支付宝、现金支付。",
            Arrays.asList("价格", "收费", "多少钱", "小时", "套餐", "收费")
        ),
        new KnowledgeItem(
            "退订规则",
            "退订规则：1) 已支付定金的预约，如需取消请提前2小时联系；2) 提前2小时以上取消，定金全额退还；3) 2小时内取消，定金不退；4) 特殊情况可与门店协商。",
            Arrays.asList("退订", "取消预约", "退款", "取消")
        ),
        new KnowledgeItem(
            "场所规则",
            "场所规则：1) 禁止吸烟（指定区域除外）；2) 禁止酗酒闹事；3) 保持安静，不要大声喧哗；4) 爱护公共设施；5) 携宠物需拴系；6) 12岁以下儿童需成人陪同。",
            Arrays.asList("规则", "禁止", "注意", "要求")
        ),
        new KnowledgeItem(
            "联系方式",
            "联系方式：1) 门店电话：400-888-8888；2) 微信客服：aimengshe；3) 门店地址：XX市XX区XX路XX号。营业时间内随时恭候您的光临！",
            Arrays.asList("电话", "联系", "地址", "怎么联系", "微信")
        )
    );
    
    public String getKnowledgeContext(String question) {
        if (question == null || question.trim().isEmpty()) {
            return "";
        }
        
        String q = question.toLowerCase();
        
        for (KnowledgeItem item : KNOWLEDGE_BASE) {
            for (String keyword : item.keywords) {
                if (q.contains(keyword)) {
                    log.info("知识库匹配成功 - 类别: {}", item.category);
                    return item.content;
                }
            }
        }
        
        return "";
    }
    
    private static class KnowledgeItem {
        String category;
        String content;
        List<String> keywords;
        
        KnowledgeItem(String category, String content, List<String> keywords) {
            this.category = category;
            this.content = content;
            this.keywords = keywords;
        }
    }
}
