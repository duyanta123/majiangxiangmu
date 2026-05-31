package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.SysConsume;
import com.ruoyi.system.mapper.SysConsumeMapper;
import com.ruoyi.system.service.VerificationCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Random;

@Service
public class VerificationCodeServiceImpl implements VerificationCodeService {

    @Autowired
    private SysConsumeMapper consumeMapper;

    private static final String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int CODE_LENGTH = 8;

    @Override
    public String generateCode() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    @Override
    @Transactional
    public SysConsume verifyCode(String code, String verifierId, String verifierName) {
        SysConsume consume = consumeMapper.selectConsumeByVerificationCode(code);
        
        if (consume == null) {
            throw new RuntimeException("无效的核销码");
        }
        
        if ("VERIFIED".equals(consume.getVerificationStatus())) {
            throw new RuntimeException("该订单已被核销");
        }
        
        if (!"success".equals(consume.getPayStatus()) && !"1".equals(consume.getPayStatus())) {
            throw new RuntimeException("订单未支付，无法核销");
        }
        
        consume.setVerificationStatus("VERIFIED");
        consume.setVerificationTime(new Date());
        consume.setVerifierId(verifierId);
        consume.setVerifierName(verifierName);
        
        consumeMapper.updateConsume(consume);
        
        return consume;
    }

    @Override
    public SysConsume getConsumeByCode(String code) {
        return consumeMapper.selectConsumeByVerificationCode(code);
    }
}