package com.wagglex2.waggle.domain.study.controller;


import com.wagglex2.waggle.common.response.APIResponse;
import com.wagglex2.waggle.common.security.CustomUserDetails;
import com.wagglex2.waggle.domain.common.dto.response.RecruitmentWithAppsResponseDto;
import com.wagglex2.waggle.domain.common.type.RecruitmentStatus;
import com.wagglex2.waggle.domain.common.type.Skill;
import com.wagglex2.waggle.domain.common.util.KomoranUtil;
import com.wagglex2.waggle.domain.study.controller.docs.StudyControllerDocs;
import com.wagglex2.waggle.domain.study.dto.request.StudyCreationRequestDto;
import com.wagglex2.waggle.domain.study.dto.request.StudySearchCondition;
import com.wagglex2.waggle.domain.study.dto.request.StudyUpdateRequestDto;
import com.wagglex2.waggle.domain.study.dto.response.StudyDetailResponseDto;
import com.wagglex2.waggle.domain.study.dto.response.StudySummaryResponseDto;
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

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/studies")
@RequiredArgsConstructor
public class StudyController implements StudyControllerDocs {
    private final StudyService studyService;
    private final KomoranUtil komoranUtil;

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

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<APIResponse<Page<StudySummaryResponseDto>>> getStudySummaries(
            @RequestParam(value = "q", required = false) String keywords,
            @RequestParam(value = "skills", required = false) List<Skill> skills,
            @RequestParam(value = "status", required = false) RecruitmentStatus status,
            @PageableDefault(size = 9) Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Set<String> nouns = (keywords != null) ? komoranUtil.getNouns(keywords) : Set.of();
        Set<Skill> skillSet = (skills != null) ? Set.copyOf(skills) : Set.of();

        StudySearchCondition condition = new StudySearchCondition(
                nouns,
                skillSet,
                status
        );

        Page<StudySummaryResponseDto> studySummaries =
                studyService.getStudySummaries(userDetails.getUserId(), condition, pageable);

        return ResponseEntity.ok(
                APIResponse.ok("스터디 공고 목록을 성공적으로 조회하였습니다.", studySummaries)
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

    @GetMapping("/bookmarks")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<APIResponse<Page<StudySummaryResponseDto>>> getMyBookmarks(
            @RequestParam(value = "status", required = false) RecruitmentStatus status,
            @PageableDefault(size = 9) Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Page<StudySummaryResponseDto> bookmarkedStudies =
                studyService.getBookmarkedStudiesByUserId(userDetails.getUserId(), status, pageable);

        return ResponseEntity.ok(
                APIResponse.ok("스터디 공고 찜 목록을 성공적으로 조회하였습니다.", bookmarkedStudies)
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
