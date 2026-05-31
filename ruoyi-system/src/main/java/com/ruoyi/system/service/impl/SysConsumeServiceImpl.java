package com.ruoyi.system.service.impl;

import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.system.mapper.SysConsumeMapper;
import com.ruoyi.system.domain.SysConsume;
import com.ruoyi.system.service.ISysConsumeService;
import com.ruoyi.system.service.PointsSyncService;
import com.ruoyi.system.service.VerificationCodeService;

@Service
public class SysConsumeServiceImpl implements ISysConsumeService {

    @Autowired
    private SysConsumeMapper consumeMapper;

    @Autowired
    private PointsSyncService pointsSyncService;

    @Autowired
    private VerificationCodeService verificationCodeService;

    @Override
    public SysConsume selectConsumeById(Long consumeId) {
        return consumeMapper.selectConsumeById(consumeId);
    }

    @Override
    public List<SysConsume> selectConsumeList(SysConsume consume) {
        return consumeMapper.selectConsumeList(consume);
    }

    @Override
    @Transactional
    public int insertConsume(SysConsume consume) {
        if (consume.getConsumeNo() == null) {
            consume.setConsumeNo(generateConsumeNo());
        }
        
        if (("success".equals(consume.getPayStatus()) || "1".equals(consume.getPayStatus())) && consume.getVerificationCode() == null) {
            consume.setVerificationCode(verificationCodeService.generateCode());
            consume.setVerificationStatus("UNVERIFIED");
        }
        
        int result = consumeMapper.insertConsume(consume);
        
        if (result > 0 && consume.getPlayerId() != null && consume.getTotalAmount() != null) {
            double amount = consume.getTotalAmount().doubleValue();
            pointsSyncService.awardPointsForConsumption(
                    consume.getPlayerId(), 
                    consume.getPlayerName(), 
                    amount);
        }
        
        return result;
    }

    @Override
    public int updateConsume(SysConsume consume) {
        return consumeMapper.updateConsume(consume);
    }

    @Override
    public int deleteConsumeByIds(Long[] consumeIds) {
        return consumeMapper.deleteConsumeByIds(consumeIds);
    }

    @Override
    public String generateConsumeNo() {
        return "XF" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    }
}
