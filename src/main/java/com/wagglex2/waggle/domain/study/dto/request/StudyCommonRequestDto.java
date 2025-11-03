package com.wagglex2.waggle.domain.study.dto.request;

import com.wagglex2.waggle.common.error.ErrorCode;
import com.wagglex2.waggle.common.exception.BusinessException;
import com.wagglex2.waggle.domain.common.dto.request.BaseRecruitmentRequestDto;
import com.wagglex2.waggle.domain.common.dto.request.PeriodRequestDto;
import com.wagglex2.waggle.domain.common.type.Skill;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
public class StudyCommonRequestDto extends BaseRecruitmentRequestDto {

    @NotNull(message = "진행 기간 정보가 누락되었습니다.")
    @Valid
    private final PeriodRequestDto period;

    @NotEmpty(message = "기술 스택 정보가 누락되었습니다.")
    private final Set<Skill> skills;

    protected StudyCommonRequestDto(
            String title, String content, LocalDateTime deadline,
            PeriodRequestDto period, Set<Skill> skills
    ) {
        super(title, content, deadline);
        this.period = period;
        this.skills = skills;
    }

    public void validate() {
        if (getDeadline() != null && period != null &&
                period.endDate() != null &&
                getDeadline().isAfter(period.endDate().atTime(23, 59, 59))) {
            throw new BusinessException(ErrorCode.INVALID_DATE_RANGE, "마감일은 종료일 이전이어야 합니다.");
        }
    }
}
