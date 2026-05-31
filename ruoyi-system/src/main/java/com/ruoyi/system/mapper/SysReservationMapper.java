package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysReservation;
import org.apache.ibatis.annotations.Param;

public interface SysReservationMapper {
    public SysReservation selectReservationById(Long reservationId);
    public List<SysReservation> selectReservationList(SysReservation reservation);
    public int insertReservation(SysReservation reservation);
    public int updateReservation(SysReservation reservation);
    public int deleteReservationByIds(Long[] reservationIds);
    public SysReservation checkReservationCodeUnique(@Param("reservationCode") String reservationCode);
}
