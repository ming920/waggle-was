package com.wagglex2.waggle.domain.study.controller;


import com.wagglex2.waggle.common.response.APIResponse;
import com.wagglex2.waggle.common.security.CustomUserDetails;
import com.wagglex2.waggle.domain.common.dto.response.RecruitmentWithAppsResponseDto;
import com.wagglex2.waggle.domain.study.dto.request.StudyCreationRequestDto;
import com.wagglex2.waggle.domain.study.dto.request.StudyUpdateRequestDto;
import com.wagglex2.waggle.domain.study.dto.response.StudyDetailResponseDto;
import com.wagglex2.waggle.domain.study.service.StudyService;
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
@RequestMapping("/api/v1/studies")
@RequiredArgsConstructor
public class StudyController {
    private final StudyService studyService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<APIResponse<Long>> createStudy(
            @RequestBody @Valid StudyCreationRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long studyId = studyService.createStudy(requestDto, userDetails.getUserId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(APIResponse.ok("스터디 공고를 성공적으로 등록하였습니다.", studyId));
    }

    @GetMapping("/{studyId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<APIResponse<StudyDetailResponseDto>> getStudy(
            @PathVariable Long studyId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        StudyDetailResponseDto responseDto = studyService.getStudy(userDetails.getUserId(), studyId);

        return ResponseEntity.ok(
                APIResponse.ok("스터디 공고를 성공적으로 조회하였습니다.", responseDto)
        );
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<APIResponse<Page<RecruitmentWithAppsResponseDto>>> getMyStudies(
            @PageableDefault(size = 5) Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        PageRequest pageRequest = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<RecruitmentWithAppsResponseDto> studiesWithApps = studyService.getAllByUserId(userDetails.getUserId(), pageRequest);

        return ResponseEntity.ok(
                APIResponse.ok("내 스터디 공고 목록을 성공적으로 조회하였습니다.", studiesWithApps)
        );
    }

    @PutMapping("/{studyId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<APIResponse<Void>> updateStudy(
            @PathVariable Long studyId,
            @RequestBody @Valid StudyUpdateRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        studyService.updateStudy(userDetails.getUserId(), studyId, requestDto);

        return ResponseEntity.ok(
                APIResponse.ok("스터디 공고를 성공적으로 수정하였습니다.")
        );
    }

    @DeleteMapping("/{studyId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<APIResponse<Void>> deleteStudy(
            @PathVariable Long studyId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        studyService.deleteStudy(userDetails.getUserId(), studyId);
        return ResponseEntity.ok(
                APIResponse.ok("스터디 공고를 성공적으로 삭제하였습니다.")
        );
    }
}
