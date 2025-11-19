package com.wagglex2.waggle.domain.team_member.controller.docs;

import com.wagglex2.waggle.common.response.APIResponse;
import com.wagglex2.waggle.common.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;

public interface TeamMemberControllerDocs {

    @Operation(
            summary = "팀 멤버 삭제",
            description = "팀 리더가 특정 멤버를 팀에서 삭제한다.",
            parameters = {
                    @Parameter(
                            name = "teamId",
                            description = "팀 ID",
                            in = ParameterIn.PATH,
                            example = "1"
                    ),
                    @Parameter(
                            name = "memberId",
                            description = "삭제할 멤버 ID",
                            in = ParameterIn.PATH,
                            example = "10"
                    )
            },
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "팀 멤버 삭제 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "SUCCESS",
                                                        "message": "팀 멤버 삭제에 성공했습니다."
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
                    description = "자기 자신 삭제 불가능, 리더만 삭제 가능",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "자기 자신 삭제 불가능",
                                            value = """
                                                    {
                                                        "code": "CANNOT_REMOVE_SELF",
                                                        "message": "자기 자신은 삭제할 수 없습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "리더만 삭제 가능",
                                            value = """
                                                    {
                                                        "code": "CANNOT_REMOVE_NOT_LEADER",
                                                        "message": "리더만 멤버를 삭제할 수 있습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "팀 / 리더 / 삭제할 멤버 / 공고 / 역할을 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "팀을 찾을 수 없음",
                                            value = """
                                                    {
                                                        "code": "TEAM_NOT_FOUND",
                                                        "message": "팀을 찾을 수 없습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "리더를 찾을 수 없음",
                                            value = """
                                                    {
                                                        "code": "LEADER_NOT_FOUND",
                                                        "message": "리더를 찾을 수 없습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "삭제할 멤버를 찾을 수 없음",
                                            value = """
                                                    {
                                                        "code": "TARGET_MEMBER_NOT_FOUND",
                                                        "message": "삭제할 멤버를 찾을 수 없습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "공고를 찾을 수 없음",
                                            value = """
                                                    {
                                                        "code": "RECRUITMENT_NOT_FOUND",
                                                        "message": "공고를 찾을 수 없습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "역할을 찾을 수 없음",
                                            value = """
                                                    {
                                                        "code": "POSITION_NOT_FOUND",
                                                        "message": "역할을 찾을 수 없습니다."
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
    ResponseEntity<APIResponse<Void>> deleteMember(
            @PathVariable Long teamId,
            @PathVariable Long memberId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );
}
