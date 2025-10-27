package com.wagglex2.waggle.domain.common.service.serviceImpl;

import com.wagglex2.waggle.common.error.ErrorCode;
import com.wagglex2.waggle.common.exception.BusinessException;
import com.wagglex2.waggle.domain.common.entity.BaseRecruitment;
import com.wagglex2.waggle.domain.common.repository.RecruitmentRepository;
import com.wagglex2.waggle.domain.common.service.RecruitmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecruitmentServiceImpl implements RecruitmentService {

    private final RecruitmentRepository recruitmentRepository;

    @Override
    public BaseRecruitment findById(Long recruitmentId) {
        return recruitmentRepository.findByIdNotCanceled(recruitmentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RECRUITMENT_NOT_FOUND));
    }
}
