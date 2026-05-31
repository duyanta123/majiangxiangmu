package com.ruoyi.system.service;

import com.ruoyi.system.domain.SysConsume;

import java.util.Map;

public interface VerificationCodeService {

    String generateCode();

    SysConsume verifyCode(String code, String verifierId, String verifierName);

    SysConsume getConsumeByCode(String code);
}