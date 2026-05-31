package com.ruoyi.system.service;

import com.ruoyi.system.domain.vo.AppReservationVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

@Service
public class AppReservationService {
    
    private static final Logger log = LoggerFactory.getLogger(AppReservationService.class);
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Map<String, Object> createReservation(Long userId, AppReservationVo reservation) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (hasConflictReservation(userId, reservation.getReservationDate(), reservation.getReservationTime())) {
                result.put("success", false);
                result.put("message", "该时间段已有预约，请选择其他时间");
                log.warn("预约冲突，用户ID: {}, 日期: {}, 时间: {}", userId, reservation.getReservationDate(), reservation.getReservationTime());
                return result;
            }

            String sql = "INSERT INTO app_reservation (user_id, table_type, reservation_date, reservation_time, " +
                    "duration, person_count, contact_name, contact_phone, remark, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, '0')";

            int affectedRows = jdbcTemplate.update(sql, userId, reservation.getTableType(),
                    reservation.getReservationDate(), reservation.getReservationTime(),
                    reservation.getDuration() != null ? reservation.getDuration() : 2,
                    reservation.getPersonCount() != null ? reservation.getPersonCount() : 4,
                    reservation.getContactName(), reservation.getContactPhone(), reservation.getRemark());

            if (affectedRows > 0) {
                Long reservationId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);

                String playerName = reservation.getContactName() != null ? reservation.getContactName() : getPlayerName(userId);
                String playerPhone = reservation.getContactPhone() != null ? reservation.getContactPhone() : getPlayerPhone(userId);
                String reservationCode = "RES" + System.currentTimeMillis();

                String timeSlot = reservation.getReservationTime();
                String[] times = timeSlot.split("-");
                String startTime = times.length > 0 ? times[0].trim() : "00:00:00";
                String endTime = times.length > 1 ? times[1].trim() : "00:00:00";

                String adminSql = "INSERT INTO sys_reservation (player_id, player_name, player_phone, table_name, " +
                        "reserve_date, reserve_time, start_time, end_time, duration, people_count, status, reservation_status, " +
                        "remark, reservation_code, app_reservation_id, del_flag, create_time) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, '0', '0', ?, ?, ?, '0', NOW())";

                jdbcTemplate.update(adminSql, userId, playerName, playerPhone, reservation.getTableType(),
                        reservation.getReservationDate(), startTime, startTime, endTime,
                        reservation.getDuration() != null ? reservation.getDuration() : 2,
                        reservation.getPersonCount() != null ? reservation.getPersonCount() : 4,
                        reservation.getRemark(), reservationCode, reservationId);

                Map<String, Object> data = new HashMap<>();
                data.put("reservationId", reservationId);
                data.put("tableType", reservation.getTableType());
                data.put("reservationDate", reservation.getReservationDate());
                data.put("reservationTime", reservation.getReservationTime());

                result.put("success", true);
                result.put("message", "预约提交成功，请等待确认");
                result.put("data", data);
                log.info("预约创建成功，ID: {}, 用户ID: {}", reservationId, userId);
            } else {
                result.put("success", false);
                result.put("message", "预约提交失败：未成功插入数据库");
                log.warn("预约创建失败，用户ID: {}, 影响行数: {}", userId, affectedRows);
            }
            return result;
        } catch (Exception e) {
            log.error("创建预约失败: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "预约提交失败: " + e.getMessage());
            return result;
        }
    }

    private String getPlayerName(Long userId) {
        try {
            String sql = "SELECT nick_name FROM sys_user WHERE user_id = ?";
            return jdbcTemplate.queryForObject(sql, String.class, userId);
        } catch (Exception e) {
            return "玩家" + userId;
        }
    }

    private String getPlayerPhone(Long userId) {
        try {
            String sql = "SELECT phonenumber FROM sys_user WHERE user_id = ?";
            return jdbcTemplate.queryForObject(sql, String.class, userId);
        } catch (Exception e) {
            return "";
        }
    }

    private boolean hasConflictReservation(Long userId, String reservationDate, String reservationTime) {
        try {
            String sql = "SELECT COUNT(*) FROM app_reservation " +
                    "WHERE user_id = ? AND reservation_date = ? AND reservation_time = ? " +
                    "AND status IN ('0', '1') AND del_flag = '0'";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, reservationDate, reservationTime);
            return count != null && count > 0;
        } catch (Exception e) {
            log.error("检查预约冲突失败: {}", e.getMessage());
            return false;
        }
    }

    public List<Map<String, Object>> getReservationList(Long userId) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        try {
            String sql = "SELECT reservation_id, table_type, reservation_date, reservation_time, " +
                    "duration, person_count, contact_name, contact_phone, remark, status, " +
                    "create_time FROM app_reservation " +
                    "WHERE user_id = ? AND del_flag = '0' ORDER BY reservation_date DESC, create_time DESC";
            
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, userId);
            for (Map<String, Object> row : rows) {
                Map<String, Object> item = new HashMap<>();
                item.put("reservationId", row.get("reservation_id"));
                item.put("tableType", row.get("table_type"));
                item.put("reservationDate", formatDate(row.get("reservation_date")));
                item.put("reservationTime", row.get("reservation_time"));
                item.put("duration", row.get("duration"));
                item.put("personCount", row.get("person_count"));
                item.put("contactName", row.get("contact_name"));
                item.put("contactPhone", row.get("contact_phone"));
                item.put("remark", row.get("remark"));
                item.put("status", row.get("status"));
                item.put("createTime", row.get("create_time"));
                resultList.add(item);
            }
            return resultList;
        } catch (Exception e) {
            log.error("查询预约列表失败: {}", e.getMessage(), e);
            return resultList;
        }
    }

    public Map<String, Object> getReservationDetail(Long userId, Long reservationId) {
        try {
            String sql = "SELECT reservation_id, table_type, reservation_date, reservation_time, " +
                    "duration, person_count, contact_name, contact_phone, remark, status, " +
                    "create_time FROM app_reservation " +
                    "WHERE reservation_id = ? AND user_id = ? AND del_flag = '0'";
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, reservationId, userId);
            
            if (results.isEmpty()) {
                return null;
            }
            
            Map<String, Object> row = results.get(0);
            Map<String, Object> detail = new HashMap<>();
            detail.put("reservationId", row.get("reservation_id"));
            detail.put("tableType", row.get("table_type"));
            detail.put("reservationDate", formatDate(row.get("reservation_date")));
            detail.put("reservationTime", row.get("reservation_time"));
            detail.put("duration", row.get("duration"));
            detail.put("personCount", row.get("person_count"));
            detail.put("contactName", row.get("contact_name"));
            detail.put("contactPhone", row.get("contact_phone"));
            detail.put("remark", row.get("remark"));
            detail.put("status", row.get("status"));
            detail.put("createTime", row.get("create_time"));
            return detail;
        } catch (Exception e) {
            log.error("查询预约详情失败: {}", e.getMessage(), e);
            return null;
        }
    }

    public boolean cancelReservation(Long userId, Long reservationId) {
        try {
            String sql = "UPDATE app_reservation SET status = '3', update_time = NOW() " +
                    "WHERE reservation_id = ? AND user_id = ? AND status = '0'";
            int rows = jdbcTemplate.update(sql, reservationId, userId);
            if (rows > 0) {
                jdbcTemplate.update("UPDATE sys_reservation SET status = '3', order_status = '3' " +
                        "WHERE reservation_id = ? AND player_id = ?", reservationId, userId);
                log.info("预约取消成功，ID: {}, 用户ID: {}", reservationId, userId);
            }
            return rows > 0;
        } catch (Exception e) {
            log.error("取消预约失败: {}", e.getMessage(), e);
            return false;
        }
    }

    public boolean updateReservation(Long userId, AppReservationVo reservation) {
        try {
            String sql = "UPDATE app_reservation SET table_type = ?, reservation_date = ?, " +
                    "reservation_time = ?, duration = ?, person_count = ?, contact_name = ?, " +
                    "contact_phone = ?, remark = ?, update_time = NOW() " +
                    "WHERE reservation_id = ? AND user_id = ? AND status = '0'";

            int rows = jdbcTemplate.update(sql,
                    reservation.getTableType(),
                    reservation.getReservationDate(),
                    reservation.getReservationTime(),
                    reservation.getDuration() != null ? reservation.getDuration() : 2,
                    reservation.getPersonCount() != null ? reservation.getPersonCount() : 4,
                    reservation.getContactName(),
                    reservation.getContactPhone(),
                    reservation.getRemark(),
                    reservation.getReservationId(),
                    userId);

            if (rows > 0) {
                String timeSlot = reservation.getReservationTime();
                String[] times = timeSlot.split("-");
                String startTime = times.length > 0 ? times[0].trim() : "00:00:00";
                String endTime = times.length > 1 ? times[1].trim() : "00:00:00";

                jdbcTemplate.update("UPDATE sys_reservation SET table_name = ?, reserve_date = ?, " +
                                "start_time = ?, end_time = ?, duration = ?, people_count = ?, remark = ? " +
                                "WHERE reservation_id = ? AND player_id = ?",
                        reservation.getTableType(), reservation.getReservationDate(),
                        startTime, endTime,
                        reservation.getDuration() != null ? reservation.getDuration() : 2,
                        reservation.getPersonCount() != null ? reservation.getPersonCount() : 4,
                        reservation.getRemark(),
                        reservation.getReservationId(), userId);

                log.info("预约更新成功，ID: {}, 用户ID: {}", reservation.getReservationId(), userId);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("更新预约失败: {}", e.getMessage(), e);
            return false;
        }
    }

    private String formatDate(Object date) {
        if (date == null) {
            return null;
        }
        String dateStr = date.toString();
        if (dateStr.contains(" ")) {
            return dateStr.split(" ")[0];
        }
        return dateStr;
    }
}
