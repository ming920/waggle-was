package com.wagglex2.waggle.domain.review.controller.docs;

import com.wagglex2.waggle.common.response.APIResponse;
import com.wagglex2.waggle.common.security.CustomUserDetails;
import com.wagglex2.waggle.domain.review.dto.request.ReviewCreationRequestDto;
import com.wagglex2.waggle.domain.review.dto.request.ReviewUpdateRequestDto;
import com.wagglex2.waggle.domain.review.dto.response.ReviewResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Review(리뷰)", description = "리뷰 관련 API")
public interface ReviewControllerDocs {

    @Operation(
            summary = "리뷰 작성",
            security = @SecurityRequirement(name = "Bearer Token"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "리뷰 작성 내용",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReviewCreationRequestDto.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "teamId": 52,
                                                        "revieweeId" : 12,
                                                        "content" : "디자인 너무 잘하시는 거 같아요!"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "리뷰 작성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "SUCCESS",
                                                        "message": "리뷰 작성에 성공했습니다.",
                                                        "data": 1
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "대상 유저 ID/후기 내용 누락, 후기 내용 크기 초과, 자기 자신 리뷰 불가",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "대상 유저 ID 누락, 후기 내용 누락",
                                            value = """
                                                    {
                                                        "code": "VALIDATION_FAILED",
                                                        "message": "요청 값이 유효하지 않습니다.",
                                                        "data": [
                                                            {
                                                                "field": "revieweeId",
                                                                "message": "대상 유저 ID가 누락되었습니다."
                                                            },
                                                            {
                                                                "field": "content",
                                                                "message": "후기 내용이 누락되었습니다."
                                                            }
                                                        ]
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "자기 자신 리뷰 불가",
                                            value = """
                                                    {
                                                        "code": "SELF_REVIEW_NOT_ALLOWED",
                                                        "message": "자기 자신에 대한 리뷰는 작성할 수 없습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 필요",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "UNAUTHORIZED",
                                                        "message": "인증이 필요합니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "리뷰 작성자/대상자가 팀에 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "REVIEWER_NOT_IN_TEAM",
                                                        "message": "리뷰 작성자는 해당 팀의 멤버여야 합니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "REVIEWEE_NOT_IN_TEAM",
                                                        "message": "리뷰 대상자는 해당 팀의 멤버여야 합니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "reviewer, reviewee 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "USER_NOT_FOUND",
                                                        "message": "사용자를 찾을 수 없습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 발생",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "서버 오류 발생",
                                            value = """
                                                    {
                                                        "code": "INTERNAL_ERROR",
                                                        "message": "서버 오류가 발생했습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    ResponseEntity<APIResponse<Long>> createReview(
            @Valid @RequestBody ReviewCreationRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );


    @Operation(
            summary = "로그인한 사용자가 작성한 리뷰 목록 조회",
            description = "로그인된 사용자가 지금까지 작성한 리뷰들을 페이징 형태로 조회한다.",
            parameters = {
                    @Parameter(
                            name = "size",
                            description = "가져올 리뷰 개수 (기본값: 5)",
                            in = ParameterIn.QUERY,
                            example = "5"
                    ),
                    @Parameter(
                            name = "sort",
                            description = "정렬 기준 필드(기본값: createdAt), 정렬 방향(ASC 또는 DESC, 기본값: DESC)",
                            in = ParameterIn.QUERY,
                            example = "createdAt,DESC"
                    ),
                    @Parameter(
                            name = "page",
                            description = "가져올 페이지",
                            in = ParameterIn.QUERY,
                            example = "0"
                    )
            },
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "작성한 리뷰 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReviewResponseDto.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                         "code": "SUCCESS",
                                                         "message": "내가 작성한 리뷰 조회에 성공했습니다.",
                                                         "data": {
                                                             "content": [
                                                                 {
                                                                     "reviewId": 53,
                                                                     "teamId": 52,
                                                                     "revieweeId": 11,
                                                                     "content": "디자인 너무 잘하시는 거 같아요!"
                                                                 },
                                                                 {
                                                                     "reviewId": 46,
                                                                     "teamId": 1,
                                                                     "revieweeId": 10,
                                                                     "content": "ㅎㅇㅎㅇㅎㅇ"
                                                                 },
                                                                 {
                                                                     "reviewId": 45,
                                                                     "teamId": 2,
                                                                     "revieweeId": 10,
                                                                     "content": "안녕하세요 리뷰 테스트 수정"
                                                                 },
                                                                 {
                                                                     "reviewId": 39,
                                                                     "teamId": 3,
                                                                     "revieweeId": 10,
                                                                     "content": "리뷰 테스트 테스트"
                                                                 },
                                                                 {
                                                                     "reviewId": 28,
                                                                     "teamId": 4,
                                                                     "revieweeId": 10,
                                                                     "content": "리뷰 테스트 22"
                                                                 }
                                                             ],
                                                             "page": {
                                                                 "size": 5,
                                                                 "number": 0,
                                                                 "totalElements": 11,
                                                                 "totalPages": 3
                                                             }
                                                         }
                                                     }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 정렬 기준, 페이지 크기 초과",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "잘못된 정렬 기준",
                                            description = "http://3.35.173.28:8080/api/v1/reviews/me/written?sort=create",
                                            value = """
                                                    {
                                                        "code": "INVALID_SORT_PROPERTY",
                                                        "message": "create는 정렬할 수 없는 필드입니다. 허용된 필드: [createdAt]"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "페이지 번호가 최댓값 이상인 경우",
                                            description = "http://3.35.173.28:8080/api/v1/reviews/me/written?page=999999",
                                            value = """
                                                    {
                                                        "code": "PAGE_INDEX_OUT_OF_RANGE",
                                                        "message": "페이지 번호는 최대 10000까지 가능합니다. (요청: 999999)"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 필요",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "UNAUTHORIZED",
                                                        "message": "인증이 필요합니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 발생",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "서버 오류 발생",
                                            value = """
                                                    {
                                                        "code": "INTERNAL_ERROR",
                                                        "message": "서버 오류가 발생했습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    ResponseEntity<APIResponse<Page<ReviewResponseDto>>> getMyWrittenReviews(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(
                    size = 5,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    );


    @Operation(
            summary = "로그인한 사용자가 받은 리뷰 목록 조회",
            description = "로그인된 사용자가 지금까지 받은 리뷰들을 페이징 형태로 조회한다.",
            parameters = {
                    @Parameter(
                            name = "size",
                            description = "가져올 리뷰 개수 (기본값: 5)",
                            in = ParameterIn.QUERY,
                            example = "5"
                    ),
                    @Parameter(
                            name = "sort",
                            description = "정렬 기준 필드(기본값: createdAt), 정렬 방향(ASC 또는 DESC, 기본값: DESC)",
                            in = ParameterIn.QUERY,
                            example = "createdAt,DESC"
                    ),
                    @Parameter(
                            name = "page",
                            description = "가져올 페이지",
                            in = ParameterIn.QUERY,
                            example = "0"
                    )
            },
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "받은 리뷰 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReviewResponseDto.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                         "code": "SUCCESS",
                                                         "message": "내가 받은 리뷰 조회에 성공했습니다.",
                                                         "data": {
                                                             "content": [
                                                                 {
                                                                     "reviewId": 51,
                                                                     "teamId": 5,
                                                                     "revieweeId": 3,
                                                                     "content": "덕분에 버스탔습니다!"
                                                                 },
                                                                 {
                                                                     "reviewId": 50,
                                                                     "teamId": 4,
                                                                     "revieweeId": 3,
                                                                     "content": "너무 멋있어요!"
                                                                 },
                                                                 {
                                                                     "reviewId": 18,
                                                                     "teamId": 3,
                                                                     "revieweeId": 3,
                                                                     "content": "테스팅에 재능 있으신 것 같아요. 굿굿"
                                                                 },
                                                                 {
                                                                     "reviewId": 15,
                                                                     "teamId": 2,
                                                                     "revieweeId": 3,
                                                                     "content": "이 분 볼때마다 새우깡 먹고 싶어요"
                                                                 },
                                                                 {
                                                                     "reviewId": 14,
                                                                     "teamId": 1,
                                                                     "revieweeId": 3,
                                                                     "content": "이 분 볼때마다 새우깡 먹고 싶어요"
                                                                 }
                                                             ],
                                                             "page": {
                                                                 "size": 5,
                                                                 "number": 0,
                                                                 "totalElements": 6,
                                                                 "totalPages": 2
                                                             }
                                                         }
                                                     }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 정렬 기준, 페이지 크기 초과",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "잘못된 정렬 기준",
                                            description = "http://3.35.173.28:8080/api/v1/reviews/me/received?sort=create",
                                            value = """
                                                    {
                                                        "code": "INVALID_SORT_PROPERTY",
                                                        "message": "create는 정렬할 수 없는 필드입니다. 허용된 필드: [createdAt]"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "페이지 번호가 최댓값 이상인 경우",
                                            description = "http://3.35.173.28:8080/api/v1/reviews/me/received?page=999999",
                                            value = """
                                                    {
                                                        "code": "PAGE_INDEX_OUT_OF_RANGE",
                                                        "message": "페이지 번호는 최대 10000까지 가능합니다. (요청: 999999)"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 필요",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "UNAUTHORIZED",
                                                        "message": "인증이 필요합니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 발생",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "서버 오류 발생",
                                            value = """
                                                    {
                                                        "code": "INTERNAL_ERROR",
                                                        "message": "서버 오류가 발생했습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    ResponseEntity<APIResponse<Page<ReviewResponseDto>>> getMyReceivedReviews(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(
                    size = 5,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    );


    @Operation(
            summary = "리뷰 수정",
            description = "로그인한 사용자가 작성한 리뷰 내용을 수정한다.",
            parameters = {
                    @Parameter(
                            name = "reviewId",
                            description = "수정할 리뷰 ID",
                            in = ParameterIn.PATH,
                            example = "1"
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "리뷰 수정 내용",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReviewUpdateRequestDto.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "content": "후기 내용 수정 test입니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "리뷰 수정 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "SUCCESS",
                                                        "message": "리뷰 수정에 성공했습니다.",
                                                        "data": 1
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "후기 내용 누락/크기 초과",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "후기 내용 누락",
                                            value = """
                                                    {
                                                        "code": "VALIDATION_FAILED",
                                                        "message": "요청 값이 유효하지 않습니다.",
                                                        "data": [
                                                            {
                                                                "field": "content",
                                                                "message": "후기 내용이 누락되었습니다."
                                                            }
                                                        ]
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 필요",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "UNAUTHORIZED",
                                                        "message": "인증이 필요합니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "본인이 작성한 리뷰만 수정 가능, 비활성화된 리뷰 수정 불가",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "본인이 작성한 리뷰만 수정 가능",
                                            value = """
                                                        {
                                                            "code": "CANNOT_UPDATE_ANOTHER_USER_REVIEW",
                                                            "message": "본인이 작성한 리뷰만 수정할 수 있습니다."
                                                        }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "비활성화된 리뷰 수정 불가",
                                            value = """
                                                    {
                                                        "code": "CANNOT_UPDATE_NOT_ACTIVE_REVIEW",
                                                        "message": "비활성화된 리뷰는 수정할 수 없습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "해당 Review 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "REVIEW_NOT_FOUND",
                                                        "message": "리뷰를 찾을 수 없습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 발생",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "서버 오류 발생",
                                            value = """
                                                    {
                                                        "code": "INTERNAL_ERROR",
                                                        "message": "서버 오류가 발생했습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    ResponseEntity<APIResponse<Long>> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewUpdateRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );


    @Operation(
            summary = "리뷰 삭제",
            description = "로그인한 사용자가 작성한 리뷰 내용을 삭제한다.",
            parameters = {
                    @Parameter(
                            name = "reviewId",
                            description = "삭제할 리뷰 ID",
                            in = ParameterIn.PATH,
                            example = "1"
                    )
            },
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "리뷰 삭제 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "SUCCESS",
                                                        "message": "리뷰 삭제에 성공했습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "후기 내용 누락/크기 초과",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "후기 내용 누락",
                                            value = """
                                                    {
                                                        "code": "VALIDATION_FAILED",
                                                        "message": "요청 값이 유효하지 않습니다.",
                                                        "data": [
                                                            {
                                                                "field": "content",
                                                                "message": "후기 내용이 누락되었습니다."
                                                            }
                                                        ]
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "자기 자신 리뷰 불가",
                                            value = """
                                                    {
                                                        "code": "SELF_REVIEW_NOT_ALLOWED",
                                                        "message": "자기 자신에 대한 리뷰는 작성할 수 없습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 필요",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "UNAUTHORIZED",
                                                        "message": "인증이 필요합니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "본인이 작성한 리뷰만 삭제 가능, 비활성화된 리뷰 삭제 불가",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "본인이 작성한 리뷰만 수정 가능",
                                            value = """
                                                        {
                                                            "code": "CANNOT_DELETE_ANOTHER_USER_REVIEW",
                                                            "message": "본인이 작성한 리뷰만 삭제할 수 있습니다."
                                                        }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "비활성화된 리뷰 삭제 불가",
                                            value = """
                                                    {
                                                        "code": "CANNOT_DELETE_NOT_ACTIVE_REVIEW",
                                                        "message": "비활성화된 리뷰는 삭제할 수 없습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "해당 Review 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "REVIEW_NOT_FOUND",
                                                        "message": "리뷰를 찾을 수 없습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 발생",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "서버 오류 발생",
                                            value = """
                                                    {
                                                        "code": "INTERNAL_ERROR",
                                                        "message": "서버 오류가 발생했습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    ResponseEntity<APIResponse<Void>> deleteReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );
}
