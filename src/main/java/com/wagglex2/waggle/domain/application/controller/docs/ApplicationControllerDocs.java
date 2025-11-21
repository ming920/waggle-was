package com.wagglex2.waggle.domain.application.controller.docs;

import com.wagglex2.waggle.common.response.APIResponse;
import com.wagglex2.waggle.common.security.CustomUserDetails;
import com.wagglex2.waggle.domain.application.dto.request.ApplicationCommonRequestDto;
import com.wagglex2.waggle.domain.application.dto.response.ApplicationCommonResponseDto;
import com.wagglex2.waggle.domain.common.type.RecruitmentCategory;
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
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "공고 지원", description = "공고 지원 관련 API")
public interface ApplicationControllerDocs {

    @Operation(
            summary = "지원서 제출",
            description = "특정 공고에 지원한다.",
            security = @SecurityRequirement(name = "Bearer Token"),
            parameters = {
                    @Parameter(
                            name = "recruitmentId",
                            description = "지원하려는 공고 ID (카테고리 구분 없음)",
                            required = true,
                            in = ParameterIn.PATH,
                            example = "221"
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "지원서 작성 내용",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "프로젝트 공고 지원",
                                            value = """
                                                    {
                                                      "category": "PROJECT",
                                                      "meetingType": "ONLINE",
                                                      "grade": 3,
                                                      "content": "이 프로젝트에 지원하고 싶습니다.",
                                                      "position": "FRONT_END",
                                                      "skills": ["REACT", "JAVASCRIPT"]
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "과제 공고 지원",
                                            value = """
                                                    {
                                                      "category": "ASSIGNMENT",
                                                      "meetingType": "ONLINE",
                                                      "grade": 3,
                                                      "content": "저 재수강이에요."
                                                    }
                                                    """

                                    ),
                                    @ExampleObject(
                                            name = "스터디 공고 지원",
                                            value = """
                                                    {
                                                      "category": "STUDY",
                                                      "meetingType": "ONLINE",
                                                      "grade": 2,
                                                      "content": "방학 때 시간 많아요."
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
                    description = """
                                  공고 지원 성공
                                  data: 지원 ID
                                  """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "SUCCESS",
                                                        "message": "지원서를 성공적으로 제출하였습니다.",
                                                        "data": 24
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "유효하지 않은 요청 값 또는 비즈니스 검증 실패로 인해 요청이 거부된 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "요청 값이 유효하지 않은 경우",
                                            value = """
                                                    {
                                                        "code": "VALIDATION_FAILED",
                                                        "message": "요청 값이 유효하지 않습니다.",
                                                        "data": [
                                                            {
                                                                "field": "position",
                                                                "message": "지원하는 포지션 정보가 누락되었습니다."
                                                            },
                                                            {
                                                                "field": "grade",
                                                                "message": "학년은 4 이하여야 합니다."
                                                            }
                                                        ]
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "요청을 보낸 카테고리 값과 실제 공고의 카테고리 값이 다른 경우",
                                            value = """
                                                    {
                                                        "code": "MISMATCHED_RECRUITMENT_CATEGORY",
                                                        "message": "지원하려는 공고의 카테고리가 요청한 카테고리와 일치하지 않습니다."
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
                    description = "지원할 수 없는 공고에 대해 요청을 보낸 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "타 대학의 공고에 지원하려는 경우",
                                            value = """
                                                    {
                                                        "code": "FORBIDDEN_CROSS_UNIVERSITY_RECRUITMENT",
                                                        "message": "타 대학의 공고입니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "본인 공고에 지원하려는 경우",
                                            value = """
                                                    {
                                                        "code": "CANNOT_APPLY_OWN_RECRUITMENT",
                                                        "message": "본인 공고에는 지원할 수 없습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "공고가 존재하지 않는 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "RECRUITMENT_NOT_FOUND",
                                                        "message": "공고를 찾을 수 없습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "공고/포지션에 지원이 불가능한 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "공고의 모집 기간이 이미 종료된 경우",
                                            value = """
                                                    {
                                                        "code": "RECRUITMENT_CLOSED",
                                                        "message": "모집 기간이 종료된 공고입니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "이미 지원한 공고인 경우",
                                            value = """
                                                    {
                                                        "code": "ALREADY_APPLIED_RECRUITMENT",
                                                        "message": "이미 지원한 공고입니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "모집하지 않는 포지션에 지원하려는 경우",
                                            description = "프로젝트 공고에 해당",
                                            value = """
                                                    {
                                                        "code": "NOT_RECRUITING_POSITION",
                                                        "message": "해당 포지션은 모집 대상이 아닙니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "이미 모집이 완료된 포지션에 지원하려는 경우",
                                            description = "프로젝트 공고에 해당",
                                            value = """
                                                    {
                                                        "code": "POSITION_FULL",
                                                        "message": "이미 모집이 완료된 포지션입니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "이미 모집이 완료된 공고에 지원하려는 경우",
                                            description = "과제/스터디 공고에 해당",
                                            value = """
                                                    {
                                                        "code": "RECRUITMENT_FULL",
                                                        "message": "이미 모집이 완료된 공고입니다."
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
    @PostMapping("/recruitments/{recruitmentId}")
    @PreAuthorize("isAuthenticated()")
    ResponseEntity<APIResponse<Long>> submitProjectApplication(
            @PathVariable("recruitmentId") Long recruitmentId,
            @RequestBody @Valid ApplicationCommonRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "내 지원 조회",
            description = "내 지원 내역을 조회한다.",
            security = @SecurityRequirement(name = "Bearer Token"),
            parameters = {
                    @Parameter(
                            name = "category",
                            description = "조회하려는 지원 내역의 공고 카테고리",
                            required = true,
                            in = ParameterIn.QUERY
                    ),
                    @Parameter(
                            name = "page",
                            description = """
                                    조회하려는 지원 내역 페이지<br>
                                    기본값: 0 (0부터 시작)
                                    """,
                            in = ParameterIn.QUERY
                    ),
                    @Parameter(
                            name = "size",
                            description = """
                                    조회하려는 지원 내역 개수<br>
                                    기본값: 5
                                    """,
                            in = ParameterIn.QUERY
                    )
            }
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = """
                            내 지원 내역 조회 성공<br>
                            지원 일시를 기준으로 내림차순 (최신순)
                            """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "프로젝트 공고 지원 내역",
                                            description = "`/applications/me?category=project`",
                                            value = """
                                                    {
                                                        "code": "SUCCESS",
                                                        "message": "프로젝트 공고 지원 내역을 성공적으로 조회하였습니다.",
                                                        "data": {
                                                            "content": [
                                                                {
                                                                    "applicationId": 19,
                                                                    "recruitmentId": 140,
                                                                    "recruitmentTitle": "토스 주관 공모전 팀원 구합니다.",
                                                                    "recruitmentDeadline": "2025.12.20",
                                                                    "meetingType": {
                                                                        "desc": "온라인",
                                                                        "name": "ONLINE"
                                                                    },
                                                                    "grade": 3,
                                                                    "content": "이 프로젝트에 지원하고 싶습니다.",
                                                                    "status": {
                                                                        "desc": "수락됨",
                                                                        "name": "ACCEPTED"
                                                                    },
                                                                    "position": {
                                                                        "desc": "백엔드",
                                                                        "name": "BACK_END"
                                                                    },
                                                                    "skills": [
                                                                        {
                                                                            "desc": "Spring Boot",
                                                                            "name": "SPRING_BOOT"
                                                                        },
                                                                        {
                                                                            "desc": "MySQL",
                                                                            "name": "MYSQL"
                                                                        },
                                                                        {
                                                                            "desc": "Java",
                                                                            "name": "JAVA"
                                                                        }
                                                                    ]
                                                                },
                                                                {
                                                                    "applicationId": 16,
                                                                    "recruitmentId": 139,
                                                                    "recruitmentTitle": "토스 주관 공모전 팀원 구합니다.",
                                                                    "recruitmentDeadline": "2025.12.20",
                                                                    "meetingType": {
                                                                        "desc": "온라인",
                                                                        "name": "ONLINE"
                                                                    },
                                                                    "grade": 3,
                                                                    "content": "이 프로젝트에 지원하고 싶습니다.",
                                                                    "status": {
                                                                        "desc": "수락됨",
                                                                        "name": "ACCEPTED"
                                                                    },
                                                                    "position": {
                                                                        "desc": "백엔드",
                                                                        "name": "BACK_END"
                                                                    },
                                                                    "skills": [
                                                                        {
                                                                            "desc": "Spring Boot",
                                                                            "name": "SPRING_BOOT"
                                                                        },
                                                                        {
                                                                            "desc": "MySQL",
                                                                            "name": "MYSQL"
                                                                        },
                                                                        {
                                                                            "desc": "Java",
                                                                            "name": "JAVA"
                                                                        }
                                                                    ]
                                                                },
                                                                {
                                                                    "applicationId": 15,
                                                                    "recruitmentId": 138,
                                                                    "recruitmentTitle": "네이버 주관 공모전 팀원 구합니다.",
                                                                    "recruitmentDeadline": "2025.12.20",
                                                                    "meetingType": {
                                                                        "desc": "온라인",
                                                                        "name": "ONLINE"
                                                                    },
                                                                    "grade": 3,
                                                                    "content": "이 프로젝트에 지원하고 싶습니다.",
                                                                    "status": {
                                                                        "desc": "수락됨",
                                                                        "name": "ACCEPTED"
                                                                    },
                                                                    "position": {
                                                                        "desc": "백엔드",
                                                                        "name": "BACK_END"
                                                                    },
                                                                    "skills": [
                                                                        {
                                                                            "desc": "Spring Boot",
                                                                            "name": "SPRING_BOOT"
                                                                        },
                                                                        {
                                                                            "desc": "MySQL",
                                                                            "name": "MYSQL"
                                                                        },
                                                                        {
                                                                            "desc": "Java",
                                                                            "name": "JAVA"
                                                                        }
                                                                    ]
                                                                },
                                                                {
                                                                    "applicationId": 12,
                                                                    "recruitmentId": 137,
                                                                    "recruitmentTitle": "네이버 주관 공모전 팀원 구합니다.",
                                                                    "recruitmentDeadline": "2025.12.20",
                                                                    "meetingType": {
                                                                        "desc": "온라인",
                                                                        "name": "ONLINE"
                                                                    },
                                                                    "grade": 3,
                                                                    "content": "이 프로젝트에 지원하고 싶습니다.",
                                                                    "status": {
                                                                        "desc": "대기중",
                                                                        "name": "SUBMITTED"
                                                                    },
                                                                    "position": {
                                                                        "desc": "백엔드",
                                                                        "name": "BACK_END"
                                                                    },
                                                                    "skills": [
                                                                        {
                                                                            "desc": "Spring Boot",
                                                                            "name": "SPRING_BOOT"
                                                                        },
                                                                        {
                                                                            "desc": "MySQL",
                                                                            "name": "MYSQL"
                                                                        },
                                                                        {
                                                                            "desc": "Java",
                                                                            "name": "JAVA"
                                                                        }
                                                                    ]
                                                                },
                                                                {
                                                                    "applicationId": 11,
                                                                    "recruitmentId": 3,
                                                                    "recruitmentTitle": "토스 주관 공모전 팀원 구합니다. (추가 모집)",
                                                                    "recruitmentDeadline": "2025.12.30",
                                                                    "meetingType": {
                                                                        "desc": "온라인",
                                                                        "name": "ONLINE"
                                                                    },
                                                                    "grade": 3,
                                                                    "content": "이 프로젝트에 지원하고 싶습니다.",
                                                                    "status": {
                                                                        "desc": "거절됨",
                                                                        "name": "REJECTED"
                                                                    },
                                                                    "position": {
                                                                        "desc": "백엔드",
                                                                        "name": "BACK_END"
                                                                    },
                                                                    "skills": [
                                                                        {
                                                                            "desc": "Spring Boot",
                                                                            "name": "SPRING_BOOT"
                                                                        },
                                                                        {
                                                                            "desc": "MySQL",
                                                                            "name": "MYSQL"
                                                                        },
                                                                        {
                                                                            "desc": "Java",
                                                                            "name": "JAVA"
                                                                        }
                                                                    ]
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
                                    ),
                                    @ExampleObject(
                                            name = "과제 공고 지원 내역",
                                            description = "`/applications/me?category=assigment`",
                                            value = """
                                                    {
                                                        "code": "SUCCESS",
                                                        "message": "과제 공고 지원 내역을 성공적으로 조회하였습니다.",
                                                        "data": {
                                                            "content": [
                                                                {
                                                                    "applicationId": 20,
                                                                    "recruitmentId": 132,
                                                                    "recruitmentTitle": "컴퓨터비전: Canny/HoG/SIFT 비교 리포트",
                                                                    "recruitmentDeadline": "2025.12.05",
                                                                    "meetingType": {
                                                                        "desc": "온라인",
                                                                        "name": "ONLINE"
                                                                    },
                                                                    "grade": 3,
                                                                    "content": "저 재수강이에요.",
                                                                    "status": {
                                                                        "desc": "대기중",
                                                                        "name": "SUBMITTED"
                                                                    }
                                                                },
                                                                {
                                                                    "applicationId": 10,
                                                                    "recruitmentId": 123,
                                                                    "recruitmentTitle": "운영체제 스케줄러 시뮬레이터 구현",
                                                                    "recruitmentDeadline": "2025.12.01",
                                                                    "meetingType": {
                                                                        "desc": "온라인",
                                                                        "name": "ONLINE"
                                                                    },
                                                                    "grade": 3,
                                                                    "content": "이 공고에 지원하고 싶습니다.",
                                                                    "status": {
                                                                        "desc": "수락됨",
                                                                        "name": "ACCEPTED"
                                                                    }
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
                                            name = "스터디 공고 지원 내역",
                                            description = "`/applications/me?category=study`",
                                            value = """
                                                    {
                                                        "code": "SUCCESS",
                                                        "message": "스터디 공고 지원 내역을 성공적으로 조회하였습니다.",
                                                        "data": {
                                                            "content": [
                                                                {
                                                                    "applicationId": 9,
                                                                    "recruitmentId": 27,
                                                                    "recruitmentTitle": "Spring 스터디 모집",
                                                                    "recruitmentDeadline": "2025.10.31",
                                                                    "meetingType": {
                                                                        "desc": "온라인",
                                                                        "name": "ONLINE"
                                                                    },
                                                                    "grade": 3,
                                                                    "content": "이 스터디에 지원하고 싶습니다.",
                                                                    "status": {
                                                                        "desc": "대기중",
                                                                        "name": "SUBMITTED"
                                                                    }
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
                                            name = "지원 내역 없음",
                                            value = """
                                                    {
                                                        "code": "SUCCESS",
                                                        "message": "스터디 공고 지원 내역을 성공적으로 조회하였습니다.",
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
                    description = "유효하지 않은 요청 값 또는 비즈니스 검증 실패로 인해 요청이 거부된 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "카테고리가 누락된 경우",
                                            value = """
                                                    {
                                                        "code": "REQUIRED_FIELD_MISSING",
                                                        "message": "필수 값이 누락되었습니다.",
                                                        "data": "category"
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
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    ResponseEntity<APIResponse<Page<ApplicationCommonResponseDto>>> getMyApplicationByCategory(
            @RequestParam("category") RecruitmentCategory category,
            @PageableDefault(size = 5) Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "지원 수락",
            description = "사용자의 공고에 들어온 지원을 수락한다.",
            security = @SecurityRequirement(name = "Bearer Token"),
            parameters = {
                    @Parameter(
                            name = "applicationId",
                            description = "수락하려는 지원 ID",
                            required = true,
                            in = ParameterIn.PATH,
                            example = "13"
                    )
            }
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "지원 수락 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "SUCCESS",
                                                        "message": "공고 지원 요청을 수락하였습니다."
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
                    description = "지원을 수락할 권한이 없는 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "FORBIDDEN_DECIDE_APPLICATION",
                                                        "message": "지원 수락 권한이 없습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "수락하려는 지원이 존재하지 않는 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "APPLICATION_NOT_FOUND",
                                                        "message": "지원 정보를 찾을 수 없습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "지원 수락이 불가능한 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "이미 처리된 지원서인 경우",
                                            value = """
                                                    {
                                                        "code": "ALREADY_PROCESSED_APPLICATION",
                                                        "message": "이미 처리된 지원서입니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "모집하지 않는 포지션에 대한 지원인 경우",
                                            description = "프로젝트 공고 지원에 해당",
                                            value = """
                                                    {
                                                        "code": "NOT_RECRUITING_POSITION",
                                                        "message": "해당 포지션은 모집 대상이 아닙니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "이미 모집이 완료된 포지션에 대한 지원인 경우",
                                            description = "프로젝트 공고 지원에 해당",
                                            value = """
                                                    {
                                                        "code": "POSITION_FULL",
                                                        "message": "이미 모집이 완료된 포지션입니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "이미 모집이 완료된 경우",
                                            description = "과제/스터디 공고 지원에 해당",
                                            value = """
                                                    {
                                                        "code": "TEAM_FULL",
                                                        "message": "이미 모집이 완료되었습니다."
                                                    }
                                                    """
                                    ),
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
    @PostMapping("{applicationId}/accept")
    @PreAuthorize("isAuthenticated()")
    ResponseEntity<APIResponse<Void>> acceptApplication(
            @PathVariable("applicationId") Long applicationId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "지원 거절",
            description = "사용자의 공고에 들어온 지원을 거절한다.",
            security = @SecurityRequirement(name = "Bearer Token"),
            parameters = {
                    @Parameter(
                            name = "applicationId",
                            description = "거절하려는 지원 ID",
                            required = true,
                            in = ParameterIn.PATH,
                            example = "15"
                    )
            }
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "지원 거절 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "SUCCESS",
                                                        "message": "공고 지원 요청을 거절하였습니다."
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
                    description = "지원을 거절할 권한이 없는 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "FORBIDDEN_DECIDE_APPLICATION",
                                                        "message": "지원 거절 권한이 없습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "거절하려는 지원이 존재하지 않는 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "APPLICATION_NOT_FOUND",
                                                        "message": "지원 정보를 찾을 수 없습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "지원 거절이 불가능한 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "ALREADY_PROCESSED_APPLICATION",
                                                        "message": "이미 처리된 지원서입니다."
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
    @PostMapping("{applicationId}/reject")
    @PreAuthorize("isAuthenticated()")
    ResponseEntity<APIResponse<Void>> rejectApplication(
            @PathVariable("applicationId") Long applicationId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "지원 취소/삭제",
            description = "사용자가 제출한 지원을 취소/삭제한다.",
            security = @SecurityRequirement(name = "Bearer Token"),
            parameters = {
                    @Parameter(
                            name = "applicationId",
                            description = "취소하려는 지원 ID",
                            required = true,
                            in = ParameterIn.PATH,
                            example = "44"
                    )
            }
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "지원 취소/삭제 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "SUCCESS",
                                                        "message": "지원을 성공적으로 취소/삭제하였습니다."
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
                    description = "지원을 취소/삭제할 권한이 없는 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "CANNOT_DELETE_ANOTHER_USER_APPLICATION",
                                                        "message": "다른 사용자의 지원은 취소/삭제할 수 없습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "취소/삭제하려는 지원이 존재하지 않는 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "APPLICATION_NOT_FOUND",
                                                        "message": "지원 정보를 찾을 수 없습니다."
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
    @DeleteMapping("{applicationId}")
    @PreAuthorize("isAuthenticated()")
    ResponseEntity<APIResponse<Void>> cancelApplication(
            @PathVariable("applicationId") Long applicationId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );
}
