package com.wagglex2.waggle.domain.study.service;

import com.wagglex2.waggle.domain.common.dto.response.RecruitmentWithAppsResponseDto;
import com.wagglex2.waggle.domain.common.type.RecruitmentStatus;
import com.wagglex2.waggle.domain.study.dto.request.StudyCreationRequestDto;
import com.wagglex2.waggle.domain.study.dto.request.StudySearchCondition;
import com.wagglex2.waggle.domain.study.dto.request.StudyUpdateRequestDto;
import com.wagglex2.waggle.domain.study.dto.response.StudyDetailResponseDto;
import com.wagglex2.waggle.domain.study.dto.response.StudySummaryResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StudyService {
    Long createStudy(StudyCreationRequestDto studyCreationRequestDto, Long userId);
    StudyDetailResponseDto getStudy(Long viewerId, Long studyId);
    Page<StudySummaryResponseDto> getStudySummaries(Long viewerId, StudySearchCondition condition, Pageable pageable);
    List<StudySummaryResponseDto> getStudySummariesByIds(Long viewerId, List<Long> studyIds);
    Page<RecruitmentWithAppsResponseDto> getAllByUserId(Long userId, Pageable pageable);
    Page<StudySummaryResponseDto> getBookmarkedStudiesByUserId(Long userId, RecruitmentStatus status, Pageable pageable);
    void updateStudy(Long userId, Long studyId, StudyUpdateRequestDto updateDto);
    void deleteStudy(Long userId, Long studyId);
}
