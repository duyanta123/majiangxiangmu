package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.SysReservationMapper;
import com.ruoyi.system.domain.SysReservation;
import com.ruoyi.system.service.ISysReservationService;

@Service
public class SysReservationServiceImpl implements ISysReservationService {

    @Autowired
    private SysReservationMapper reservationMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public SysReservation selectReservationById(Long reservationId) {
        return reservationMapper.selectReservationById(reservationId);
    }

    @Override
    public List<SysReservation> selectReservationList(SysReservation reservation) {
        return reservationMapper.selectReservationList(reservation);
    }

    @Override
    public int insertReservation(SysReservation reservation) {
        return reservationMapper.insertReservation(reservation);
    }

    @Override
    public int updateReservation(SysReservation reservation) {
        int result = reservationMapper.updateReservation(reservation);
        if (result > 0 && reservation.getAppReservationId() != null) {
            String status = reservation.getReservationStatus();
            if (status != null) {
                String appStatus = mapToAppStatus(status);
                String sql = "UPDATE app_reservation SET status = ?, update_time = NOW() WHERE reservation_id = ?";
                jdbcTemplate.update(sql, appStatus, reservation.getAppReservationId());
            }
        }
        return result;
    }

    private String mapToAppStatus(String adminStatus) {
        switch (adminStatus) {
            case "1": return "1";
            case "2": return "2";
            case "3": return "3";
            case "4": return "4";
            default: return "0";
        }
    }

    @Override
    public int deleteReservationByIds(Long[] reservationIds) {
        return reservationMapper.deleteReservationByIds(reservationIds);
    }

    @Override
    public boolean checkReservationCodeUnique(String reservationCode) {
        SysReservation r = reservationMapper.checkReservationCodeUnique(reservationCode);
        return r == null;
    }
}
