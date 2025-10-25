package com.wagglex2.waggle.domain.review.entity.type;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ReviewStatus {
    ACTIVE("활성"),
    DELETED("삭제");

    private final String desc;

    public String getName() { return this.name(); }
}
