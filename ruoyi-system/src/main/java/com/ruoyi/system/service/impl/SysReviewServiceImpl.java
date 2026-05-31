package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.SysReviewMapper;
import com.ruoyi.system.domain.SysReview;
import com.ruoyi.system.service.ISysReviewService;

@Service
public class SysReviewServiceImpl implements ISysReviewService {

    @Autowired
    private SysReviewMapper reviewMapper;

    @Override
    public SysReview selectReviewById(Long reviewId) {
        return reviewMapper.selectReviewById(reviewId);
    }

    @Override
    public List<SysReview> selectReviewList(SysReview review) {
        return reviewMapper.selectReviewList(review);
    }

    @Override
    public int insertReview(SysReview review) {
        return reviewMapper.insertReview(review);
    }

    @Override
    public int updateReview(SysReview review) {
        return reviewMapper.updateReview(review);
    }

    @Override
    public int deleteReviewByIds(Long[] reviewIds) {
        return reviewMapper.deleteReviewByIds(reviewIds);
    }
}
