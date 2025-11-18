package com.wagglex2.waggle.domain.study.service;

import com.wagglex2.waggle.domain.study.dto.request.StudyCreationRequestDto;
import com.wagglex2.waggle.domain.study.dto.request.StudyUpdateRequestDto;
import com.wagglex2.waggle.domain.study.dto.response.StudyResponseDto;

public interface StudyService {
    Long createStudy(StudyCreationRequestDto studyCreationRequestDto, Long userId);
    StudyResponseDto getStudy(Long viewerId, Long studyId);
    void updateStudy(Long userId, Long studyId, StudyUpdateRequestDto updateDto);
    void deleteStudy(Long userId, Long studyId);
}
