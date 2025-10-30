package com.wagglex2.waggle.domain.application.service.serviceImpl;

import com.wagglex2.waggle.common.error.ErrorCode;
import com.wagglex2.waggle.common.exception.BusinessException;
import com.wagglex2.waggle.domain.application.dto.request.ApplicationCommonRequestDto;
import com.wagglex2.waggle.domain.application.entity.Application;
import com.wagglex2.waggle.domain.application.repository.ApplicationRepository;
import com.wagglex2.waggle.domain.application.service.ApplicationService;
import com.wagglex2.waggle.domain.common.entity.BaseRecruitment;
import com.wagglex2.waggle.domain.common.service.RecruitmentService;
import com.wagglex2.waggle.domain.common.type.RecruitmentStatus;
import com.wagglex2.waggle.domain.user.entity.User;
import com.wagglex2.waggle.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserService userService;
    private final RecruitmentService recruitmentService;

    @PreAuthorize("#userId == authentication.principal.userId")
    @Transactional
    public Long submitApplication(
            @P("userId") Long userId,
            Long recruitmentId,
            ApplicationCommonRequestDto requestDto
    ) {
        BaseRecruitment recruitment = recruitmentService.findById(recruitmentId);

        // 요청한 공고 카테고리가 실제 카테고리와 일치하지 않는 경우
        if (requestDto.getCategory() != recruitment.getCategory()) {
            throw new BusinessException(ErrorCode.MISMATCHED_RECRUITMENT_CATEGORY);
        }

        // 모집이 마감된 공고에 지원한 경우
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

        User applicant = userService.findById(userId);
        Application newApplication = requestDto.toEntity(applicant, recruitment);

        return applicationRepository.save(newApplication).getId();
    }
}
