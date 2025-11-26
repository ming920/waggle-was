package com.wagglex2.waggle.domain.common.event;

/**
 * 과제/스터디 공고 생성 이벤트
 *
 * @param authorId 공고 작성자 ID
 * @param recruitmentId 생성된 과제/스터디 공고 ID
 */
public record SimpleRecruitmentCreatedEvent(
        Long authorId,
        Long recruitmentId
) {
}
