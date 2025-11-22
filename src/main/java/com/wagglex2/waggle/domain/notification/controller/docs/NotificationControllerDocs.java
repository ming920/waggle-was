package com.wagglex2.waggle.domain.notification.controller.docs;

import com.wagglex2.waggle.common.response.APIResponse;
import com.wagglex2.waggle.common.security.CustomUserDetails;
import com.wagglex2.waggle.domain.common.type.RecruitmentCategory;
import com.wagglex2.waggle.domain.notification.dto.response.NotificationResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Notification(알림)", description = "알림 관련 API")
public interface NotificationControllerDocs {

    @Operation(
            summary = "알림 조회",
            description = "나에게 온 전체/카테고리별 알림을 최신순으로 조회한다.",
            security = @SecurityRequirement(name = "Bearer Token"),
            parameters = {
                    @Parameter(
                            name = "category",
                            description = """
                                    조회하려는 알림의 공고 카테고리<br>
                                    미지정 시, 전체 알림 조회
                                    """,
                            in = ParameterIn.QUERY
                    ),
                    @Parameter(
                            name = "page",
                            description = """
                                    조회하려는 알림 페이지<br>
                                    기본값: 0 (0부터 시작)
                                    """,
                            in = ParameterIn.QUERY
                    ),
                    @Parameter(
                            name = "size",
                            description = """
                                    조회하려는 프로젝트 공고 개수<br>
                                    기본값: 5
                                    """,
                            in = ParameterIn.QUERY
                    )
            }
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "알림 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = NotificationResponseDto.class)
                            ),
                            examples = {
                                    @ExampleObject(
                                            name = "전체 알림",
                                            description = "`/notifications`",
                                            value = """
                                                    {
                                                        "code": "SUCCESS",
                                                        "message": "전체 알림을 성공적으로 조회하였습니다.",
                                                        "data": {
                                                            "content": [
                                                                {
                                                                    "notificationId": 47,
                                                                    "applicationId": 30,
                                                                    "category": {
                                                                        "desc": "스터디",
                                                                        "name": "STUDY"
                                                                    },
                                                                    "senderNickname": "박데통",
                                                                    "type": "APPLICATION_SUBMITTED",
                                                                    "createdAt": "2025.11.21",
                                                                    "isRead": false
                                                                },
                                                                {
                                                                    "notificationId": 46,
                                                                    "applicationId": 29,
                                                                    "category": {
                                                                        "desc": "스터디",
                                                                        "name": "STUDY"
                                                                    },
                                                                    "senderNickname": "매운새우깡",
                                                                    "type": "APPLICATION_ACCEPTED",
                                                                    "createdAt": "2025.11.21",
                                                                    "isRead": false
                                                                },
                                                                {
                                                                    "notificationId": 45,
                                                                    "applicationId": 28,
                                                                    "category": {
                                                                        "desc": "과제",
                                                                        "name": "ASSIGNMENT"
                                                                    },
                                                                    "senderNickname": "매운새우깡",
                                                                    "type": "APPLICATION_SUBMITTED",
                                                                    "createdAt": "2025.11.21",
                                                                    "isRead": false
                                                                },
                                                                {
                                                                    "notificationId": 44,
                                                                    "applicationId": 27,
                                                                    "category": {
                                                                        "desc": "프로젝트",
                                                                        "name": "PROJECT"
                                                                    },
                                                                    "senderNickname": "박데통",
                                                                    "type": "APPLICATION_REJECTED",
                                                                    "createdAt": "2025.11.21",
                                                                    "isRead": false
                                                                }
                                                            ],
                                                            "page": {
                                                                "size": 5,
                                                                "number": 0,
                                                                "totalElements": 4,
                                                                "totalPages": 1
                                                            }
                                                        }
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "프로젝트 알림",
                                            description = "`/notifications?category=project`",
                                            value = """
                                                    {
                                                        "code": "SUCCESS",
                                                        "message": "프로젝트 알림을 성공적으로 조회하였습니다.",
                                                        "data": {
                                                            "content": [
                                                                {
                                                                    "notificationId": 44,
                                                                    "applicationId": 27,
                                                                    "category": {
                                                                        "desc": "프로젝트",
                                                                        "name": "PROJECT"
                                                                    },
                                                                    "senderNickname": "박데통",
                                                                    "type": "APPLICATION_REJECTED",
                                                                    "createdAt": "2025.11.21",
                                                                    "isRead": false
                                                                }
                                                            ],
                                                            "page": {
                                                                "size": 5,
                                                                "number": 0,
                                                                "totalElements": 1,
                                                                "totalPages": 1
                                                            }
                                                        }
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "과제 알림",
                                            description = "`/notifications?category=assignment`",
                                            value = """
                                                    {
                                                        "code": "SUCCESS",
                                                        "message": "과제 알림을 성공적으로 조회하였습니다.",
                                                        "data": {
                                                            "content": [
                                                                {
                                                                    "notificationId": 45,
                                                                    "applicationId": 28,
                                                                    "category": {
                                                                        "desc": "과제",
                                                                        "name": "ASSIGNMENT"
                                                                    },
                                                                    "senderNickname": "매운새우깡",
                                                                    "type": "APPLICATION_SUBMITTED",
                                                                    "createdAt": "2025.11.21",
                                                                    "isRead": false
                                                                }
                                                            ],
                                                            "page": {
                                                                "size": 5,
                                                                "number": 0,
                                                                "totalElements": 1,
                                                                "totalPages": 1
                                                            }
                                                        }
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "스터디 알림",
                                            description = "`/notifications?category=study`",
                                            value = """
                                                    {
                                                        "code": "SUCCESS",
                                                        "message": "스터디 알림을 성공적으로 조회하였습니다.",
                                                        "data": {
                                                            "content": [
                                                                {
                                                                    "notificationId": 47,
                                                                    "applicationId": 30,
                                                                    "category": {
                                                                        "desc": "스터디",
                                                                        "name": "STUDY"
                                                                    },
                                                                    "senderNickname": "박데통",
                                                                    "type": "APPLICATION_SUBMITTED",
                                                                    "createdAt": "2025.11.21",
                                                                    "isRead": false
                                                                },
                                                                {
                                                                    "notificationId": 46,
                                                                    "applicationId": 29,
                                                                    "category": {
                                                                        "desc": "스터디",
                                                                        "name": "STUDY"
                                                                    },
                                                                    "senderNickname": "매운새우깡",
                                                                    "type": "APPLICATION_ACCEPTED",
                                                                    "createdAt": "2025.11.21",
                                                                    "isRead": false
                                                                }
                                                            ],
                                                            "page": {
                                                                "size": 5,
                                                                "number": 0,
                                                                "totalElements": 2,
                                                                "totalPages": 1
                                                            }
                                                        }
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "알림 없음",
                                            description = "`/notifications`",
                                            value = """
                                                    {
                                                        "code": "SUCCESS",
                                                        "message": "전체 알림을 성공적으로 조회하였습니다.",
                                                        "data": {
                                                            "content": [],
                                                            "page": {
                                                                "size": 5,
                                                                "number": 0,
                                                                "totalElements": 0,
                                                                "totalPages": 0
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
                    description = "요청 값이 유효하지 않은 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = @ExampleObject(
                                    name = "카테고리 값이 잘못된 경우",
                                    description = "`/notifications?category=apple`",
                                    value = """
                                            {
                                                "code": "INVALID_ENUM_VALUE",
                                                "message": "쿼리 파라미터 값이 유효하지 않습니다. 허용 가능한 값 목록을 확인해주세요.",
                                                "data": "쿼리 파라미터 'category'의 값 'apple'이(가) 유효하지 않습니다."
                                            }
                                            """
                            )
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
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    ResponseEntity<APIResponse<Page<NotificationResponseDto>>> getMyNotificationsByCategory(
            @RequestParam(value = "category", required = false) RecruitmentCategory category,
            @PageableDefault(size = 5) Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "알림 개별 삭제",
            description = "알림 1개를 삭제한다.",
            security = @SecurityRequirement(name = "Bearer Token"),
            parameters = {
                    @Parameter(
                            name = "notificationId",
                            description = "삭제하려는 알림 ID",
                            required = true,
                            in = ParameterIn.PATH,
                            example = "13"
                    )
            }
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "알림 개별 삭제 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "SUCCESS",
                                                        "message": "알림을 성공적으로 삭제하였습니다."
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
                    description = "알림을 삭제할 권한이 없는 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "CANNOT_DELETE_ANOTHER_USER_NOTIFICATION",
                                                        "message": "다른 사용자의 알림은 삭제할 수 없습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "삭제하려는 알림이 존재하지 않는 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "NOTIFICATION_NOT_FOUND",
                                                        "message": "알림을 찾을 수 없습니다."
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
    @DeleteMapping("{notificationId}")
    @PreAuthorize("isAuthenticated()")
    ResponseEntity<APIResponse<Void>> deleteById(
            @PathVariable("notificationId") Long notificationId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "알림 전체/카테고리별 삭제",
            description = "전체 혹은 카테고리별 모든 알림을 삭제한다.",
            security = @SecurityRequirement(name = "Bearer Token"),
            parameters = {
                    @Parameter(
                            name = "category",
                            description = """
                                    삭제하려는 알림 카테고리<br>
                                    미지정 시, 전체 알림 삭제
                                    """,
                            in = ParameterIn.QUERY
                    )
            }
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "알림 전체/카테고리별 삭제 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "전체 알림",
                                            description = "`/notifications`",
                                            value = """
                                                    {
                                                        "code": "SUCCESS",
                                                        "message": "전체 알림을 성공적으로 삭제하였습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "프로젝트 알림",
                                            description = "`/notifications?category=project`",
                                            value = """
                                                    {
                                                        "code": "SUCCESS",
                                                        "message": "프로젝트 알림을 성공적으로 삭제하였습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "과제 알림",
                                            description = "`/notifications?category=assignment`",
                                            value = """
                                                    {
                                                        "code": "SUCCESS",
                                                        "message": "과제 알림을 성공적으로 삭제하였습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "스터디 알림",
                                            description = "`/notifications?category=study`",
                                            value = """
                                                    {
                                                        "code": "SUCCESS",
                                                        "message": "스터디 알림을 성공적으로 삭제하였습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "요청 값이 유효하지 않은 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = @ExampleObject(
                                    name = "카테고리 값이 잘못된 경우",
                                    description = "`/notifications?category=apple`",
                                    value = """
                                            {
                                                "code": "INVALID_ENUM_VALUE",
                                                "message": "쿼리 파라미터 값이 유효하지 않습니다. 허용 가능한 값 목록을 확인해주세요.",
                                                "data": "쿼리 파라미터 'category'의 값 'apple'이(가) 유효하지 않습니다."
                                            }
                                            """
                            )
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
    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    ResponseEntity<APIResponse<Void>> deleteAll(
            @RequestParam(value = "category", required = false) RecruitmentCategory category,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );
}
