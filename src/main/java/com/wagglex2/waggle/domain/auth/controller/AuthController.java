package com.wagglex2.waggle.domain.auth.controller;

import com.wagglex2.waggle.common.response.APIResponse;
import com.wagglex2.waggle.common.security.CustomUserDetails;
import com.wagglex2.waggle.common.security.jwt.JwtUtil;
import com.wagglex2.waggle.domain.auth.dto.request.EmailRequestDto;
import com.wagglex2.waggle.domain.auth.dto.request.EmailVerificationRequestDto;
import com.wagglex2.waggle.domain.auth.dto.request.SignInRequestDto;
import com.wagglex2.waggle.domain.auth.dto.request.SignUpRequestDto;
import com.wagglex2.waggle.domain.auth.dto.response.TokenPair;
import com.wagglex2.waggle.domain.auth.service.AuthService;
import com.wagglex2.waggle.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "인증 관련 API (로그인/로그아웃/회원가입/토큰 재발급)")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

    private final JwtUtil jwtUtil;
    private final AuthService authService;
    private final UserService userService;

    @Operation(
            summary = "회원가입 이메일 인증코드 발송",
            description = """
                    입력된 이메일 주소로 6자리 인증번호를 발송한다.
                    
                    처리 순서:
                    1) 랜덤 6자리 인증번호 생성
                    2) Redis에 EMAIL:{email} 형태로 저장 (TTL: 3분)
                    3) 사용자 이메일로 인증코드 전송
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "이메일 전송 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "SUCCESS",
                                                        "message": "이메일 전송에 성공했습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 이메일 형식",
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
                    description = "서버 오류 발생, 이메일 발송 실패, Redis 연결 실패",
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
    @PostMapping("/email/code")
    public ResponseEntity<APIResponse<Void>> sendEmailAuthCode(
            @Valid @RequestBody EmailRequestDto dto
            ) {
        authService.sendAuthCode(dto.email());

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.ok("이메일 전송에 성공했습니다."));
    }


    @Operation(
            summary = "사용자 이메일 인증번호 검증",
            description = """
                    사용자가 입력한 6자리 인증번호를 검증한다.
                    이메일 수정하지 못하도록 이메일도 같이 보낸다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "이메일 인증번호 검증 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "SUCCESS",
                                                        "message": "이메일 인증이 완료되었습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "이메일/인증번호 누락, 패턴 불일치, 인증번호 만료/불일치",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "인증번호 패턴 불일치",
                                            value = """
                                                    {
                                                        "code": "VALIDATION_FAILED",
                                                        "message": "요청 값이 유효하지 않습니다.",
                                                        "data": [
                                                            {
                                                                "field": "inputCode",
                                                                "message": "인증번호는 숫자 6자리여야 합니다."
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
                    description = "서버 오류 발생, Redis 연결 실패",
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
    @PostMapping("/email/verify")
    public ResponseEntity<APIResponse<Void>> verifyAuthCode(
            @Valid @RequestBody EmailVerificationRequestDto dto
    ) {
        authService.verifyCode(dto.email(), dto.inputCode());

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.ok("이메일 인증이 완료되었습니다."));
    }


    @Operation(
            summary = "회원가입 요청"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "회원가입 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "code": "SUCCESS",
                                                        "message": "회원가입에 성공했습니다.",
                                                        "data": 1
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "필수값 누락, 패턴 불일치, 비밀번호/비밀번호 재입력 불일치",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "아이디(username), 닉네임 누락",
                                            value = """
                                                    {
                                                        "code": "VALIDATION_FAILED",
                                                        "message": "요청 값이 유효하지 않습니다.",
                                                        "data": [
                                                            {
                                                                "field": "nickname",
                                                                "message": "닉네임이 누락되었습니다."
                                                            },
                                                            {
                                                                "field": "username",
                                                                "message": "아이디는 4-20자의 영문, 숫자, 언더스코어만 가능합니다."
                                                            },
                                                            {
                                                                "field": "nickname",
                                                                "message": "닉네임은 2-10자의 영문, 한글, 숫자만 입력할 수 있습니다."
                                                            },
                                                            {
                                                                "field": "username",
                                                                "message": "아이디가 누락되었습니다."
                                                            }
                                                        ]
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "아이디/이메일/닉네임 중복",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "아이디 중복",
                                            value = """
                                                    {
                                                        "code": "DUPLICATED_USERNAME",
                                                        "message": "이미 가입된 아이디입니다."
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
    @PostMapping("/sign-up")
    public ResponseEntity<APIResponse<Long>> signUp(
            @Valid @RequestBody SignUpRequestDto dto
    ) {
        Long userId = userService.signUp(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(APIResponse.ok("회원가입에 성공했습니다.", userId));
    }


    @Operation(
            summary = "로그인 요청",
            description = """
                    로그인에 성공하면 Access Token 및 Refresh Token을 발급한다.
                    Access Token은 HTTP 응답 헤더(Authorization)애 추가한다.
                    Refresh Token은 HttpOnly 쿠키로 추가한다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "로그인 성공",
                    headers = {
                            @Header(
                                    name = "Authorization",
                                    description = "Bearer {accessToken}",
                                    schema = @Schema(type = "string"),
                                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                            ),
                            @Header(
                                    name = "Set-Cookie",
                                    description = "refreshToken (HttpOnly, Secure, SameSite=Lax)",
                                    schema = @Schema(type = "string"),
                                    example = "refreshToken=eyJhbGc...; Path=/; HttpOnly; Secure; SameSite=Lax; Max-Age=604800"
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
                                                        "message": "로그인에 성공했습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "아이디/비밀번호 미입력",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "비밀번호 미입력",
                                            value = """
                                                    {
                                                        "code": "VALIDATION_FAILED",
                                                        "message": "요청 값이 유효하지 않습니다.",
                                                        "data": [
                                                            {
                                                                "field": "password",
                                                                "message": "비밀번호를 입력하세요."
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
                    description = "아이디/비밀번호 불일치",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "아이디/비밀번호 불일치",
                                            value = """
                                                    {
                                                        "code": "INVALID_CREDENTIALS",
                                                        "message": "아이디 또는 비밀번호가 올바르지 않습니다."
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
    @PostMapping("/sign-in")
    public ResponseEntity<APIResponse<Void>> signIn(
            @Valid @RequestBody SignInRequestDto dto,
            HttpServletResponse response
    ) {

        // 1. 로그인 처리
        TokenPair tokens = authService.login(dto);

        // 2. Access Token -> 헤더에 추가
        response.setHeader("Authorization", "Bearer " + tokens.accessToken());

        // 3. Refresh Token -> 쿠키에 추가
        addCookie(response,
                tokens.refreshToken(),
                REFRESH_TOKEN_COOKIE_NAME,
                jwtUtil.getRefreshExpMills() / 1000
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.ok("로그인에 성공했습니다."));
    }


    @Operation(
            summary = "로그아웃 요청",
            description = """
                    Redis에서 userId에 해당하는 Refresh Token을 삭제한다.
                    Refresh Token에 빈 값을 넣고 Cookie를 설정한다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "로그아웃 성공",
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
                                                        "message": "로그아웃에 성공했습니다."
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
    @PostMapping("/sign-out")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<APIResponse<Void>> signOut(HttpServletResponse response,
                                                     @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId();

        authService.deleteRefreshToken(userId);

        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, "")
                .maxAge(0)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        log.info("로그아웃 성공 : userId = {}", userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.ok("로그아웃에 성공했습니다."));
    }


    @Operation(
            summary = "토큰 재발급",
            description = """
                    Refresh Token을 사용해 Refresh/Access Token을 재발급한다.
                    쿠키에서 Refresh Token을 추출하고 새로운 Refresh Token을 쿠키로 설정하고
                    새로운 AccessToken을 헤더에 추가한다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "토큰 재발급 성공",
                    headers = {
                            @Header(
                                    name = "Authorization",
                                    description = "Bearer {accessToken}",
                                    schema = @Schema(type = "string"),
                                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                            ),
                            @Header(
                                    name = "Set-Cookie",
                                    description = "refreshToken (HttpOnly, Secure, SameSite=Lax)",
                                    schema = @Schema(type = "string"),
                                    example = "refreshToken=eyJhbGc...; Path=/; HttpOnly; Secure; SameSite=Lax; Max-Age=604800"
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
                                                        "message": "토큰 재발급에 성공했습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Refresh Token 찾을 수 없음, 유효하지 않음, 타입 불일치, 불일치, 만료",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Refresh Token 찾을 수 없음",
                                            value = """
                                                    {
                                                        "code": "REFRESH_TOKEN_NOT_FOUND",
                                                        "message": "리프레시 토큰을 찾을 수 없습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 발생, Redis 연결 실패",
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
    @PostMapping("/refresh")
    public ResponseEntity<APIResponse<Void>> refreshToken(
            @CookieValue(name = REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshToken,
            HttpServletResponse response
    ) {
        // 1. 토큰 재발급
        TokenPair tokens = authService.reissueTokens(refreshToken);

        // 2. Access Token -> 헤더에 추가
        response.setHeader("Authorization", "Bearer " + tokens.accessToken());

        // 2. Refresh Token -> 쿠키 설정
        addCookie(response,
                tokens.refreshToken(),
                REFRESH_TOKEN_COOKIE_NAME,
                jwtUtil.getRefreshExpMills() / 1000
        );

        return ResponseEntity.status(HttpStatus.OK)
                .body(APIResponse.ok("토큰 재발급에 성공했습니다."));
    }

    /**
     * 주어진 토큰을 응답 쿠키에 설정한다.
     *
     * <p>설정 옵션:</p>
     * <ul>
     *   <li><b>HttpOnly</b>: true (JS에서 접근 불가, XSS 방어)</li>
     *   <li><b>Secure</b>: true (HTTPS에서만 전송)</li>
     *   <li><b>SameSite</b>: Lax (기본 CSRF 방어)</li>
     *   <li><b>Path</b>: "/" (애플리케이션 전역에서 사용 가능)</li>
     *   <li><b>Max-Age</b>: 토큰 만료 시간(초)</li>
     * </ul>
     *
     * @param response   HTTP 응답
     * @param token      저장할 토큰 값
     * @param cookieName 쿠키 이름
     * @param maxAge     만료 시간(초)
     */
    private void addCookie(
            HttpServletResponse response,
            String token,
            String cookieName,
            long maxAge
    ) {
        ResponseCookie cookie = ResponseCookie.from(cookieName, token)
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .path("/")
                .maxAge(maxAge)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }
}
