package com.wagglex2.waggle.domain.bookmark.controller;

import com.wagglex2.waggle.common.response.APIResponse;
import com.wagglex2.waggle.common.security.CustomUserDetails;
import com.wagglex2.waggle.domain.bookmark.controller.docs.BookmarkControllerDocs;
import com.wagglex2.waggle.domain.bookmark.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bookmarks")
@RequiredArgsConstructor
public class BookmarkController implements BookmarkControllerDocs {

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
