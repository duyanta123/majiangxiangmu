package com.ruoyi.web.controller.app;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/app/notification")
public class AppNotificationController {

    private static final List<Map<String, Object>> notificationStore = new ArrayList<>();
    
    static {
        notificationStore.add(createNotification(1L, "预约成功", "您的预约已提交成功，请按时到店", "reservation", "2026-05-24 10:00"));
        notificationStore.add(createNotification(2L, "预约已确认", "您的预约已被管理员确认，期待您的光临", "reservation", "2026-05-24 10:30"));
        notificationStore.add(createNotification(3L, "积分到账", "您已获得100积分，感谢消费", "points", "2026-05-24 14:00"));
        notificationStore.add(createNotification(4L, "消费提醒", "您有新的消费记录，请确认", "consumption", "2026-05-24 14:30"));
    }

    private static Map<String, Object> createNotification(Long id, String title, String content, String type, String time) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("id", id);
        notification.put("title", title);
        notification.put("content", content);
        notification.put("type", type);
        notification.put("time", time);
        notification.put("isRead", false);
        return notification;
    }

    @GetMapping("/list")
    public AjaxResult getMyNotifications() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            return AjaxResult.error("用户未登录");
        }
        
        return AjaxResult.success().put("list", notificationStore);
    }

    @GetMapping("/unreadCount")
    public AjaxResult getUnreadCount() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            return AjaxResult.error("用户未登录");
        }
        
        long count = notificationStore.stream()
                .filter(n -> !Boolean.TRUE.equals(n.get("isRead")))
                .count();
        
        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        return AjaxResult.success().put("data", result);
    }

    @PostMapping("/markRead")
    public AjaxResult markAsRead(@RequestParam Long notificationId) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            return AjaxResult.error("用户未登录");
        }
        
        for (Map<String, Object> notification : notificationStore) {
            if (notificationId.equals(notification.get("id"))) {
                notification.put("isRead", true);
                return AjaxResult.success("标记已读成功");
            }
        }
        
        return AjaxResult.error("通知不存在");
    }

    @PostMapping("/markAllRead")
    public AjaxResult markAllAsRead() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            return AjaxResult.error("用户未登录");
        }
        
        notificationStore.forEach(n -> n.put("isRead", true));
        return AjaxResult.success("全部已读成功");
    }

    public static void addNotification(String title, String content, String type) {
        Long id = (long) (notificationStore.size() + 1);
        String time = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
        notificationStore.add(0, createNotification(id, title, content, type, time));
    }
}
