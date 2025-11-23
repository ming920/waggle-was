package com.wagglex2.waggle.domain.study.service;

import com.wagglex2.waggle.domain.common.dto.response.RecruitmentWithAppsResponseDto;
import com.wagglex2.waggle.domain.study.dto.request.StudyCreationRequestDto;
import com.wagglex2.waggle.domain.study.dto.request.StudyUpdateRequestDto;
import com.wagglex2.waggle.domain.study.dto.response.StudyDetailResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StudyService {
    Long createStudy(StudyCreationRequestDto studyCreationRequestDto, Long userId);
    StudyDetailResponseDto getStudy(Long viewerId, Long studyId);
    Page<RecruitmentWithAppsResponseDto> getAllByUserId(Long userId, Pageable pageable);
    void updateStudy(Long userId, Long studyId, StudyUpdateRequestDto updateDto);
    void deleteStudy(Long userId, Long studyId);
}
