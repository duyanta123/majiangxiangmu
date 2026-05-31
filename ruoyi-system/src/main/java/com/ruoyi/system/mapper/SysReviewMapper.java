package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysReview;

public interface SysReviewMapper {
    public SysReview selectReviewById(Long reviewId);
    public List<SysReview> selectReviewList(SysReview review);
    public int insertReview(SysReview review);
    public int updateReview(SysReview review);
    public int deleteReviewByIds(Long[] reviewIds);
}
