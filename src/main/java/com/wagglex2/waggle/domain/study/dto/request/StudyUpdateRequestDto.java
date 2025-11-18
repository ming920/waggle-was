package com.wagglex2.waggle.domain.study.dto.request;

import com.wagglex2.waggle.domain.common.dto.request.ParticipantInfoUpdateRequestDto;
import com.wagglex2.waggle.domain.common.dto.request.PeriodRequestDto;
import com.wagglex2.waggle.domain.common.type.Skill;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
public class StudyUpdateRequestDto extends StudyCommonRequestDto {

    @NotNull(message = "참여 인원 정보가 누락되었습니다.")
    @Valid
    private final ParticipantInfoUpdateRequestDto participants;

    public StudyUpdateRequestDto(
            String title, String content, LocalDateTime deadline,
            PeriodRequestDto period, Set<Skill> skills,
            ParticipantInfoUpdateRequestDto participants
    ) {
        super(title, content, deadline, period, skills);
        this.participants = participants;
    }
}
