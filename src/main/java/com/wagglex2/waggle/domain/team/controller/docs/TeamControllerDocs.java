package com.wagglex2.waggle.domain.team.controller.docs;

import com.wagglex2.waggle.common.response.APIResponse;
import com.wagglex2.waggle.common.security.CustomUserDetails;
import com.wagglex2.waggle.domain.common.type.RecruitmentCategory;
import com.wagglex2.waggle.domain.common.type.RecruitmentStatus;
import com.wagglex2.waggle.domain.team.dto.response.TeamResponseDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Team(팀)", description = "팀 관련 API")
public interface TeamControllerDocs {

    @Operation(
            summary = "로그인한 사용자의 팀 목록 조회",
            description = """
                            카테고리 및 상태별로 조회한다.
                            카테고리(PROJECT/STUDY/ASSIGNMENT), 모집 상태(RECRUITING/CLOSED/CANCELED)
                          """,
            parameters = {
                    @Parameter(
                            name = "category",
                            description = "카테고리",
                            in = ParameterIn.QUERY,
                            example = "PROJECT"
                    ),
                    @Parameter(
                            name = "status",
                            description = "모집 상태",
                            in = ParameterIn.QUERY,
                            example = "RECRUITING"
                    ),
                    @Parameter(
                            name = "size",
                            description = "가져올 리뷰 개수 (기본값: 5)",
                            in = ParameterIn.QUERY,
                            example = "3"
                    ),
                    @Parameter(
                            name = "sort",
                            description = "정렬 기준 필드(기본값: createdAt), 정렬 방향(ASC 또는 DESC, 기본값: DESC)",
                            in = ParameterIn.QUERY,
                            example = "createdAt,ASC"
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
                    description = "팀 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TeamResponseDto.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "SUCCESS",
                                                        "message": "팀 조회에 성공했습니다.",
                                                        "data": {
                                                            "content": [
                                                                {
                                                                    "id": 30,
                                                                    "recruitmentId": 163,
                                                                    "recruitmentTitle": "팀 멤버 포지션 할당 확인",
                                                                    "category": {
                                                                        "desc": "프로젝트",
                                                                        "name": "PROJECT"
                                                                    },
                                                                    "status": {
                                                                        "desc": "모집 중",
                                                                        "name": "RECRUITING"
                                                                    },
                                                                    "period": {
                                                                        "startDate": "2025-12-25",
                                                                        "endDate": "2026-03-20"
                                                                    },
                                                                    "durationDays": 85,
                                                                    "leaderNickname": "새우깡",
                                                                    "memberCount": 2,
                                                                    "members": [
                                                                        {
                                                                            "userId": 5,
                                                                            "nickname": "새우깡",
                                                                            "profileImageUrl": "https://waggle-image-bucket.s3.ap-northeast-2.amazonaws.com/user-profile-images/default-profile-image.png",
                                                                            "role": {
                                                                                "desc": "리더",
                                                                                "name": "LEADER"
                                                                            }
                                                                        },
                                                                        {
                                                                            "userId": 1,
                                                                            "nickname": "민민민재",
                                                                            "profileImageUrl": "https://waggle-image-bucket.s3.ap-northeast-2.amazonaws.com/user-profile-images/default-profile-image.png",
                                                                            "role": {
                                                                                "desc": "멤버",
                                                                                "name": "MEMBER"
                                                                            },
                                                                            "position": {
                                                                                "desc": "프론트엔드",
                                                                                "name": "FRONT_END"
                                                                            }
                                                                        }
                                                                    ]
                                                                },
                                                                {
                                                                    "id": 29,
                                                                    "recruitmentId": 162,
                                                                    "recruitmentTitle": "팀 멤버 포지션 할당 확인",
                                                                    "category": {
                                                                        "desc": "프로젝트",
                                                                        "name": "PROJECT"
                                                                    },
                                                                    "status": {
                                                                        "desc": "모집 중",
                                                                        "name": "RECRUITING"
                                                                    },
                                                                    "period": {
                                                                        "startDate": "2025-12-25",
                                                                        "endDate": "2026-03-20"
                                                                    },
                                                                    "durationDays": 85,
                                                                    "leaderNickname": "새우깡",
                                                                    "memberCount": 4,
                                                                    "members": [
                                                                        {
                                                                            "userId": 5,
                                                                            "nickname": "새우깡",
                                                                            "profileImageUrl": "https://waggle-image-bucket.s3.ap-northeast-2.amazonaws.com/user-profile-images/default-profile-image.png",
                                                                            "role": {
                                                                                "desc": "리더",
                                                                                "name": "LEADER"
                                                                            }
                                                                        },
                                                                        {
                                                                            "userId": 1,
                                                                            "nickname": "민민민재",
                                                                            "profileImageUrl": "https://waggle-image-bucket.s3.ap-northeast-2.amazonaws.com/user-profile-images/default-profile-image.png",
                                                                            "role": {
                                                                                "desc": "멤버",
                                                                                "name": "MEMBER"
                                                                            },
                                                                            "position": {
                                                                                "desc": "프론트엔드",
                                                                                "name": "FRONT_END"
                                                                            }
                                                                        },
                                                                        {
                                                                            "userId": 34,
                                                                            "nickname": "매운새우깡",
                                                                            "profileImageUrl": "https://waggle-image-bucket.s3.ap-northeast-2.amazonaws.com/user-profile-images/default-profile-image.png",
                                                                            "role": {
                                                                                "desc": "멤버",
                                                                                "name": "MEMBER"
                                                                            },
                                                                            "position": {
                                                                                "desc": "프론트엔드",
                                                                                "name": "FRONT_END"
                                                                            }
                                                                        },
                                                                        {
                                                                            "userId": 40,
                                                                            "nickname": "박데통",
                                                                            "profileImageUrl": "https://waggle-image-bucket.s3.ap-northeast-2.amazonaws.com/user-profile-images/default-profile-image.png",
                                                                            "role": {
                                                                                "desc": "멤버",
                                                                                "name": "MEMBER"
                                                                            }
                                                                        }
                                                                    ]
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
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 정렬 기준, 페이지 크기 초과, ENUM 불일치, CANCELED 불가",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "잘못된 정렬 기준",
                                            description = "http://3.35.173.28:8080/api/v1/teams/me?sort=create",
                                            value = """
                                                    {
                                                        "code": "INVALID_SORT_PROPERTY",
                                                        "message": "create는 정렬할 수 없는 필드입니다. 허용된 필드: [createdAt]"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "쿼리 파라미터 값이 유효하지 않을 때(ENUM 불일치)",
                                            description = "http://3.35.173.28:8080/api/v1/teams/me?status=closeddd",
                                            value = """
                                                    {
                                                        "code": "INVALID_ENUM_VALUE",
                                                        "message": "쿼리 파라미터 값이 유효하지 않습니다. 허용 가능한 값 목록을 확인해주세요.",
                                                        "data": "쿼리 파라미터 'status'의 값 'closeddd'이(가) 유효하지 않습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "status가 CANCELED인 경우",
                                            description = "http://3.35.173.28:8080/api/v1/teams/me?status=canceled",
                                            value = """
                                                    {
                                                        "code": "INVALID_ARGUMENT",
                                                        "message": "유효하지 않은 인자 값입니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "페이지 번호가 최댓값 이상인 경우",
                                            description = "http://3.35.173.28:8080/api/v1/teams/me?page=999999&status=closed",
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
    ResponseEntity<APIResponse<Page<TeamResponseDto>>> getMyTeamByCategory(
            @RequestParam(value = "category", defaultValue = "PROJECT") RecruitmentCategory category,
            @RequestParam(value = "status", defaultValue = "RECRUITING") RecruitmentStatus status,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(
                    size = 3,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    );
}
