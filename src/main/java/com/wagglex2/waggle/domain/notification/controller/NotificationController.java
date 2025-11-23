package com.wagglex2.waggle.domain.notification.controller;

import com.wagglex2.waggle.common.response.APIResponse;
import com.wagglex2.waggle.common.security.CustomUserDetails;
import com.wagglex2.waggle.domain.common.type.RecruitmentCategory;
import com.wagglex2.waggle.domain.notification.controller.docs.NotificationControllerDocs;
import com.wagglex2.waggle.domain.notification.dto.response.NotificationResponseDto;
import com.wagglex2.waggle.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController implements NotificationControllerDocs {

    private final NotificationService notificationService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<APIResponse<Page<NotificationResponseDto>>> getMyNotificationsByCategory(
            @RequestParam(value = "category", required = false) RecruitmentCategory category,
            @PageableDefault(size = 5) Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        PageRequest pageRequest = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<NotificationResponseDto> notifications = notificationService.getAllByUserIdAndCategory(
                userDetails.getUserId(),
                category,
                pageRequest
        );

        String message = (category != null ? category.getDesc() + " 알림을" : "전체 알림을") + " 성공적으로 조회하였습니다.";

        return ResponseEntity.ok(APIResponse.ok(message, notifications));
    }

    @PatchMapping("{notificationId}/read")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<APIResponse<Void>> markAsRead(
            @PathVariable("notificationId") Long notificationId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        notificationService.markAsRead(userDetails.getUserId(), notificationId);

        return ResponseEntity.ok(APIResponse.ok("알림을 읽음 처리하였습니다."));
    }

    @DeleteMapping("{notificationId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<APIResponse<Void>> deleteById(
            @PathVariable("notificationId") Long notificationId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        notificationService.deleteById(userDetails.getUserId(), notificationId);

        return ResponseEntity.ok(APIResponse.ok("알림을 성공적으로 삭제하였습니다."));
    }

    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<APIResponse<Void>> deleteAll(
            @RequestParam(value = "category", required = false) RecruitmentCategory category,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        notificationService.deleteAll(userDetails.getUserId(), category);

        String message = (category != null ? category.getDesc() + " 알림을" : "전체 알림을") + " 성공적으로 삭제하였습니다.";

        return ResponseEntity.ok(APIResponse.ok(message));
    }
}
