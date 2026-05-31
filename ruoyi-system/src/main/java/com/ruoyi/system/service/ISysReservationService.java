package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.SysReservation;

public interface ISysReservationService {
    public SysReservation selectReservationById(Long reservationId);
    public List<SysReservation> selectReservationList(SysReservation reservation);
    public int insertReservation(SysReservation reservation);
    public int updateReservation(SysReservation reservation);
    public int deleteReservationByIds(Long[] reservationIds);
    public boolean checkReservationCodeUnique(String reservationCode);
}
