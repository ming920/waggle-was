package com.wagglex2.waggle.domain.application.type;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ApplicationStatus {
    SUBMITTED("대기중"),
    ACCEPTED("수락됨"),
    REJECTED("거절됨"),
    CLOSED("모집종료"),
    CANCELED("모집취소");

    private final String desc;

    public String getName() {
        return name();
    }
}
