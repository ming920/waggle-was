package com.wagglex2.waggle.domain.project.event;

import com.wagglex2.waggle.domain.common.type.PositionType;

/**
 * 프로젝트 공고 생성 이벤트
 *
 * @param authorId 작성자 ID
 * @param projectId 생성된 프로젝트 공고 ID
 * @param authorPosition 작성자 포지션
 */
public record ProjectCreatedEvent(
        Long authorId,
        Long projectId,
        PositionType authorPosition
) {
}
