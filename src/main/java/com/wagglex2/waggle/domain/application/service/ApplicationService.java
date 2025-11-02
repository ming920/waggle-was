package com.wagglex2.waggle.domain.application.service;

import com.wagglex2.waggle.domain.application.dto.request.ApplicationCommonRequestDto;

public interface ApplicationService {

    Long submitApplication(Long userId, Long recruitmentId, ApplicationCommonRequestDto requestDto);
}
