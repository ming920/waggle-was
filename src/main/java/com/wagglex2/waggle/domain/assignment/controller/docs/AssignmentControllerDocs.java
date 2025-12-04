package com.wagglex2.waggle.domain.assignment.controller.docs;

import com.wagglex2.waggle.common.response.APIResponse;
import com.wagglex2.waggle.common.security.CustomUserDetails;
import com.wagglex2.waggle.domain.assignment.dto.request.AssignmentCreationRequestDto;
import com.wagglex2.waggle.domain.assignment.dto.response.AssignmentDetailResponseDto;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

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
}
