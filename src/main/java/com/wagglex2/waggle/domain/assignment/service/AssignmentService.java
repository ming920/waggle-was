package com.wagglex2.waggle.domain.assignment.service;

import com.wagglex2.waggle.domain.assignment.dto.request.AssignmentCreationRequestDto;
import com.wagglex2.waggle.domain.assignment.dto.request.AssignmentSearchCondition;
import com.wagglex2.waggle.domain.assignment.dto.request.AssignmentUpdateRequestDto;
import com.wagglex2.waggle.domain.assignment.dto.response.AssignmentDetailResponseDto;
import com.wagglex2.waggle.domain.assignment.dto.response.AssignmentSummaryResponseDto;
import com.wagglex2.waggle.domain.common.dto.response.RecruitmentWithAppsResponseDto;
import com.wagglex2.waggle.domain.common.type.RecruitmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AssignmentService {
    Long createAssignment(AssignmentCreationRequestDto assignmentCreationRequestDto, Long userId);
    AssignmentDetailResponseDto getAssignment(Long viewerId, Long assignmentId);
    Page<AssignmentSummaryResponseDto> getAssignmentSummaries(Long viewerId, AssignmentSearchCondition condition, Pageable pageable);
    List<AssignmentSummaryResponseDto> getAssignmentSummariesByIds(Long viewerId, List<Long> assignmentIds);
    Page<AssignmentSummaryResponseDto> getBookmarkedAssignmentsByUserId(Long userId, RecruitmentStatus status, Pageable pageable);
    Page<RecruitmentWithAppsResponseDto> getAllByUserId(Long userId, Pageable pageable);
    void updateAssignment(Long userId, Long assignmentId, AssignmentUpdateRequestDto updateDto);
    void deleteAssignment(Long userId, Long assignmentId);
}
