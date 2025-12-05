package com.wagglex2.waggle.domain.assignment.controller.docs;

import com.wagglex2.waggle.common.response.APIResponse;
import com.wagglex2.waggle.common.security.CustomUserDetails;
import com.wagglex2.waggle.domain.assignment.dto.request.AssignmentCreationRequestDto;
import com.wagglex2.waggle.domain.assignment.dto.request.AssignmentUpdateRequestDto;
import com.wagglex2.waggle.domain.assignment.dto.response.AssignmentDetailResponseDto;
import com.wagglex2.waggle.domain.assignment.dto.response.AssignmentSummaryResponseDto;
import com.wagglex2.waggle.domain.common.dto.response.RecruitmentWithAppsResponseDto;
import com.wagglex2.waggle.domain.common.type.RecruitmentStatus;
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
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Set;

@Tag(name = "과제 공고", description = "과제 공고 관련 API")
public interface AssignmentControllerDocs {

    @Operation(
            summary = "과제 공고 등록",
            description = "과제 공고를 등록한다.",
            security = @SecurityRequirement(name = "Bearer Token"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "과제 공고 작성 내용",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AssignmentCreationRequestDto.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "title": "데이터베이스 설계 과제 팀원 모집",
                                                        "content": "데이터베이스 설계 과제를 함께 진행할 팀원을 모집합니다.",
                                                        "department": "컴퓨터공학과",
                                                        "lecture": "데이터베이스",
                                                        "lectureCode": "CS301",
                                                        "maxParticipants": 3,
                                                        "grades": [
                                                            { "grade": 2 },
                                                            { "grade": 3 }
                                                        ],
                                                        "deadline": "2025-12-20T23:59:59"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = """
                            과제 공고 등록 성공<br>
                            data: 과제 공고 ID
                            """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "SUCCESS",
                                                        "message": "과제 공고를 성공적으로 등록하였습니다.",
                                                        "data": 142
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
                                                                "field": "maxParticipants",
                                                                "message": "모집 인원은 1 이상이어야 합니다."
                                                            },
                                                            {
                                                                "field": "department",
                                                                "message": "학과명이 누락되었습니다."
                                                            },
                                                            {
                                                                "field": "grades[].grade",
                                                                "message": "모집 학년은 1 이상 4 이하여야 합니다."
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
    ResponseEntity<APIResponse<Long>> createAssignment(
            @RequestBody @Valid AssignmentCreationRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "과제 공고 상세 조회",
            description = "과제 공고 1건을 상세 조회한다.",
            security = @SecurityRequirement(name = "Bearer Token"),
            parameters = {
                  @Parameter(
                          name = "assignmentId",
                          description = "조회하려는 과제 공고 ID",
                          required = true,
                          in = ParameterIn.PATH,
                          example = "3"
                  )
            }
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "과제 공고 상세 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AssignmentDetailResponseDto.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "SUCCESS",
                                                        "message": "과제 공고를 성공적으로 조회하였습니다.",
                                                        "data": {
                                                            "id": 259,
                                                            "authorId": 1,
                                                            "authorNickname": "민민민재",
                                                            "authorProfileImageUrl": "https://waggle-image-bucket.s3.ap-northeast-2.amazonaws.com/user-profile-images/default-profile-image.png",
                                                            "category": {
                                                                "desc": "과제",
                                                                "name": "ASSIGNMENT"
                                                            },
                                                            "university": {
                                                                "desc": "영남대",
                                                                "domain": "yu.ac.kr",
                                                                "name": "YOUNGNAM_UNIV"
                                                            },
                                                            "title": "데이터베이스 설계 과제 팀원 모집",
                                                            "content": "데이터베이스 설계 과제를 함께 진행할 팀원을 모집합니다.",
                                                            "deadline": "2025-12-20 23:59:59",
                                                            "createdAt": "2025-11-27 00:28:49",
                                                            "status": {
                                                                "desc": "모집 중",
                                                                "name": "RECRUITING"
                                                            },
                                                            "viewCount": 1,
                                                            "department": "컴퓨터공학과",
                                                            "lecture": "데이터베이스",
                                                            "lectureCode": "CS301",
                                                            "participants": {
                                                                "maxParticipants": 3,
                                                                "currParticipants": 0
                                                            },
                                                            "grades": [
                                                                2,
                                                                3
                                                            ],
                                                            "bookmarked": false
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
                    responseCode = "403",
                    description = "타 대학 공고인 경우",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "FORBIDDEN_CROSS_UNIVERSITY_RECRUITMENT",
                                                        "message": "타 대학의 공고입니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "해당 과제 공고를 찾을 수 없는 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "ASSIGNMENT_NOT_FOUND",
                                                        "message": "과제 공고를 찾을 수 없습니다."
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
    ResponseEntity<APIResponse<AssignmentDetailResponseDto>> getAssignment(
            @PathVariable Long assignmentId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "과제 공고 목록 조회(검색)",
            description = """
                    과제 공고 목록을 조회(검색)한다.<br>
                    기본 정렬은 생성일 기준 최신순이다.<br>
                    모든 조건은 AND로 처리된다. 단, 각 검색어, 학년은 OR로 처리된다.<br>
                    
                    예시:
                    
                    요청 - `/assignments?q=데이터베이스+과제&grades=2,3&status=recruiting`<br>
                    
                    처리 - ("데이터베이스" OR "과제") AND (2 OR 3) AND recruiting
                    """,
            security = @SecurityRequirement(name = "Bearer Token"),
            parameters = {
                    @Parameter(
                            name = "q",
                            description = """
                                    검색어<br>
                                    띄어쓰기는 `+`로 구분한다.
                                    """,
                            in = ParameterIn.QUERY,
                            example = "데이터베이스+과제"
                    ),
                    @Parameter(
                            name = "grades",
                            description = """
                                    조회하려는 학년<br>
                                    값은 `,`로 구별한다.<br>
                                    예시: `/assignments?grades=2,3`
                                    """,
                            in = ParameterIn.QUERY
                    ),
                    @Parameter(
                            name = "status",
                            description = """
                                    조회하려는 공고 상태(마감 여부)<br>
                                    기본값(미지정): `모집 중`, `마감` 모두
                                    """,
                            in = ParameterIn.QUERY,
                            schema = @Schema(allowableValues = {"RECRUITING", "CLOSED"})
                    ),
                    @Parameter(
                            name = "page",
                            description = """
                                    조회하려는 과제 공고 목록 페이지<br>
                                    기본값: 0 (0부터 시작)
                                    """,
                            in = ParameterIn.QUERY
                    ),
                    @Parameter(
                            name = "size",
                            description = """
                                    조회하려는 과제 공고 개수<br>
                                    기본값: 9
                                    """,
                            in = ParameterIn.QUERY
                    )
            }
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "과제 공고 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = AssignmentSummaryResponseDto.class)
                            ),
                            examples = {
                                    @ExampleObject(
                                            name = "/assignments?grades=2,3&page=0&size=3",
                                            description = "`/assignments?grades=2,3&page=0&size=3`",
                                            value = """
                                                    {
                                                        "code": "SUCCESS",
                                                        "message": "과제 공고 목록을 성공적으로 조회하였습니다.",
                                                        "data": {
                                                            "content": [
                                                                {
                                                                    "id": 259,
                                                                    "authorId": 1,
                                                                    "authorNickname": "민민민재",
                                                                    "authorProfileImageUrl": "https://waggle-image-bucket.s3.ap-northeast-2.amazonaws.com/user-profile-images/default-profile-image.png",
                                                                    "university": {
                                                                        "desc": "영남대",
                                                                        "domain": "yu.ac.kr",
                                                                        "name": "YOUNGNAM_UNIV"
                                                                    },
                                                                    "category": {
                                                                        "desc": "과제",
                                                                        "name": "ASSIGNMENT"
                                                                    },
                                                                    "title": "데이터베이스 설계 과제 팀원 모집",
                                                                    "deadline": "2025-12-20",
                                                                    "status": {
                                                                        "desc": "모집 중",
                                                                        "name": "RECRUITING"
                                                                    },
                                                                    "department": "컴퓨터공학과",
                                                                    "lecture": "데이터베이스",
                                                                    "lectureCode": "CS301",
                                                                    "grades": [2, 3],
                                                                    "bookmarked": false
                                                                },
                                                                {
                                                                    "id": 170,
                                                                    "authorId": 34,
                                                                    "authorNickname": "매운새우깡",
                                                                    "authorProfileImageUrl": "https://waggle-image-bucket.s3.ap-northeast-2.amazonaws.com/user-profile-images/default-profile-image.png",
                                                                    "university": {
                                                                        "desc": "영남대",
                                                                        "domain": "yu.ac.kr",
                                                                        "name": "YOUNGNAM_UNIV"
                                                                    },
                                                                    "category": {
                                                                        "desc": "과제",
                                                                        "name": "ASSIGNMENT"
                                                                    },
                                                                    "title": "알고리즘 과제 팀원 모집",
                                                                    "deadline": "2025-12-25",
                                                                    "status": {
                                                                        "desc": "모집 중",
                                                                        "name": "RECRUITING"
                                                                    },
                                                                    "department": "컴퓨터공학과",
                                                                    "lecture": "알고리즘",
                                                                    "lectureCode": "CS302",
                                                                    "grades": [2, 3],
                                                                    "bookmarked": false
                                                                },
                                                                {
                                                                    "id": 166,
                                                                    "authorId": 34,
                                                                    "authorNickname": "매운새우깡",
                                                                    "authorProfileImageUrl": "https://waggle-image-bucket.s3.ap-northeast-2.amazonaws.com/user-profile-images/default-profile-image.png",
                                                                    "university": {
                                                                        "desc": "영남대",
                                                                        "domain": "yu.ac.kr",
                                                                        "name": "YOUNGNAM_UNIV"
                                                                    },
                                                                    "category": {
                                                                        "desc": "과제",
                                                                        "name": "ASSIGNMENT"
                                                                    },
                                                                    "title": "웹 프로그래밍 과제 팀원 모집",
                                                                    "deadline": "2025-12-30",
                                                                    "status": {
                                                                        "desc": "모집 중",
                                                                        "name": "RECRUITING"
                                                                    },
                                                                    "department": "컴퓨터공학과",
                                                                    "lecture": "웹 프로그래밍",
                                                                    "lectureCode": "CS303",
                                                                    "grades": [2, 3],
                                                                    "bookmarked": false
                                                                }
                                                            ],
                                                            "page": {
                                                                "size": 3,
                                                                "number": 0,
                                                                "totalElements": 24,
                                                                "totalPages": 8
                                                            }
                                                        }
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "/assignments?q=데이터베이스&page=0&size=3",
                                            description = "`/assignments?q=데이터베이스&page=0&size=3`",
                                            value = """
                                                    {
                                                        "code": "SUCCESS",
                                                        "message": "과제 공고 목록을 성공적으로 조회하였습니다.",
                                                        "data": {
                                                            "content": [
                                                                {
                                                                    "id": 259,
                                                                    "authorId": 1,
                                                                    "authorNickname": "민민민재",
                                                                    "authorProfileImageUrl": "https://waggle-image-bucket.s3.ap-northeast-2.amazonaws.com/user-profile-images/default-profile-image.png",
                                                                    "university": {
                                                                        "desc": "영남대",
                                                                        "domain": "yu.ac.kr",
                                                                        "name": "YOUNGNAM_UNIV"
                                                                    },
                                                                    "category": {
                                                                        "desc": "과제",
                                                                        "name": "ASSIGNMENT"
                                                                    },
                                                                    "title": "데이터베이스 설계 과제 팀원 모집",
                                                                    "deadline": "2025-12-20",
                                                                    "status": {
                                                                        "desc": "모집 중",
                                                                        "name": "RECRUITING"
                                                                    },
                                                                    "department": "컴퓨터공학과",
                                                                    "lecture": "데이터베이스",
                                                                    "lectureCode": "CS301",
                                                                    "grades": [2, 3],
                                                                    "bookmarked": false
                                                                }
                                                            ],
                                                            "page": {
                                                                "size": 3,
                                                                "number": 0,
                                                                "totalElements": 1,
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
                    description = "요청 값이 유효하지 않은 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "enum 조건이 잘못된 값인 경우",
                                            description = "`/assignments?page=5&size=3&status=banana`",
                                            value = """
                                                    {
                                                        "code": "INVALID_ENUM_VALUE",
                                                        "message": "쿼리 파라미터 값이 유효하지 않습니다. 허용 가능한 값 목록을 확인해주세요.",
                                                        "data": "쿼리 파라미터 'status'의 값 'banana'이(가) 유효하지 않습니다."
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
    ResponseEntity<APIResponse<Page<AssignmentSummaryResponseDto>>> getAssignmentSummaries(
            @RequestParam(value = "q", required = false) String keywords,
            @RequestParam(value = "grades", required = false) Set<Integer> grades,
            @RequestParam(value = "status", required = false) RecruitmentStatus status,
            @PageableDefault(size = 9) Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "내 과제 공고 목록 조회",
            description = """
                    사용자별 등록한 과제 공고 목록을 조회한다.<br>
                    각 공고에 해당하는 지원 정보를 포함한다.<br>
                    응답의 공고와 각 공고에 대한 지원 정보는 생성 일자, 지원 일자 기준 최신순이다.
                    """,
            security = @SecurityRequirement(name = "Bearer Token"),
            parameters = {
                    @Parameter(
                            name = "page",
                            description = """
                                    조회하려는 내 과제 공고 목록 페이지<br>
                                    기본값: 0 (0부터 시작)
                                    """,
                            in = ParameterIn.QUERY
                    ),
                    @Parameter(
                            name = "size",
                            description = """
                                    조회하려는 내 과제 공고 개수<br>
                                    기본값: 5
                                    """,
                            in = ParameterIn.QUERY
                    )
            }
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "내 과제 공고 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = RecruitmentWithAppsResponseDto.class)
                            ),
                            examples = {
                                    @ExampleObject(
                                            name = "/assignments/me?page=1&size=3",
                                            description = "`/assignments/me?page=1&size=3`",
                                            value = """
                                                    {
                                                        "code": "SUCCESS",
                                                        "message": "내 과제 공고 목록을 성공적으로 조회하였습니다.",
                                                        "data": {
                                                            "content": [
                                                                {
                                                                    "recruitmentId": 139,
                                                                    "title": "데이터베이스 설계 과제 팀원 모집",
                                                                    "deadline": "2025.12.20",
                                                                    "applications": []
                                                                },
                                                                {
                                                                    "recruitmentId": 138,
                                                                    "title": "알고리즘 과제 팀원 모집",
                                                                    "deadline": "2025.12.25",
                                                                    "applications": []
                                                                },
                                                                {
                                                                    "recruitmentId": 137,
                                                                    "title": "웹 프로그래밍 과제 팀원 모집",
                                                                    "deadline": "2025.12.30",
                                                                    "applications": [
                                                                        {
                                                                            "applicationId": 27,
                                                                            "applicantId": 40,
                                                                            "nickname": "박데통",
                                                                            "profileImageUrl": "https://waggle-image-bucket.s3.ap-northeast-2.amazonaws.com/user-profile-images/default-profile-image.png",
                                                                            "grade": 3,
                                                                            "content": "이 과제에 지원하고 싶습니다.",
                                                                            "appliedAt": "2025.11.22"
                                                                        },
                                                                        {
                                                                            "applicationId": 12,
                                                                            "applicantId": 34,
                                                                            "nickname": "매운새우깡",
                                                                            "profileImageUrl": "https://waggle-image-bucket.s3.ap-northeast-2.amazonaws.com/user-profile-images/default-profile-image.png",
                                                                            "grade": 2,
                                                                            "content": "이 과제에 지원하고 싶습니다.",
                                                                            "appliedAt": "2025.11.03"
                                                                        }
                                                                    ]
                                                                }
                                                            ],
                                                            "page": {
                                                                "size": 3,
                                                                "number": 1,
                                                                "totalElements": 10,
                                                                "totalPages": 4
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
    ResponseEntity<APIResponse<Page<RecruitmentWithAppsResponseDto>>> getMyAssignments(
            @PageableDefault(size = 5) Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "과제 공고 찜 목록 조회",
            description = "과제 공고 찜 목록을 찜 일자 기준 최신순으로 조회한다.",
            security = @SecurityRequirement(name = "Bearer Token"),
            parameters = {
                    @Parameter(
                            name = "status",
                            description = """
                                    조회하려는 공고 상태<br>
                                    기본값(미지정): `모집 중`, `마감` 모두
                                    """,
                            in = ParameterIn.QUERY,
                            schema = @Schema(allowableValues = {"RECRUITING", "CLOSED"})
                    ),
                    @Parameter(
                            name = "page",
                            description = """
                                    조회하려는 찜 목록 페이지<br>
                                    기본값: 0 (0부터 시작)
                                    """,
                            in = ParameterIn.QUERY
                    ),
                    @Parameter(
                            name = "size",
                            description = """
                                    조회하려는 과제 공고 개수<br>
                                    기본값: 9
                                    """,
                            in = ParameterIn.QUERY
                    )
            }
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "과제 공고 찜 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = AssignmentSummaryResponseDto.class)
                            ),
                            examples = {
                                    @ExampleObject(
                                            name = "status(상태) 미지정",
                                            description = "`/assignments/bookmarks?size=5`",
                                            value = """
                                                    {
                                                        "code": "SUCCESS",
                                                        "message": "과제 공고 찜 목록을 성공적으로 조회하였습니다.",
                                                        "data": {
                                                            "content": [
                                                                {
                                                                    "id": 118,
                                                                    "authorId": 1,
                                                                    "authorNickname": "민민민재",
                                                                    "authorProfileImageUrl": "https://waggle-image-bucket.s3.ap-northeast-2.amazonaws.com/user-profile-images/default-profile-image.png",
                                                                    "university": {
                                                                        "desc": "영남대",
                                                                        "domain": "yu.ac.kr",
                                                                        "name": "YOUNGNAM_UNIV"
                                                                    },
                                                                    "category": {
                                                                        "desc": "과제",
                                                                        "name": "ASSIGNMENT"
                                                                    },
                                                                    "title": "데이터베이스 설계 과제 팀원 모집",
                                                                    "deadline": "2025-11-11",
                                                                    "status": {
                                                                        "desc": "마감",
                                                                        "name": "CLOSED"
                                                                    },
                                                                    "department": "컴퓨터공학과",
                                                                    "lecture": "데이터베이스",
                                                                    "lectureCode": "CS301",
                                                                    "grades": [2, 3],
                                                                    "bookmarkId": 30,
                                                                    "bookmarked": true
                                                                },
                                                                {
                                                                    "id": 117,
                                                                    "authorId": 1,
                                                                    "authorNickname": "민민민재",
                                                                    "authorProfileImageUrl": "https://waggle-image-bucket.s3.ap-northeast-2.amazonaws.com/user-profile-images/default-profile-image.png",
                                                                    "university": {
                                                                        "desc": "영남대",
                                                                        "domain": "yu.ac.kr",
                                                                        "name": "YOUNGNAM_UNIV"
                                                                    },
                                                                    "category": {
                                                                        "desc": "과제",
                                                                        "name": "ASSIGNMENT"
                                                                    },
                                                                    "title": "알고리즘 과제 팀원 모집",
                                                                    "deadline": "2025-11-11",
                                                                    "status": {
                                                                        "desc": "마감",
                                                                        "name": "CLOSED"
                                                                    },
                                                                    "department": "컴퓨터공학과",
                                                                    "lecture": "알고리즘",
                                                                    "lectureCode": "CS302",
                                                                    "grades": [2, 3],
                                                                    "bookmarkId": 29,
                                                                    "bookmarked": true
                                                                }
                                                            ],
                                                            "page": {
                                                                "size": 5,
                                                                "number": 0,
                                                                "totalElements": 10,
                                                                "totalPages": 2
                                                            }
                                                        }
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "모집 중인 공고만 조회",
                                            description = "`/assignments/bookmarks?status=recruiting&size=5`",
                                            value = """
                                                    {
                                                        "code": "SUCCESS",
                                                        "message": "과제 공고 찜 목록을 성공적으로 조회하였습니다.",
                                                        "data": {
                                                            "content": [
                                                                {
                                                                    "id": 119,
                                                                    "authorId": 1,
                                                                    "authorNickname": "민민민재",
                                                                    "authorProfileImageUrl": "https://waggle-image-bucket.s3.ap-northeast-2.amazonaws.com/user-profile-images/default-profile-image.png",
                                                                    "university": {
                                                                        "desc": "영남대",
                                                                        "domain": "yu.ac.kr",
                                                                        "name": "YOUNGNAM_UNIV"
                                                                    },
                                                                    "category": {
                                                                        "desc": "과제",
                                                                        "name": "ASSIGNMENT"
                                                                    },
                                                                    "title": "웹 프로그래밍 과제 팀원 모집",
                                                                    "deadline": "2025-12-20",
                                                                    "status": {
                                                                        "desc": "모집 중",
                                                                        "name": "RECRUITING"
                                                                    },
                                                                    "department": "컴퓨터공학과",
                                                                    "lecture": "웹 프로그래밍",
                                                                    "lectureCode": "CS303",
                                                                    "grades": [2, 3],
                                                                    "bookmarkId": 27,
                                                                    "bookmarked": true
                                                                }
                                                            ],
                                                            "page": {
                                                                "size": 5,
                                                                "number": 0,
                                                                "totalElements": 5,
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
    ResponseEntity<APIResponse<Page<AssignmentSummaryResponseDto>>> getMyBookmarks(
            @RequestParam(value = "status", required = false) RecruitmentStatus status,
            @PageableDefault(size = 9) Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "과제 공고 수정",
            description = "본인이 작성한 과제 공고 1건을 수정한다.",
            security = @SecurityRequirement(name = "Bearer Token"),
            parameters = {
                    @Parameter(
                            name = "assignmentId",
                            description = "수정하려는 과제 공고 ID",
                            required = true,
                            in = ParameterIn.PATH,
                            example = "23"
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "과제 공고 수정 내용",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AssignmentUpdateRequestDto.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "title": "데이터베이스 설계 과제 팀원 모집 (추가 모집)",
                                                        "content": "데이터베이스 설계 과제를 함께 진행할 팀원을 추가로 모집합니다.",
                                                        "department": "컴퓨터공학과",
                                                        "lecture": "데이터베이스",
                                                        "lectureCode": "CS301",
                                                        "participants": {
                                                            "maxParticipants": 5,
                                                            "currParticipants": 2
                                                        },
                                                        "grades": [
                                                            { "grade": 2 },
                                                            { "grade": 3 }
                                                        ],
                                                        "deadline": "2025-12-25T23:59:59"
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
                    description = "과제 공고 수정 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "SUCCESS",
                                                        "message": "과제 공고를 성공적으로 수정하였습니다."
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
                            examples = {
                                    @ExampleObject(
                                            name = "현재 참가 인원이 모집 인원보다 많은 경우",
                                            value = """
                                                    {
                                                        "code": "MAX_PARTICIPANTS_EXCEEDED",
                                                        "message": "참가 인원이 최대 모집 인원을 초과했습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "요청 값이 유효하지 않은 경우",
                                            value = """
                                                    {
                                                        "code": "VALIDATION_FAILED",
                                                        "message": "요청 값이 유효하지 않습니다.",
                                                        "data": [
                                                            {
                                                                "field": "participants.maxParticipants",
                                                                "message": "모집 인원은 1 이상이어야 합니다."
                                                            },
                                                            {
                                                                "field": "department",
                                                                "message": "학과명이 누락되었습니다."
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
                    description = "다른 사용자의 과제 공고를 수정하려는 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "CANNOT_UPDATE_ANOTHER_USER_ASSIGNMENT",
                                                        "message": "다른 사용자의 과제 공고는 수정할 수 없습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "해당 과제 공고를 찾을 수 없는 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "ASSIGNMENT_NOT_FOUND",
                                                        "message": "과제 공고를 찾을 수 없습니다."
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
    ResponseEntity<APIResponse<Void>> updateAssignment(
            @PathVariable Long assignmentId,
            @RequestBody @Valid AssignmentUpdateRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "과제 공고 삭제",
            description = "본인이 작성한 과제 공고 1건을 삭제한다.",
            security = @SecurityRequirement(name = "Bearer Token"),
            parameters = {
                    @Parameter(
                            name = "assignmentId",
                            description = "삭제하려는 과제 공고 ID",
                            required = true,
                            in = ParameterIn.PATH,
                            example = "23"
                    )
            }
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "과제 공고 삭제 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "SUCCESS",
                                                        "message": "과제 공고를 성공적으로 삭제하였습니다."
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
                    description = "다른 사용자의 과제 공고를 삭제하려는 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "CANNOT_DELETE_ANOTHER_USER_ASSIGNMENT",
                                                        "message": "다른 사용자의 과제 공고는 삭제할 수 없습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "해당 과제 공고를 찾을 수 없는 경우",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "ASSIGNMENT_NOT_FOUND",
                                                        "message": "과제 공고를 찾을 수 없습니다."
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
    ResponseEntity<APIResponse<Void>> deleteAssignment(
            @PathVariable Long assignmentId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );
}
