package com.wagglex2.waggle.domain.review.entity.type;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 리뷰 상태(ReviewStatus) Enum
 *
 * <p>리뷰의 현재 노출 상태를 관리한다.
 * Soft Delete 정책을 사용하기 때문에 DB에서는 삭제되지 않지만,
 * 상태 필드를 통해 사용자에게 노출 여부를 제어한다.
 *
 * <p><b>정책 요약</b>
 * <ul>
 *   <li>{@code ACTIVE} : 사용자에게 정상적으로 노출되는 리뷰</li>
 *   <li>{@code DELETED} : 사용자가 삭제한 리뷰 (비노출 상태)</li>
 * </ul>
 */

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ReviewStatus {
    ACTIVE("활성"),
    DELETED("삭제");

    private final String desc;

    public String getName() { return this.name(); }
}
