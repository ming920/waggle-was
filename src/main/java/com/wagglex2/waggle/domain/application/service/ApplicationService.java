package com.wagglex2.waggle.domain.application.service;

import com.wagglex2.waggle.domain.application.dto.request.ApplicationCommonRequestDto;
import com.wagglex2.waggle.domain.application.entity.Application;
import com.wagglex2.waggle.domain.application.dto.response.ApplicationCommonResponseDto;
import com.wagglex2.waggle.domain.common.type.RecruitmentCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ApplicationService {

    Long submitApplication(Long userId, Long recruitmentId, ApplicationCommonRequestDto requestDto);
    Application findById(Long id);

    /**
     * 주어진 모집 공고 ID 목록에 해당하는 제출된 지원서를 조회한다.
     *
     * <p>삭제되지 않은 지원서만 포함하며, 연관된 모집 공고와 작성자, 기술 스택을 함께 조회한다.</p>
     *
     * @param recruitmentId 조회할 모집 공고 ID 목록
     * @return 조건에 맞는 지원서 목록을 반환한다
     */
    List<Application> findAllByRecruitmentIds(List<Long> recruitmentId);
    Page<ApplicationCommonResponseDto> getAllByUserIdAndRecruitmentCategory(Long userId, RecruitmentCategory category, Pageable pageable);
    void acceptApplication(Long deciderId, Long applicationId);
    void rejectApplication(Long deciderId, Long applicationId);
    void cancelApplication(Long userId, Long applicationId);

    /**
     * 마감된 공고에 대한 모든 지원 상태를 CLOSED로 변경한다.
     */
    void closeApplicationsForClosedRecruitments();
}
