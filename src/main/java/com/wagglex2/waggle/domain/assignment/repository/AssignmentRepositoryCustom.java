package com.wagglex2.waggle.domain.assignment.repository;

import com.wagglex2.waggle.domain.assignment.dto.request.AssignmentSearchCondition;
import com.wagglex2.waggle.domain.assignment.dto.response.AssignmentSummaryResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * QueryDSL을 활용한 Assignment 커스텀 리포지토리
 */
public interface AssignmentRepositoryCustom {

    /**
     * 검색 조건({@link AssignmentSearchCondition})에 맞는 프로젝트 요약 DTO를 페이징 조회
     * @param condition 검색 조건 ({@link AssignmentSearchCondition})
     * @param pageable  페이징 및 정렬 정보 ({@link org.springframework.data.domain.Pageable})
     * @return 조건에 맞는 프로젝트 요약 DTO 페이지 ({@link org.springframework.data.domain.Page}&lt;{@link AssignmentSummaryResponseDto}&gt;)
     */
    Page<AssignmentSummaryResponseDto> findAssignmentSummaries(AssignmentSearchCondition condition, Pageable pageable);
}
