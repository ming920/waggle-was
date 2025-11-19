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

@Tag(name = "Review", description = "리뷰 관련 API")
public interface ReviewControllerDocs {

    @Operation(
            summary = "리뷰 작성",
            security = @SecurityRequirement(name = "Bearer Token")
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
                            description = "정렬 기준 필드(기본값: createdAt)",
                            in = ParameterIn.QUERY,
                            example = "createdAt"
                    ),
                    @Parameter(
                            name = "direction",
                            description = "정렬 방향(ASC 또는 DESC, 기본값: DESC)",
                            in = ParameterIn.QUERY,
                            example = "DESC"
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
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "SUCCESS",
                                                        "message": "내가 작성한 리뷰 조회에 성공했습니다.",
                                                        "data": {
                                                            "content": [
                                                                {
                                                                    "content": "리뷰 test13"
                                                                },
                                                                {
                                                                    "content": "리뷰 test12"
                                                                },
                                                                {
                                                                    "content": "리뷰 test11"
                                                                },
                                                                {
                                                                    "content": "리뷰 test9"                                                                    },
                                                                {
                                                                    "content": "리뷰 test8"
                                                                }
                                                            ],
                                                            "page": {
                                                                "size": 5,
                                                                "number": 0,
                                                                "totalElements": 13,
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
                            description = "정렬 기준 필드(기본값: createdAt)",
                            in = ParameterIn.QUERY,
                            example = "createdAt"
                    ),
                    @Parameter(
                            name = "direction",
                            description = "정렬 방향(ASC 또는 DESC, 기본값: DESC)",
                            in = ParameterIn.QUERY,
                            example = "DESC"
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
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "SUCCESS",
                                                        "message": "내가 받은 리뷰 조회에 성공했습니다.",
                                                        "data": {
                                                            "content": [
                                                                {
                                                                    "content": "리뷰 test입니다.(5)"
                                                                },
                                                                {
                                                                    "content": "리뷰 test입니다.(4)"
                                                                },
                                                                {
                                                                    "content": "리뷰 test입니다.(3)"
                                                                },
                                                                {
                                                                   "content": "리뷰 test입니다.(2)"
                                                                },
                                                                {
                                                                    "content": "리뷰 test입니다.(1)"
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
