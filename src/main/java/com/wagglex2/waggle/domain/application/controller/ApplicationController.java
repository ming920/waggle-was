package com.wagglex2.waggle.domain.application.controller;

import com.wagglex2.waggle.common.response.ApiResponse;
import com.wagglex2.waggle.common.security.CustomUserDetails;
import com.wagglex2.waggle.domain.application.dto.request.ApplicationCommonRequestDto;
import com.wagglex2.waggle.domain.application.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping("/recruitments/{recruitmentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Long>> submitProjectApplication(
            @PathVariable("recruitmentId") Long recruitmentId,
            @RequestBody @Valid ApplicationCommonRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long applicationId = applicationService.submitApplication(
                userDetails.getUserId(),
                recruitmentId,
                requestDto
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("지원서가 성공적으로 제출되었습니다.", applicationId));
    }
}
