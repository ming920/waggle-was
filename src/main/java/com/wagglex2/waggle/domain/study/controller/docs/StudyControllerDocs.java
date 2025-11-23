package com.wagglex2.waggle.domain.study.controller.docs;

import com.wagglex2.waggle.common.response.APIResponse;
import com.wagglex2.waggle.common.security.CustomUserDetails;
import com.wagglex2.waggle.domain.common.dto.response.RecruitmentWithAppsResponseDto;
import com.wagglex2.waggle.domain.common.type.RecruitmentStatus;
import com.wagglex2.waggle.domain.common.type.Skill;
import com.wagglex2.waggle.domain.study.dto.request.StudyCreationRequestDto;
import com.wagglex2.waggle.domain.study.dto.request.StudyUpdateRequestDto;
import com.wagglex2.waggle.domain.study.dto.response.StudyDetailResponseDto;
import com.wagglex2.waggle.domain.study.dto.response.StudySummaryResponseDto;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "스터디 공고", description = "스터디 공고 관련 API")
public interface StudyControllerDocs {

    @Operation(
            summary = "스터디 공고 등록",
            description = "스터디 공고를 등록한다.",
            security = @SecurityRequirement(name = "Bearer Token"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "**스터디 공고 작성 내용**",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = StudyCreationRequestDto.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                         "title": "Spring Boot 스터디 모집합니다.",
                                                         "content": "함께 Spring Boot를 학습하며 프로젝트를 진행할 스터디원을 모집합니다.",
                                                         "skills": ["SPRING_BOOT", "JAVA"],
                                                         "maxParticipants": 5,
                                                         "period": {
                                                             "startDate": "2025-12-25",
                                                             "endDate": "2026-03-20"
                                                         },
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
                    description = "**스터디 공고 등록 성공**",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "SUCCESS",
                                                        "message": "스터디 공고를 성공적으로 등록하였습니다.",
                                                        "data": 142
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "**유효하지 않은 요청 값 또는 비즈니스 검증 실패로 인해 요청이 거부된 경우**",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "마감일이 종료일 이후로 설정된 경우",
                                            value = """
                                                    {
                                                        "code": "INVALID_DATE_RANGE",
                                                        "message": "마감일은 종료일 이전이어야 합니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "종료일이 시작일 이전으로 설정된 경우",
                                            value = """
                                                    {
                                                        "code": "INVALID_DATE_RANGE",
                                                        "message": "종료일은 시작일 이후여야 합니다."
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
                                                                "field": "maxParticipants",
                                                                "message": "모집 인원은 1 이상이어야 합니다."
                                                            },
                                                            {
                                                                "field": "skills",
                                                                "message": "기술 스택 정보가 누락되었습니다."
                                                            }
                                                        ]
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    ResponseEntity<APIResponse<Long>> createStudy(
            @RequestBody @Valid StudyCreationRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "스터디 공고 상세 조회",
            description = "스터디 공고 1건을 상세 조회한다.",
            security = @SecurityRequirement(name = "Bearer Token"),
            parameters = {
                  @Parameter(
                          name = "studyId",
                          description = "**조회하려는 스터디 공고 ID**",
                          required = true,
                          in = ParameterIn.PATH,
                          example = "3"
                  )
            }
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "**스터디 공고 상세 조회 성공**",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = StudyDetailResponseDto.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "SUCCESS",
                                                        "message": "스터디 공고를 성공적으로 조회하였습니다.",
                                                        "data": {
                                                            "id": 142,
                                                            "authorId": 5,
                                                            "authorNickname": "새우깡",
                                                            "category": {
                                                                "desc": "스터디",
                                                                "name": "STUDY"
                                                            },
                                                            "university": {
                                                                "desc": "영남대",
                                                                "domain": "yu.ac.kr",
                                                                "name": "YOUNGNAM_UNIV"
                                                            },
                                                            "title": "Spring Boot 스터디 모집합니다.",
                                                            "content": "함께 Spring Boot를 학습하며 프로젝트를 진행할 스터디원을 모집합니다.",
                                                            "deadline": "2025-12-20 23:59:59",
                                                            "createdAt": "2025-11-17 15:10:51",
                                                            "status": {
                                                                "desc": "모집 중",
                                                                "name": "RECRUITING"
                                                            },
                                                            "viewCount": 1,
                                                            "participants": {
                                                                "maxParticipants": 5,
                                                                "currParticipants": 0
                                                            },
                                                            "skills": [
                                                                {
                                                                    "desc": "Spring Boot",
                                                                    "name": "SPRING_BOOT"
                                                                },
                                                                {
                                                                    "desc": "Java",
                                                                    "name": "JAVA"
                                                                }
                                                            ],
                                                            "period": {
                                                                "startDate": "2025-12-25",
                                                                "endDate": "2026-03-20"
                                                            }
                                                        }
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "**타 대학 공고인 경우**",
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
                    description = "**해당 스터디 공고를 찾을 수 없는 경우**",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "STUDY_NOT_FOUND",
                                                        "message": "스터디 공고를 찾을 수 없습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    ResponseEntity<APIResponse<StudyDetailResponseDto>> getStudy(
            @PathVariable Long studyId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "스터디 공고 목록 조회(검색)",
            description = """
                    스터디 공고 목록을 조회(검색)한다.<br>
                    enum 값은 대소문자와 `-`, `_`를 구분하지 않는다.
                    
                    예시:
                    - `/studies?skills=spring-boot` (O)
                    - `/studies?skills=SPRING_BOOT` (O)
                    - `/studies?skills=springboot` (X)
                    """,
            security = @SecurityRequirement(name = "Bearer Token"),
            parameters = {
                    @Parameter(
                            name = "q",
                            description = "**검색어**<br>띄어쓰기는 `+`로 구분한다.",
                            in = ParameterIn.QUERY,
                            example = "스프링+스터디"
                    ),
                    @Parameter(
                            name = "skills",
                            description = """
                                    **조회하려는 기술 스택**<br>
                                    값은 `,`로 구별한다.<br>
                                    예시: `/studies?skills=react,spring-boot`
                                    """,
                            in = ParameterIn.QUERY
                    ),
                    @Parameter(
                            name = "status",
                            description = """
                                    **조회하려는 공고 상태(마감 여부)**<br>
                                    기본값(미지정): `모집 중`, `마감` 모두
                                    """,
                            in = ParameterIn.QUERY
                    ),
                    @Parameter(
                            name = "page",
                            description = """
                                    **조회하려는 스터디 공고 목록 페이지**<br>
                                    기본값: 0 (0부터 시작)
                                    """,
                            in = ParameterIn.QUERY
                    ),
                    @Parameter(
                            name = "size",
                            description = """
                                    **조회하려는 스터디 공고 개수**<br>
                                    기본값: 9
                                    """,
                            in = ParameterIn.QUERY
                    )
            }
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "**스터디 공고 목록 조회 성공**",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = StudySummaryResponseDto.class)
                            ),
                            examples = {
                                    @ExampleObject(
                                            name = "/studies?page=0&size=3&skills=spring-boot",
                                            description = "`/studies?page=0&size=3&skills=spring-boot`",
                                            value = """
                                                    {
                                                        "code": "SUCCESS",
                                                        "message": "스터디 공고 목록을 성공적으로 조회하였습니다.",
                                                        "data": {
                                                            "content": [
                                                                {
                                                                    "id": 113,
                                                                    "authorId": 1,
                                                                    "authorNickname": "민민민재",
                                                                    "university": {
                                                                        "desc": "영남대",
                                                                        "domain": "yu.ac.kr",
                                                                        "name": "YOUNGNAM_UNIV"
                                                                    },
                                                                    "category": {
                                                                        "desc": "스터디",
                                                                        "name": "STUDY"
                                                                    },
                                                                    "title": "Spring Boot 스터디 모집합니다.",
                                                                    "deadline": "2025-11-11",
                                                                    "status": {
                                                                        "desc": "모집 중",
                                                                        "name": "RECRUITING"
                                                                    },
                                                                    "skills": [
                                                                        {
                                                                            "desc": "Spring Boot",
                                                                            "name": "SPRING_BOOT"
                                                                        },
                                                                        {
                                                                            "desc": "Java",
                                                                            "name": "JAVA"
                                                                        }
                                                                    ]
                                                                },
                                                                {
                                                                    "id": 23,
                                                                    "authorId": 20,
                                                                    "authorNickname": "우주멋쟁이",
                                                                    "university": {
                                                                        "desc": "영남대",
                                                                        "domain": "yu.ac.kr",
                                                                        "name": "YOUNGNAM_UNIV"
                                                                    },
                                                                    "category": {
                                                                        "desc": "스터디",
                                                                        "name": "STUDY"
                                                                    },
                                                                    "title": "React 스터디 함께하실 분",
                                                                    "deadline": "2025-10-25",
                                                                    "status": {
                                                                        "desc": "모집 중",
                                                                        "name": "RECRUITING"
                                                                    },
                                                                    "skills": [
                                                                        {
                                                                            "desc": "React",
                                                                            "name": "REACT"
                                                                        },
                                                                        {
                                                                            "desc": "JavaScript",
                                                                            "name": "JAVASCRIPT"
                                                                        }
                                                                    ]
                                                                },
                                                                {
                                                                    "id": 19,
                                                                    "authorId": 5,
                                                                    "authorNickname": "새우깡",
                                                                    "university": {
                                                                        "desc": "영남대",
                                                                        "domain": "yu.ac.kr",
                                                                        "name": "YOUNGNAM_UNIV"
                                                                    },
                                                                    "category": {
                                                                        "desc": "스터디",
                                                                        "name": "STUDY"
                                                                    },
                                                                    "title": "알고리즘 스터디 모집",
                                                                    "deadline": "2025-11-11",
                                                                    "status": {
                                                                        "desc": "모집 중",
                                                                        "name": "RECRUITING"
                                                                    },
                                                                    "skills": [
                                                                        {
                                                                            "desc": "Python",
                                                                            "name": "PYTHON"
                                                                        }
                                                                    ]
                                                                }
                                                            ],
                                                            "page": {
                                                                "size": 3,
                                                                "number": 0,
                                                                "totalElements": 16,
                                                                "totalPages": 6
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
                    description = "**요청 값이 유효하지 않은 경우**",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "/studies?page=5&size=3&skills=banana",
                                            description = "`/studies?page=5&size=3&skills=banana`",
                                            value = """
                                                    {
                                                        "code": "INVALID_ENUM_VALUE",
                                                        "message": "쿼리 파라미터 값이 유효하지 않습니다. 허용 가능한 값 목록을 확인해주세요.",
                                                        "data": "쿼리 파라미터 'skills'의 값 'banana'이(가) 유효하지 않습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    ResponseEntity<APIResponse<Page<StudySummaryResponseDto>>> getStudySummaries(
            @RequestParam(value = "q", required = false) String keywords,
            @RequestParam(value = "skills", required = false) List<Skill> skills,
            @RequestParam(value = "status", required = false) RecruitmentStatus status,
            @PageableDefault(size = 9) Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "내 스터디 공고 목록 조회",
            description = """
                    사용자별 등록한 스터디 공고 목록을 조회한다.<br>
                    각 공고에 해당하는 지원 정보를 포함한다.<br>
                    응답의 공고와 각 공고에 대한 지원 정보는 생성 일자, 지원 일자 기준 최신순이다.
                    """,
            security = @SecurityRequirement(name = "Bearer Token"),
            parameters = {
                    @Parameter(
                            name = "page",
                            description = """
                                    **조회하려는 내 스터디 공고 목록 페이지**<br>
                                    기본값: 0 (0부터 시작)
                                    """,
                            in = ParameterIn.QUERY
                    ),
                    @Parameter(
                            name = "size",
                            description = """
                                    **조회하려는 내 스터디 공고 개수**<br>
                                    기본값: 5
                                    """,
                            in = ParameterIn.QUERY
                    )
            }
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "**내 스터디 공고 목록 조회 성공**",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = RecruitmentWithAppsResponseDto.class)
                            ),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "SUCCESS",
                                                        "message": "내 스터디 공고 목록을 성공적으로 조회하였습니다.",
                                                        "data": {
                                                            "content": [
                                                                {
                                                                    "recruitmentId": 141,
                                                                    "title": "Spring Boot 스터디 모집합니다.",
                                                                    "deadline": "2025.12.20",
                                                                    "applications": []
                                                                },
                                                                {
                                                                    "recruitmentId": 137,
                                                                    "title": "React 스터디 함께하실 분",
                                                                    "deadline": "2025.12.20",
                                                                    "applications": [
                                                                        {
                                                                            "applicationId": 27,
                                                                            "applicantId": 40,
                                                                            "nickname": "박데통",
                                                                            "meetingType": {
                                                                                "desc": "온라인",
                                                                                "name": "ONLINE"
                                                                            },
                                                                            "grade": 3,
                                                                            "content": "이 스터디에 지원하고 싶습니다.",
                                                                            "appliedAt": "2025.11.22",
                                                                            "position": null,
                                                                            "skills": [
                                                                                {
                                                                                    "desc": "JavaScript",
                                                                                    "name": "JAVASCRIPT"
                                                                                },
                                                                                {
                                                                                    "desc": "React",
                                                                                    "name": "REACT"
                                                                                }
                                                                            ]
                                                                        },
                                                                        {
                                                                            "applicationId": 12,
                                                                            "applicantId": 34,
                                                                            "nickname": "매운새우깡",
                                                                            "meetingType": {
                                                                                "desc": "온라인",
                                                                                "name": "ONLINE"
                                                                            },
                                                                            "grade": 3,
                                                                            "content": "이 스터디에 지원하고 싶습니다.",
                                                                            "appliedAt": "2025.11.03",
                                                                            "position": null,
                                                                            "skills": [
                                                                                {
                                                                                    "desc": "MySQL",
                                                                                    "name": "MYSQL"
                                                                                },
                                                                                {
                                                                                    "desc": "Spring Boot",
                                                                                    "name": "SPRING_BOOT"
                                                                                },
                                                                                {
                                                                                    "desc": "Java",
                                                                                    "name": "JAVA"
                                                                                }
                                                                            ]
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
                    responseCode = "401",
                    description = "**인증 필요**",
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
                    description = "**서버 오류 발생**",
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
    ResponseEntity<APIResponse<Page<RecruitmentWithAppsResponseDto>>> getMyStudies(
            @PageableDefault(size = 5) Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "스터디 공고 수정",
            description = "본인이 작성한 스터디 공고 1건을 수정한다.",
            security = @SecurityRequirement(name = "Bearer Token"),
            parameters = {
                    @Parameter(
                            name = "studyId",
                            description = "**수정하려는 스터디 공고 ID**",
                            required = true,
                            in = ParameterIn.PATH,
                            example = "23"
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "**스터디 공고 수정 내용**",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = StudyUpdateRequestDto.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "title": "Spring Boot 스터디 모집합니다. (추가 모집)",
                                                        "content": "함께 Spring Boot를 학습하며 프로젝트를 진행할 스터디원을 모집합니다.",
                                                        "skills": ["VUE_JS", "DJANGO"],
                                                        "participants": {
                                                            "maxParticipants": 5,
                                                            "currParticipants": 2
                                                        },
                                                        "period": {
                                                            "startDate": "2025-12-25",
                                                            "endDate": "2026-03-20"
                                                        },
                                                        "deadline": "2025-12-30T23:59:59"
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
                    description = "**스터디 공고 수정 성공**",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "SUCCESS",
                                                        "message": "스터디 공고를 성공적으로 수정하였습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "**입력 값이 유효하지 않은 경우**",
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
                                            name = "마감일이 종료일 이후인 경우",
                                            value = """
                                                    {
                                                        "code": "INVALID_DATE_RANGE",
                                                        "message": "마감일은 종료일 이전이어야 합니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "**다른 사용자의 스터디 공고를 수정하려는 경우**",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "CANNOT_UPDATE_ANOTHER_USER_STUDY",
                                                        "message": "다른 사용자의 스터디 공고는 수정할 수 없습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "**해당 스터디 공고를 찾을 수 없는 경우**",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "STUDY_NOT_FOUND",
                                                        "message": "스터디 공고를 찾을 수 없습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
    })
    ResponseEntity<APIResponse<Void>> updateStudy(
            @PathVariable Long studyId,
            @RequestBody @Valid StudyUpdateRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );

    @Operation(
            summary = "스터디 공고 삭제",
            description = "본인이 작성한 스터디 공고 1건을 삭제한다.",
            security = @SecurityRequirement(name = "Bearer Token"),
            parameters = {
                    @Parameter(
                            name = "studyId",
                            description = "**삭제하려는 스터디 공고 ID**",
                            required = true,
                            in = ParameterIn.PATH,
                            example = "23"
                    )
            }
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "**스터디 공고 삭제 성공**",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "SUCCESS",
                                                        "message": "스터디 공고를 성공적으로 삭제하였습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "**다른 사용자의 스터디 공고를 삭제하려는 경우**",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "CANNOT_DELETE_ANOTHER_USER_STUDY",
                                                        "message": "다른 사용자의 스터디 공고는 삭제할 수 없습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "**해당 스터디 공고를 찾을 수 없는 경우**",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "STUDY_NOT_FOUND",
                                                        "message": "스터디 공고를 찾을 수 없습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
    })
    ResponseEntity<APIResponse<Void>> deleteStudy(
            @PathVariable Long studyId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );
}
