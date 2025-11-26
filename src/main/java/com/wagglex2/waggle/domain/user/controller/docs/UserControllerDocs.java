package com.wagglex2.waggle.domain.user.controller.docs;

import com.wagglex2.waggle.common.response.APIResponse;
import com.wagglex2.waggle.common.security.CustomUserDetails;
import com.wagglex2.waggle.domain.auth.dto.request.EmailRequestDto;
import com.wagglex2.waggle.domain.auth.dto.request.UserBasicInfoRequestDto;
import com.wagglex2.waggle.domain.review.dto.response.ReviewResponseDto;
import com.wagglex2.waggle.domain.user.dto.request.*;
import com.wagglex2.waggle.domain.user.dto.response.UserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "User(사용자)", description = "사용자 관련 API")
public interface UserControllerDocs {

    @Operation(
            summary = "아이디 중복 여부 검사",
            description = "아이디가 중복이면 true, 사용 가능이면 false를 반환한다",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "아이디 중복 요청 내용",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UsernameCheckRequestDto.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "username": "test1234"
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
                    description = "중복 검사 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "이미 사용 중인 아이디",
                                            value = """
                                                    {
                                                        "code": "SUCCESS",
                                                        "message": "이미 사용 중인 아이디입니다.",
                                                        "data": true
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "사용 가능한 아이디",
                                            value = """
                                                    {
                                                        "code": "SUCCESS",
                                                        "message": "사용 가능한 아이디입니다.",
                                                        "data": false
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 아이디 형식",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "잘못된 아이디 예시",
                                            value = """
                                                    {
                                                        "code": "VALIDATION_FAILED",
                                                        "message": "요청 값이 유효하지 않습니다.",
                                                        "data": [
                                                            {
                                                                "field": "username",
                                                                "message": "아이디는 4-20자의 영문, 숫자, 언더스코어만 가능합니다."
                                                            }
                                                        ]
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
    ResponseEntity<APIResponse<Boolean>> existsByUsername(
            @RequestBody @Valid UsernameCheckRequestDto dto
    );


    @Operation(
            summary = "이메일 중복 여부 검사",
            description = "이메일 중복이면 true, 사용 가능이면 false를 반환한다",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "이메일 중복 요청 내용",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EmailCheckRequestDto.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "email": "qwer1234@yu.ac.kr"
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
                    description = "중복 검사 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "이미 사용 중인 이메일",
                                            value = """
                                                    {
                                                        "code": "SUCCESS",
                                                        "message": "이미 사용 중인 이메일입니다.",
                                                        "data": true
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "사용 가능한 이메일",
                                            value = """
                                                    {
                                                        "code": "SUCCESS",
                                                        "message": "사용 가능한 이메일입니다.",
                                                        "data": false
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 이메일 형식/누락",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "잘못된 이메일 예시",
                                            value = """
                                                    {
                                                        "code": "VALIDATION_FAILED",
                                                        "message": "요청 값이 유효하지 않습니다.",
                                                        "data": [
                                                            {
                                                                "field": "email",
                                                                "message": "올바른 이메일 형식이 아닙니다."
                                                            }
                                                        ]
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
    ResponseEntity<APIResponse<Boolean>> existsByEmail(
            @RequestBody @Valid EmailCheckRequestDto dto
    );


    @Operation(
            summary = "닉네임 중복 여부 검사",
            description = "닉네임 중복이면 true, 사용 가능이면 false를 반환한다",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "닉네임 중복 요청 내용",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = NicknameCheckRequestDto.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "nickname": "민민민재"
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
                    description = "중복 검사 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "이미 사용 중인 닉네임",
                                            value = """
                                                    {
                                                        "code": "SUCCESS",
                                                        "message": "이미 사용 중인 닉네임입니다.",
                                                        "data": true
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "사용 가능한 닉네임",
                                            value = """
                                                    {
                                                        "code": "SUCCESS",
                                                        "message": "사용 가능한 닉네임입니다.",
                                                        "data": false
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 닉네임 형식",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "잘못된 닉네임 예시",
                                            value = """
                                                    {
                                                        "code": "VALIDATION_FAILED",
                                                        "message": "요청 값이 유효하지 않습니다.",
                                                        "data": [
                                                            {
                                                                "field": "nickname",
                                                                "message": "닉네임은 2-10자의 영문, 한글, 숫자만 입력할 수 있습니다."
                                                            }
                                                        ]
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
    ResponseEntity<APIResponse<Boolean>> existsByNickname(
            @RequestBody @Valid NicknameCheckRequestDto dto
    );


    @Operation(
            summary = "기본 정보 입력",
            description = "status가 INCOMPLETED인 사용자는 기본 정보 입력을 해야한다.",
            security = @SecurityRequirement(name = "Bearer Token"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "기본 정보 입력 내용",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserBasicInfoRequestDto.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "grade": 3,
                                                        "position": "BACK_END",
                                                        "skills": [
                                                            "JAVA",
                                                            "SPRING_BOOT"
                                                        ],
                                                        "shortIntro": "안녕하세요, 백엔드 개발자 지망생입니다."
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
                    description = "기본 정보 입력 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "SUCCESS",
                                                        "message": "기본 정보 입력에 성공했습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "학년/포지션/기술 스택/한 줄 소개 누락, 학년/기술 스택/한 줄 소개 크기 초과",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "",
                                            value = """
                                                    {
                                                        "code": "VALIDATION_FAILED",
                                                        "message": "요청 값이 유효하지 않습니다.",
                                                        "data": [
                                                            {
                                                                "field": "grade",
                                                                "message": "학년은 4 이하이어야 합니다."
                                                            },
                                                            {
                                                                "field": "skills",
                                                                "message": "기술 스택이 누락되었습니다."
                                                            },
                                                            {
                                                                "field": "shortIntro",
                                                                "message": "한 줄 소개가 누락되었습니다."
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
                    responseCode = "404",
                    description = "해당 유저를 찾을 수 없음",
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
    ResponseEntity<APIResponse<Void>> updateBasicInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid UserBasicInfoRequestDto dto
    );


    @Operation(
            summary = "비밀번호 변경",
            description = "기존 비밀번호, 새 비밀번호, 확인 비밀번호를 검증 후 처리한다.",
            security = @SecurityRequirement(name = "Bearer Token"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "비밀번호 변경 내용",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PasswordRequestDto.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "old": "asdf1234!",
                                                        "newPassword": "qwer1234!",
                                                        "passwordConfirm": "qwer1234!"
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
                    description = "비밀번호 변경 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "SUCCESS",
                                                        "message": "비밀번호 변경에 성공했습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "기존 비밀번호/새 비밀번호 일치, 새 비밀번호/비밀번호 확인 불일치, 기존 비밀번호 불일치, 기존 비밀번호/새 비밀번호/비밀번호 확인 누락 및 형식 불일치",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "기존 비밀번호/새 비밀번호 일치",
                                            value = """
                                                    {
                                                        "code": "PASSWORD_SAME_AS_OLD",
                                                        "message": "기존 비밀번호와 새로운 비밀번호가 일치합니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "새 비밀번호/비밀번호 확인 불일치",
                                            value = """
                                                    {
                                                        "code": "MISMATCHED_PASSWORD",
                                                        "message": "비밀번호와 비밀번호 확인이 일치하지 않습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "기존 비밀번호 불일치",
                                            value = """
                                                    {
                                                        "code": "OLD_PASSWORD_INCORRECT",
                                                        "message": "기존 비밀번호가 일치하지 않습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "새 비밀번호 형식 불일치",
                                            value = """
                                                    {
                                                        "code": "VALIDATION_FAILED",
                                                        "message": "요청 값이 유효하지 않습니다.",
                                                        "data": [
                                                            {
                                                                "field": "newPassword",
                                                                "message": "비밀번호는 8자 이상 72자 이내의 영문, 숫자, 특수문자를 포함해야 합니다."
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
                    responseCode = "404",
                    description = "해당 유저를 찾을 수 없음",
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
    ResponseEntity<APIResponse<Void>> passwordChange(
            @Valid @RequestBody PasswordRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    );


    @Operation(
            summary = "회원정보 조회",
            description = "현재 로그인한 사용자의 정보를 조회한다.",
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "회원정보 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDto.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "SUCCESS",
                                                        "message": "회원정보를 불러오는데 성공했습니다.",
                                                        "data": {
                                                            "username": "test1234",
                                                            "email": "12345678@yu.ac.kr",
                                                            "university": {
                                                                "desc": "영남대",
                                                                "domain": "yu.ac.kr",
                                                                "name": "YOUNGNAM_UNIV"
                                                            },
                                                            "nickname": "민민민재",
                                                            "grade": 3,
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
                                                                    "desc": "Java",
                                                                    "name": "JAVA"
                                                                }
                                                            ],
                                                            "shortIntro": "안녕하세요, 백엔드 개발자 지망생입니다."
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
                    responseCode = "404",
                    description = "해당 유저를 찾을 수 없음",
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
    ResponseEntity<APIResponse<UserResponseDto>> getMe(
            @AuthenticationPrincipal CustomUserDetails userDetails
    );


    @Operation(
            summary = "회원정보 수정",
            description = "현재 로그인한 사용자의 정보를 수정한다.",
            security = @SecurityRequirement(name = "Bearer Token"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "회원정보 수정 내용",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserUpdateRequestDto.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "nickname" : "민민민재"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "nickname" : "민민민재",
                                                        "grade" : 4,
                                                        "position" : "FRONT_END",
                                                        "skills": [
                                                            "JAVA",
                                                            "SPRING_BOOT",
                                                            "CPP"
                                                        ],
                                                        "shortIntro": "안녕하세요!"
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
                    description = "회원정보 수정 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "SUCCESS",
                                                        "message": "회원정보를 불러오는데 성공했습니다.",
                                                        "data": {
                                                            "username": "test1234",
                                                            "email": "12345678@yu.ac.kr",
                                                            "university": {
                                                                "desc": "영남대",
                                                                "domain": "yu.ac.kr",
                                                                "name": "YOUNGNAM_UNIV"
                                                            },
                                                            "nickname": "민민민재",
                                                            "grade": 3,
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
                                                                    "desc": "Java",
                                                                    "name": "JAVA"
                                                                }
                                                            ],
                                                            "shortIntro": "안녕하세요!"
                                                        }
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "학년/포지션/기술 스택/한 줄 소개 누락 및 크기 초과, 닉네임 형식 불일치",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "학년/포지션/기술 스택/한 줄 소개 누락",
                                            value = """
                                                    {
                                                        "code": "VALIDATION_FAILED",
                                                        "message": "요청 값이 유효하지 않습니다.",
                                                        "data": [
                                                            {
                                                                "field": "grade",
                                                                "message": "학년이 누락되었습니다."
                                                            },
                                                            {
                                                                "field": "skills",
                                                                "message": "기술 스택이 누락되었습니다."
                                                            },
                                                            {
                                                                "field": "shortIntro",
                                                                "message": "한 줄 소개가 누락되었습니다."
                                                            },
                                                            {
                                                                "field": "position",
                                                                "message": "포지션이 누락되었습니다."
                                                            }
                                                        ]
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "한 줄 소개 크기 초과, 닉네임 형식 불일치",
                                            value = """
                                                    {
                                                        "code": "VALIDATION_FAILED",
                                                        "message": "요청 값이 유효하지 않습니다.",
                                                        "data": [
                                                            {
                                                                "field": "shortIntro",
                                                                "message": "한 줄 소개는 100자를 초과할 수 없습니다."
                                                            },
                                                            {
                                                                "field": "nickname",
                                                                "message": "닉네임은 2-10자의 영문, 한글, 숫자만 입력할 수 있습니다."
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
                    responseCode = "404",
                    description = "해당 유저를 찾을 수 없음",
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
    ResponseEntity<APIResponse<UserResponseDto>> updateMe(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UserUpdateRequestDto dto
    );


    @Operation(
            summary = "회원탈퇴",
            description = "현재 로그인한 사용자를 탈퇴한다. 본인 확인 인증을 위해 비밀번호로 검증한다.",
            security = @SecurityRequirement(name = "Bearer Token"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "회원탈퇴 시 비밀번호 인증",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = WithdrawRequestDto.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "password" : "zxcv1234!"
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
                    description = "회원탈퇴 성공",
                    headers = {
                            @Header(
                                    name = "Set-Cookie",
                                    description = "refreshToken (HttpOnly, Secure, SameSite=Lax)",
                                    schema = @Schema(type = "string"),
                                    example = "refreshToken=; Path=/; HttpOnly; Secure; SameSite=Lax; Max-Age=0"
                            )
                    },
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "SUCCESS",
                                                        "message": "회원탈퇴에 성공했습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "비밀번호 누락 및 크기 초과, 비밀번호 불일치",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "비밀번호 불일치",
                                            value = """
                                                    {
                                                        "code": "MISMATCHED_PASSWORD",
                                                        "message": "비밀번호와 비밀번호 확인이 일치하지 않습니다."
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
                    description = "해당 유저를 찾을 수 없음",
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
                    responseCode = "409",
                    description = "이미 탈퇴한 회원",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "ALREADY_WITHDRAWN_USER",
                                                        "message": "이미 탈퇴한 회원입니다."
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
    ResponseEntity<APIResponse<Void>> withdraw(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody WithdrawRequestDto dto,
            HttpServletResponse response
    );


    @Operation(
            summary = "특정 사용자 리뷰 조회",
            description = "특정 사용자가 받은 리뷰 목록을 조건에 따라 페이지네이션 방식으로 조회한다.",
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
                    description = "리뷰 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReviewResponseDto.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "SUCCESS",
                                                        "message": "리뷰 조회에 성공했습니다.",
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
                                                                    "content": "리뷰 test9"
                                                                },
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
                    responseCode = "400",
                    description = "잘못된 정렬 기준, 페이지 크기 초과",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "잘못된 정렬 기준",
                                            description = "http://3.35.173.28:8080/api/v1/users/{userId}/reviews/received?sort=create",
                                            value = """
                                                    {
                                                        "code": "INVALID_SORT_PROPERTY",
                                                        "message": "create는 정렬할 수 없는 필드입니다. 허용된 필드: [createdAt]"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "페이지 번호가 최댓값 이상인 경우",
                                            description = "http://3.35.173.28:8080/api/v1/users/{userId}/reviews/received?page=999999",
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
                    responseCode = "404",
                    description = "해당 유저를 찾을 수 없음",
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
    ResponseEntity<APIResponse<Page<ReviewResponseDto>>> getReviews(
            @PathVariable(name = "userId") Long userId,
            @PageableDefault(
                    size = 5,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    );
}
