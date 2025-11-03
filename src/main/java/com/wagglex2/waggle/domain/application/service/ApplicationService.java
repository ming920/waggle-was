package com.wagglex2.waggle.domain.application.service;

import com.wagglex2.waggle.domain.application.dto.request.ApplicationCommonRequestDto;
import com.wagglex2.waggle.domain.application.entity.Application;
import com.wagglex2.waggle.domain.application.dto.response.ApplicationCommonResponseDto;
import com.wagglex2.waggle.domain.common.type.RecruitmentCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ApplicationService {

    Long submitApplication(Long userId, Long recruitmentId, ApplicationCommonRequestDto requestDto);
    Application findById(Long id);
    Page<ApplicationCommonResponseDto> getAllByUserIdAndRecruitmentCategory(Long userId, RecruitmentCategory category, Pageable pageable);
    void cancelApplication(Long userId, Long applicationId);

    /**
     * 마감된 공고에 대한 모든 지원 상태를 CLOSED로 변경한다.
     */
    void closeApplicationsForClosedRecruitments();
}
