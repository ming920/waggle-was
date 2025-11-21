package com.wagglex2.waggle.domain.project.service;

import com.wagglex2.waggle.domain.common.dto.response.RecruitmentWithAppsResponseDto;
import com.wagglex2.waggle.domain.project.dto.request.ProjectCreationRequestDto;
import com.wagglex2.waggle.domain.project.dto.request.ProjectSearchCondition;
import com.wagglex2.waggle.domain.project.dto.request.ProjectUpdateRequestDto;
import com.wagglex2.waggle.domain.project.dto.response.ProjectDetailResponseDto;
import com.wagglex2.waggle.domain.project.dto.response.ProjectSummaryResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProjectService {
    Long createProject(Long userId, ProjectCreationRequestDto projectCreationRequestDto);
    ProjectDetailResponseDto getProject(Long viewerId, Long projectId);
    Page<ProjectSummaryResponseDto> getProjectSummaries(Long viewerId, ProjectSearchCondition condition, Pageable pageable);
    List<ProjectSummaryResponseDto> getProjectSummariesByIds(Long viewerId, List<Long> projectIds);
    Page<RecruitmentWithAppsResponseDto> getAllByUserId(Long userId, Pageable pageable);
    void updateProject(Long userId, Long projectId, ProjectUpdateRequestDto updateDto);
    void deleteProject(Long userId, Long projectId);
}
