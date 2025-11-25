package com.wagglex2.waggle.domain.application.service.serviceImpl;

import com.wagglex2.waggle.common.error.ErrorCode;
import com.wagglex2.waggle.common.exception.BusinessException;
import com.wagglex2.waggle.domain.application.dto.request.ApplicationCommonRequestDto;
import com.wagglex2.waggle.domain.application.dto.request.ApplicationProjectRequestDto;
import com.wagglex2.waggle.domain.application.dto.response.ApplicationCommonResponseDto;
import com.wagglex2.waggle.domain.application.dto.response.ApplicationProjectResponseDto;
import com.wagglex2.waggle.domain.application.dto.response.ApplicationSimpleResponseDto;
import com.wagglex2.waggle.domain.application.entity.Application;
import com.wagglex2.waggle.domain.application.event.ApplicationProcessedEvent;
import com.wagglex2.waggle.domain.application.repository.ApplicationRepository;
import com.wagglex2.waggle.domain.application.service.ApplicationService;
import com.wagglex2.waggle.domain.application.type.ApplicationStatus;
import com.wagglex2.waggle.domain.assignment.entity.Assignment;
import com.wagglex2.waggle.domain.common.entity.BaseRecruitment;
import com.wagglex2.waggle.domain.common.service.RecruitmentService;
import com.wagglex2.waggle.domain.common.type.ParticipantInfo;
import com.wagglex2.waggle.domain.common.type.PositionParticipantInfo;
import com.wagglex2.waggle.domain.common.type.RecruitmentCategory;
import com.wagglex2.waggle.domain.common.type.RecruitmentStatus;
import com.wagglex2.waggle.domain.notification.type.NotificationType;
import com.wagglex2.waggle.domain.project.entity.Project;
import com.wagglex2.waggle.domain.project.type.MeetingType;
import com.wagglex2.waggle.domain.study.entity.Study;
import com.wagglex2.waggle.domain.team.entity.Team;
import com.wagglex2.waggle.domain.team.service.TeamService;
import com.wagglex2.waggle.domain.team_member.entity.TeamMember;
import com.wagglex2.waggle.domain.team_member.service.TeamMemberService;
import com.wagglex2.waggle.domain.user.entity.User;
import com.wagglex2.waggle.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserService userService;
    private final RecruitmentService recruitmentService;
    private final TeamService teamService;
    private final TeamMemberService teamMemberService;
    private final ApplicationEventPublisher publisher;

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
        if (applicationRepository.existsByApplicantIdAndRecruitmentIdAndIsDeletedFalse(userId, recruitmentId)) {
            throw new BusinessException(ErrorCode.ALREADY_APPLIED_RECRUITMENT);
        }

        Long applicationId = switch (recruitment.getCategory()) {
            case PROJECT -> applyProject(applicant, (Project) recruitment, (ApplicationProjectRequestDto) requestDto);
            case ASSIGNMENT -> applyAssignment(applicant, (Assignment) recruitment, requestDto);
            case STUDY -> applyStudy(applicant, (Study) recruitment, requestDto);
        };

        // 이벤트 발행
        publisher.publishEvent(new ApplicationProcessedEvent(
                applicant.getId(),
                recruitment.getUser().getId(),
                applicationId,
                NotificationType.APPLICATION_SUBMITTED
        ));

        return applicationId;
    }

    @Override
    public Application findById(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.APPLICATION_NOT_FOUND));
    }

    @Override
    public List<Application> findAllByRecruitmentIds(List<Long> recruitmentIds) {
        return applicationRepository.findAllByRecruitmentIds(recruitmentIds);
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

    @PreAuthorize("#deciderId == authentication.principal.userId")
    @Transactional
    @Retryable(
            retryFor = ObjectOptimisticLockingFailureException.class,
            noRetryFor = BusinessException.class,
            maxAttempts = 3
    )
    @Override
    public void acceptApplication(@P("deciderId") Long deciderId, Long applicationId) {
        Application application =
                applicationRepository.findByIdAndNotDeletedWithRecruitmentAndAuthor(applicationId)
                        .orElseThrow(() -> new BusinessException(ErrorCode.APPLICATION_NOT_FOUND));

        BaseRecruitment recruitment = application.getRecruitment();

        // 수락/거절 자격 확인(공고 작성자인지 확인)
        if (!deciderId.equals(recruitment.getUser().getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN_DECIDE_APPLICATION, "지원 수락 권한이 없습니다.");
        }

        // 이미 처리된 지원서인 경우
        if (application.getStatus() != ApplicationStatus.SUBMITTED) {
            throw new BusinessException(ErrorCode.ALREADY_PROCESSED_APPLICATION);
        }

        // 처리 로직
        ParticipantInfo participantInfo = null;

        switch (recruitment.getCategory()) {
            case PROJECT -> {
                Project project = (Project) recruitment;
                participantInfo = project.getPositionInfoByRole(application.getPosition())
                        .orElseThrow(() -> new BusinessException(ErrorCode.NOT_RECRUITING_POSITION))
                        .getParticipantInfo();

                // 지원한 포지션의 모집이 이미 완료된 경우
                if (participantInfo.isFull()) {
                    throw new BusinessException(ErrorCode.POSITION_FULL);
                }
            }
            case ASSIGNMENT -> {
                Assignment assignment = (Assignment) recruitment;
                participantInfo = assignment.getParticipants();

                // 모집이 이미 완료된 경우
                if (participantInfo.isFull()) {
                    throw new BusinessException(ErrorCode.TEAM_FULL);
                }
            }
            case STUDY -> {
                Study study = (Study) recruitment;
                participantInfo = study.getParticipants();

                // 모집이 이미 완료된 경우
                if (participantInfo.isFull()) {
                    throw new BusinessException(ErrorCode.TEAM_FULL);
                }
            }
        }

        application.accept();

        // 참여 인원 업데이트
        participantInfo.incrementCurrParticipants();

        // 팀에 추가
        Team team = teamService.findByRecruitmentId(recruitment.getId());
        TeamMember newMember = teamMemberService.createMember(team, application);
        team.addMember(newMember);

        // 이벤트 발행
        publisher.publishEvent(new ApplicationProcessedEvent(
                deciderId,
                application.getApplicant().getId(),
                applicationId,
                NotificationType.APPLICATION_ACCEPTED
        ));
    }

    @PreAuthorize("#deciderId == authentication.principal.userId")
    @Transactional
    @Override
    public void rejectApplication(Long deciderId, Long applicationId) {
        Application application =
                applicationRepository.findByIdAndNotDeletedWithRecruitmentAndAuthor(applicationId)
                        .orElseThrow(() -> new BusinessException(ErrorCode.APPLICATION_NOT_FOUND));

        BaseRecruitment recruitment = application.getRecruitment();

        // 수락/거절 자격 확인(공고 작성자인지 확인)
        if (!deciderId.equals(recruitment.getUser().getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN_DECIDE_APPLICATION, "지원 거절 권한이 없습니다.");
        }

        // 이미 처리된 지원서인 경우
        if (application.getStatus() != ApplicationStatus.SUBMITTED) {
            throw new BusinessException(ErrorCode.ALREADY_PROCESSED_APPLICATION);
        }

        // 처리 로직
        application.reject();

        // 이벤트 발행
        publisher.publishEvent(new ApplicationProcessedEvent(
                deciderId,
                application.getApplicant().getId(),
                applicationId,
                NotificationType.APPLICATION_REJECTED
        ));
    }

    @PreAuthorize("#userId == authentication.principal.userId")
    @Transactional
    @Override
    public void deleteApplication(Long userId, Long applicationId) {
        Application application = findById(applicationId);

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

    @Transactional
    @Override
    public void cancelApplication(Long recruitmentId) {
        applicationRepository.cancelAllByRecruitmentId(recruitmentId);
    }

    @Transactional
    @Override
    public void closeApplicationsForClosedRecruitments() {
        int updated = applicationRepository.closeApplicationsForClosedRecruitments();

        log.info("[지원 상태 모집 종료 처리] 모집 종료 상태로 변경된 지원 건수: {}", updated);
    }

    private Long applyProject(User applicant, Project project, ApplicationProjectRequestDto requestDto) {
        MeetingType recruitingMeetingType = project.getMeetingType();
        MeetingType appliedMeetingType = requestDto.getMeetingType();

        // 모집하는 진행 방식에 해당되지 않는 경우
        if (recruitingMeetingType != MeetingType.HYBRID && appliedMeetingType != MeetingType.HYBRID) {
            if (recruitingMeetingType != appliedMeetingType) {
                throw new BusinessException(ErrorCode.MISMATCHED_MEETING_TYPE);
            }
        }

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

    /**
     * 재시도 실패 시 처리
     */
    @Recover
    protected void recover(ObjectOptimisticLockingFailureException e, Long deciderId, Long applicationId) {
        throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
    }
}
