package com.wagglex2.waggle.domain.notification.repository;

import com.wagglex2.waggle.domain.common.type.RecruitmentCategory;
import com.wagglex2.waggle.domain.notification.dto.response.NotificationResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationRepositoryCustom {

    /**
     * 지정된 사용자 ID와 카테고리에 해당하는 알림 목록을 페이지 단위로 조회한다.
     *
     * @param receiverId 수신자 ID
     * @param category 조회할 알림의 카테고리 (nullable, null이면 모든 카테고리 포함)
     * @param pageable 페이징 및 정렬 정보
     * @return {@code Page<NotificationResponseDto>} - 지정된 사용자와 카테고리에 해당하는 알림의 페이지
     */
    Page<NotificationResponseDto> getAllByUserIdAndCategory(Long receiverId, RecruitmentCategory category, Pageable pageable);
}
