package com.wagglex2.waggle.domain.application.controller;

import com.wagglex2.waggle.common.response.ApiResponse;
import com.wagglex2.waggle.common.security.CustomUserDetails;
import com.wagglex2.waggle.domain.application.dto.request.ApplicationCommonRequestDto;
import com.wagglex2.waggle.domain.application.dto.response.ApplicationCommonResponseDto;
import com.wagglex2.waggle.domain.application.service.ApplicationService;
import com.wagglex2.waggle.domain.common.type.RecruitmentCategory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<ApplicationCommonResponseDto>>> getMyApplicationByCategory(
            @RequestParam("category") RecruitmentCategory category,
            @PageableDefault(size = 5) Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        PageRequest pageRequest = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<ApplicationCommonResponseDto> applications = applicationService.getAllByUserIdAndRecruitmentCategory(
                userDetails.getUserId(),
                category,
                pageRequest
        );

        return ResponseEntity.ok(
                ApiResponse.ok(category.getDesc() + " 공고 지원 내역을 성공적으로 조회하였습니다.", applications)
        );
    }

    @PostMapping("{applicationId}/accept")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> acceptApplication(
            @PathVariable("applicationId") Long applicationId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        applicationService.acceptApplication(userDetails.getUserId(), applicationId);

        return ResponseEntity.ok(ApiResponse.ok("공고 지원 요청을 수락하였습니다."));
    }

    @DeleteMapping("{applicationId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> cancelApplication(
            @PathVariable("applicationId") Long applicationId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        applicationService.cancelApplication(userDetails.getUserId(), applicationId);

        return ResponseEntity.ok(
                ApiResponse.ok("지원서가 성공적으로 삭제되었습니다.")
        );
    }
}
