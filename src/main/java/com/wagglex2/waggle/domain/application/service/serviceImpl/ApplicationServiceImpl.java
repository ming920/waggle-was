package com.wagglex2.waggle.domain.application.service.serviceImpl;

import com.wagglex2.waggle.common.error.ErrorCode;
import com.wagglex2.waggle.common.exception.BusinessException;
import com.wagglex2.waggle.domain.application.dto.request.ApplicationCommonRequestDto;
import com.wagglex2.waggle.domain.application.dto.request.ApplicationProjectRequestDto;
import com.wagglex2.waggle.domain.application.dto.response.ApplicationCommonResponseDto;
import com.wagglex2.waggle.domain.application.dto.response.ApplicationProjectResponseDto;
import com.wagglex2.waggle.domain.application.dto.response.ApplicationSimpleResponseDto;
import com.wagglex2.waggle.domain.application.entity.Application;
import com.wagglex2.waggle.domain.application.repository.ApplicationRepository;
import com.wagglex2.waggle.domain.application.service.ApplicationService;
import com.wagglex2.waggle.domain.assignment.entity.Assignment;
import com.wagglex2.waggle.domain.common.entity.BaseRecruitment;
import com.wagglex2.waggle.domain.common.service.RecruitmentService;
import com.wagglex2.waggle.domain.common.type.ParticipantInfo;
import com.wagglex2.waggle.domain.common.type.PositionParticipantInfo;
import com.wagglex2.waggle.domain.common.type.RecruitmentCategory;
import com.wagglex2.waggle.domain.common.type.RecruitmentStatus;
import com.wagglex2.waggle.domain.project.entity.Project;
import com.wagglex2.waggle.domain.study.entity.Study;
import com.wagglex2.waggle.domain.user.entity.User;
import com.wagglex2.waggle.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserService userService;
    private final RecruitmentService recruitmentService;

    @PreAuthorize("#userId == authentication.principal.userId")
    @Transactional
    @Override
    public Long submitApplication(
            @P("userId") Long userId,
            Long recruitmentId,
            ApplicationCommonRequestDto requestDto
    ) {
        User applicant = userService.findById(userId);
        BaseRecruitment recruitment = recruitmentService.findById(recruitmentId);

        // 타 대학의 공고에 지원한 경우
        if (applicant.getUniversity() != recruitment.getUser().getUniversity()) {
            throw new BusinessException(ErrorCode.FORBIDDEN_CROSS_UNIVERSITY_RECRUITMENT);
        }

        // 요청한 공고 카테고리가 실제 카테고리와 일치하지 않는 경우
        if (requestDto.getCategory() != recruitment.getCategory()) {
            throw new BusinessException(ErrorCode.MISMATCHED_RECRUITMENT_CATEGORY);
        }

        // 모집 기간이 종료된 공고에 지원한 경우
        if (recruitment.getStatus() != RecruitmentStatus.RECRUITING) {
            throw new BusinessException(ErrorCode.RECRUITMENT_CLOSED);
        }

        // 본인 공고에 지원한 경우
        if (userId.equals(recruitment.getUser().getId())) {
            throw new BusinessException(ErrorCode.CANNOT_APPLY_OWN_RECRUITMENT);
        }

        // 동일한 공고에 중복 지원한 경우
        if (applicationRepository.existsByApplicantIdAndRecruitmentId(userId, recruitmentId)) {
            throw new BusinessException(ErrorCode.ALREADY_APPLIED_RECRUITMENT);
        }

        return switch (recruitment.getCategory()) {
            case PROJECT -> applyProject(applicant, (Project) recruitment, (ApplicationProjectRequestDto) requestDto);
            case ASSIGNMENT -> applyAssignment(applicant, (Assignment) recruitment, requestDto);
            case STUDY -> applyStudy(applicant, (Study) recruitment, requestDto);
        };
    }

    @Override
    public Application findById(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.APPLICATION_NOT_FOUND));
    }

    @PreAuthorize("#userId == authentication.principal.userId")
    @Override
    public Page<ApplicationCommonResponseDto> getAllByUserIdAndRecruitmentCategory(
            Long userId,
            RecruitmentCategory category,
            Pageable pageable
    ) {

        Page<Application> applications =
                applicationRepository.findAllByApplicantIdAndRecruitmentCategoryAndIsDeletedFalse(userId, category, pageable);

        // 지원 내역이 없는 경우
        if (applications.getContent().isEmpty()) {
            return Page.empty(pageable);
        }

        // 프로젝트 지원
        if (category == RecruitmentCategory.PROJECT) {
            return applications.map(ApplicationProjectResponseDto::fromEntity);
        }

        // 과제 / 스터디 지원
        return applications.map(ApplicationSimpleResponseDto::fromEntity);
    }

    @Transactional
    @Override
    public void closeApplicationsForClosedRecruitments() {
        int updated = applicationRepository.closeApplicationsForClosedRecruitments();

        log.info("[지원 상태 모집 종료 처리] 모집 종료 상태로 변경된 지원 건수: {}", updated);
    }

    private Long applyProject(User applicant, Project project, ApplicationProjectRequestDto requestDto) {
        // 지원한 포지션에 대한 정보 가져오기
        PositionParticipantInfo targetPositionInfo = project.getPositionInfoByRole(requestDto.getPosition())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_RECRUITING_POSITION));

        ParticipantInfo participantInfo = targetPositionInfo.getParticipantInfo();

        // 지원한 포지션의 모집이 이미 완료된 경우
        if (participantInfo.isFull()) {
            throw new BusinessException(ErrorCode.POSITION_FULL);
        }

        Application newApplication = requestDto.toEntity(applicant, project);

        return applicationRepository.save(newApplication).getId();
    }

    private Long applyAssignment(User applicant, Assignment assignment, ApplicationCommonRequestDto requestDto) {
        ParticipantInfo participantInfo = assignment.getParticipants();

        // 모집이 이미 완료된 경우
        if (participantInfo.isFull()) {
            throw new BusinessException(ErrorCode.RECRUITMENT_FULL);
        }

        Application newApplication = requestDto.toEntity(applicant, assignment);

        return applicationRepository.save(newApplication).getId();
    }

    private Long applyStudy(User applicant, Study study, ApplicationCommonRequestDto requestDto) {
        ParticipantInfo participantInfo = study.getParticipants();

        // 모집이 이미 완료된 경우
        if (participantInfo.isFull()) {
            throw new BusinessException(ErrorCode.RECRUITMENT_FULL);
        }

        Application newApplication = requestDto.toEntity(applicant, study);

        return applicationRepository.save(newApplication).getId();
    }

    @PreAuthorize("#userId == authentication.principal.userId")
    @Transactional
    @Override
    public void cancelApplication(Long userId, Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.APPLICATION_NOT_FOUND));

        // 권한 검증
        if (!userId.equals(application.getApplicant().getId())) {
            throw new BusinessException(ErrorCode.CANNOT_DELETE_ANOTHER_USER_APPLICATION);
        }

        // 이미 삭제 처리된 경우
        if (application.isDeleted()) {
            throw new BusinessException(ErrorCode.APPLICATION_NOT_FOUND);
        }

        // 논리적 삭제
        application.delete();
    }
}
