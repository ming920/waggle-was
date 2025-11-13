package com.wagglex2.waggle.domain.bookmark.controller;

import com.wagglex2.waggle.common.response.APIResponse;
import com.wagglex2.waggle.common.security.CustomUserDetails;
import com.wagglex2.waggle.domain.bookmark.service.BookmarkService;
import com.wagglex2.waggle.domain.project.dto.response.ProjectSummaryResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @PostMapping("/recruitments/{recruitmentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<APIResponse<Long>> createBookmark(
            @PathVariable("recruitmentId") Long recruitmentId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long bookmarkId = bookmarkService.createBookmark(userDetails.getUserId(), recruitmentId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(APIResponse.ok("공고를 찜 목록에 성공적으로 추가하였습니다.", bookmarkId));
    }

    @GetMapping("/projects")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<APIResponse<Page<ProjectSummaryResponseDto>>> getBookmarkedProjectSummariesByUserId(
            @PageableDefault(size = 9) Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Page<ProjectSummaryResponseDto> bookmarkedProjects =
                bookmarkService.getBookmarkedProjectsByUserId(userDetails.getUserId(), pageable);

        return ResponseEntity.ok(
                APIResponse.ok("프로젝트 공고 찜 목록을 성공적으로 조회하였습니다.", bookmarkedProjects)
        );
    }

    @DeleteMapping("/{bookmarkId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<APIResponse<Void>> deleteBookmark(
            @PathVariable("bookmarkId") Long bookmarkId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        bookmarkService.deleteBookmark(userDetails.getUserId(), bookmarkId);

        return ResponseEntity.ok(
                APIResponse.ok("공고를 찜 목록에서 성공적으로 삭제하였습니다.")
        );
    }
}
