package com.wagglex2.waggle.domain.common.service;

import com.wagglex2.waggle.domain.common.entity.BaseRecruitment;

public interface RecruitmentService {

    BaseRecruitment findById(Long recruitmentId);

    /**
     * 마감일이 지난 모집 공고의 상태를 CLOSED로 변경
     */
    void closeExpiredRecruitments();

    BaseRecruitment findByIdNotCanceled(Long recruitmentId);
}
