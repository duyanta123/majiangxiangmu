package com.ruoyi.web.controller.app;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.SysMemberLevel;
import com.ruoyi.system.service.ISysMemberLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/app/promotion")
public class AppPromotionController {

    private static final List<Map<String, Object>> promotions = new ArrayList<>();
    
    static {
        promotions.add(createPromotion(1L, "新用户首单优惠", "新用户首次预约享受8折优惠", "2026-01-01", "2026-12-31", "discount", "8折"));
        promotions.add(createPromotion(2L, "会员日特惠", "每周三会员日，全场包间半价", "2026-01-01", "2026-12-31", "discount", "半价"));
        promotions.add(createPromotion(3L, "积分兑换时长", "500积分兑换1小时时长", "2026-01-01", "2026-12-31", "points", "1小时"));
        promotions.add(createPromotion(4L, "邀请好友", "邀请好友成功预约，双方各获100积分", "2026-01-01", "2026-12-31", "points", "100积分"));
        promotions.add(createPromotion(5L, "生日特惠", "生日当月享受3次VIP包间免时长优惠", "2026-01-01", "2026-12-31", "special", "免时长"));
    }

    private static Map<String, Object> createPromotion(Long id, String title, String desc, 
            String startDate, String endDate, String type, String benefit) {
        Map<String, Object> promotion = new HashMap<>();
        promotion.put("id", id);
        promotion.put("title", title);
        promotion.put("description", desc);
        promotion.put("startDate", startDate);
        promotion.put("endDate", endDate);
        promotion.put("type", type);
        promotion.put("benefit", benefit);
        promotion.put("isActive", true);
        return promotion;
    }

    @Autowired
    private ISysMemberLevelService memberLevelService;

    @GetMapping("/list")
    public AjaxResult getPromotions() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            return AjaxResult.error("用户未登录");
        }
        
        return AjaxResult.success().put("list", promotions);
    }

    @GetMapping("/member/benefits")
    public AjaxResult getMemberBenefits() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            return AjaxResult.error("用户未登录");
        }
        
        Integer currentPoints = 1000;
        SysMemberLevel currentLevel = memberLevelService.getLevelByPoints(currentPoints);
        
        List<SysMemberLevel> allLevels = memberLevelService.selectMemberLevelList(new SysMemberLevel());
        
        Map<String, Object> result = new HashMap<>();
        result.put("currentLevel", currentLevel != null ? currentLevel.getLevelName() : "普通会员");
        result.put("currentPoints", currentPoints);
        result.put("nextLevel", getNextLevel(allLevels, currentPoints));
        result.put("pointsToNext", getPointsToNextLevel(allLevels, currentPoints));
        result.put("benefits", getMemberBenefitsList(currentLevel));
        result.put("levelProgress", calculateLevelProgress(allLevels, currentPoints));
        
        return AjaxResult.success().put("data", result);
    }

    private String getNextLevel(List<SysMemberLevel> levels, Integer points) {
        for (SysMemberLevel level : levels) {
            if (level.getMinPoints() != null && level.getMinPoints() > points) {
                return level.getLevelName();
            }
        }
        return "最高等级";
    }

    private Integer getPointsToNextLevel(List<SysMemberLevel> levels, Integer points) {
        for (SysMemberLevel level : levels) {
            if (level.getMinPoints() != null && level.getMinPoints() > points) {
                return level.getMinPoints() - points;
            }
        }
        return 0;
    }

    private List<Map<String, Object>> getMemberBenefitsList(SysMemberLevel level) {
        List<Map<String, Object>> benefits = new ArrayList<>();
        
        if (level != null && level.getDiscount() != null) {
            benefits.add(createBenefit("专属折扣", "享受" + level.getDiscount() + "折优惠", "💰"));
        } else {
            benefits.add(createBenefit("专属折扣", "享受9.5折优惠", "💰"));
        }
        
        benefits.add(createBenefit("积分加速", "消费1元积累1积分", "🎁"));
        benefits.add(createBenefit("专属活动", "优先参与会员专属活动", "🎉"));
        benefits.add(createBenefit("生日福利", "生日当月享受特别优惠", "🎂"));
        
        return benefits;
    }

    private Map<String, Object> createBenefit(String title, String desc, String icon) {
        Map<String, Object> benefit = new HashMap<>();
        benefit.put("title", title);
        benefit.put("description", desc);
        benefit.put("icon", icon);
        return benefit;
    }

    private Integer calculateLevelProgress(List<SysMemberLevel> levels, Integer points) {
        if (levels == null || levels.isEmpty()) {
            return 0;
        }
        
        Integer minPoints = 0;
        Integer maxPoints = 10000;
        
        for (SysMemberLevel level : levels) {
            if (level.getMinPoints() != null && level.getMinPoints() > points) {
                maxPoints = level.getMinPoints();
                break;
            }
            if (level.getMinPoints() != null) {
                minPoints = level.getMinPoints();
            }
        }
        
        if (maxPoints.equals(minPoints)) {
            return 100;
        }
        
        return (points - minPoints) * 100 / (maxPoints - minPoints);
    }
}
