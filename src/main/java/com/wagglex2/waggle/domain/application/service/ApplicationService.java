package com.wagglex2.waggle.domain.application.service;

import com.wagglex2.waggle.domain.application.dto.request.ApplicationCommonRequestDto;
import com.wagglex2.waggle.domain.application.entity.Application;
import com.wagglex2.waggle.domain.application.dto.response.ApplicationCommonResponseDto;
import com.wagglex2.waggle.domain.application.event.ApplicationEventListener;
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
    void deleteApplication(Long userId, Long applicationId);

    /**
     * 특정 공고가 삭제될 경우, 해당 공고에 대한 모든 지원 내역을 취소 상태로 변경한다.
     * <p>
     * 이 메서드는 {@link ApplicationEventListener} 이벤트 리스너에서 호출되어
     * 공고 삭제에 따른 지원 상태 변경을 처리한다.
     *
     * @param recruitmentId 삭제된 공고의 ID
     */
    void cancelApplication(Long recruitmentId);

    /**
     * 마감 상태였던 공고가 마감일 수정으로 인해 다시 모집 가능 상태(RECRUITING)가 된 경우,
     * 해당 공고에 대한 모든 지원서를 '제출됨' 상태로 되돌린다.
     * <p>
     * 이 메서드는 {@link ApplicationEventListener} 이벤트 리스너에서 호출되어
     * 마감일 수정에 따른 지원 상태 변경을 처리한다.
     *
     * @param recruitmentId 마감 상태에서 모집 상태로 변경된 공고의 ID
     */
    void updateAllByRecruitmentReopened(Long recruitmentId);

    /**
     * 마감된 공고에 대한 모든 지원 상태를 CLOSED로 변경한다.
     */
    void closeApplicationsForClosedRecruitments();
}
