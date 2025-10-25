package com.wagglex2.waggle.domain.review.controller;

import com.wagglex2.waggle.common.response.ApiResponse;
import com.wagglex2.waggle.common.security.CustomUserDetails;
import com.wagglex2.waggle.domain.common.dto.response.PageResponse;
import com.wagglex2.waggle.domain.review.dto.request.ReviewCreationRequestDto;
import com.wagglex2.waggle.domain.review.dto.request.ReviewUpdateRequestDto;
import com.wagglex2.waggle.domain.review.dto.response.ReviewResponseDto;
import com.wagglex2.waggle.domain.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 리뷰를 작성한다.
     *
     * @param dto         후기 작성 요청 DTO
     * @param userDetails 인증된 사용자 정보
     * @return 생성된 리뷰 ID를 포함한 ApiResponse
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Long>> createReview(
            @Valid @RequestBody ReviewCreationRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long reviewId = reviewService.createReview(userDetails.getUserId(), dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("리뷰 작성에 성공했습니다.", reviewId));
    }

    /**
     * 로그인한 사용자가 <b>작성한 리뷰 목록</b>을 페이지네이션 방식으로 조회한다.
     *
     * <p><b>요청 파라미터 예시:</b></p>
     * <ul>
     *   <li>{@code GET /me/written?page=0&size=5&sort=createdAt,desc}</li>
     *   <li>페이지 번호는 0부터 시작 (Spring Data JPA의 기본 규칙)</li>
     * </ul>
     *
     * @param userDetails 현재 인증된 사용자 정보
     * @param pageable    페이지 정보 (기본값: size=5, sort=createdAt, direction=DESC)
     * @return 내가 작성한 리뷰 목록을 포함한 {@link ApiResponse} (200 OK)
     */
    @GetMapping("/me/written")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PageResponse<ReviewResponseDto>>> getMyWrittenReviews(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(
                    size = 5,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        PageResponse<ReviewResponseDto> data = reviewService.getReviewsByReviewerId(
                userDetails.getUserId(),
                pageable
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("내가 작성한 리뷰 조회에 성공했습니다.", data));
    }

    /**
     * 로그인한 사용자가 <b>받은 리뷰 목록</b>을 페이지네이션 방식으로 조회한다.
     *
     * @param userDetails 현재 인증된 사용자 정보
     * @param pageable    페이지 정보 (기본값: size=5, sort=createdAt, direction=DESC)
     * @return 받은 리뷰 목록을 포함한 {@link ApiResponse} (200 OK)
     */
    @GetMapping("/me/received")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PageResponse<ReviewResponseDto>>> getMyReceivedReviews(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(
                    size = 5,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        PageResponse<ReviewResponseDto> data = reviewService.getReviewsByRevieweeId(
                userDetails.getUserId(),
                pageable
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("내가 받은 리뷰 조회에 성공했습니다.", data));
    }

    /**
     * 리뷰 수정 API
     *
     * <p>사용자가 본인이 작성한 리뷰 내용을 수정한다.
     * PATCH 메서드를 사용하여 부분 업데이트를 수행한다.
     *
     * @param reviewId 수정할 리뷰 ID (PathVariable)
     * @param dto 리뷰 수정 요청 DTO (내용 검증 포함)
     * @param userDetails 현재 인증된 사용자 정보 (Spring Security Context에서 주입)
     * @return 수정된 리뷰의 ID를 포함한 응답 (ApiResponse<Long>)
     */
    @PatchMapping("/me/written/{reviewId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Long>> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewUpdateRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        Long data = reviewService.updateReview(
                userDetails.getUserId(),
                reviewId,
                dto
                );

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("리뷰 수정에 성공했습니다.", data));
    }
}
