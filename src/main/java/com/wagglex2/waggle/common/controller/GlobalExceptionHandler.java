package com.wagglex2.waggle.common.controller;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.wagglex2.waggle.common.error.ErrorCode;
import com.wagglex2.waggle.common.exception.BusinessException;
import com.wagglex2.waggle.common.response.APIResponse;
import com.wagglex2.waggle.common.response.ValidationError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 비즈니스 예외 처리
     * <p>
     * 서비스 또는 도메인 로직에서 발생하는 비즈니스 예외를 처리하여,
     * 클라이언트에게 HTTP 상태 코드와 ErrorCode, 메시지를 전달한다.
     *
     * @param ex {@link BusinessException}
     * @return {@link ResponseEntity} - {@link APIResponse}를 포함한 에러 응답
     * @author 오재민
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<APIResponse<Void>> handleBusinessException(BusinessException ex) {
        HttpStatus httpStatus = ex.getErrorCode().getHttpStatus();
        String code = ex.getErrorCode().getCode();
        String message = ex.getMessage();

        return ResponseEntity.status(httpStatus)
                .body(APIResponse.error(code, message));
    }

    /**
     * Validation 예외 처리
     * <p>
     * 요청 DTO에서 {@code @Valid} 또는 {@code @Validated}를 사용해 검증할 때
     * 발생하는 모든 필드 예외를 처리하여,
     * 클라이언트에게 HTTP 상태 코드와 메시지를 전달한다.
     * </p>
     *
     * @param ex {@link MethodArgumentNotValidException}
     * @return {@link ResponseEntity} - {@link APIResponse}를 포함한 Validation 실패 응답
     * @author 오재민
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponse<List<ValidationError>>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<ValidationError> errorInfo = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ValidationError(error.getField(), error.getDefaultMessage()))
                .toList();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(APIResponse.error(ErrorCode.VALIDATION_FAILED, errorInfo));
    }

    /**
     * MissingServletRequestParameterException 예외 처리
     * <p>
     * 클라이언트 요청에서 필수 {@code @RequestParam} 파라미터가 누락될 경우 발생하는 예외를 처리한다.
     * 클라이언트에게 HTTP 상태 코드와 ErrorCode, 누락된 필드 이름을 전달한다.
     * </p>
     *
     * @param ex {@link MissingServletRequestParameterException} - 누락된 요청 파라미터 정보 포함
     * @return {@link ResponseEntity} - {@link APIResponse}를 포함한 에러 응답
     * @author 오재민
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<APIResponse<String>> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(APIResponse.error(ErrorCode.REQUIRED_FIELD_MISSING, ex.getParameterName()));
    }
      
    /** 컨트롤러에서 전달된 파라미터 타입이 예상과 다른 경우 발생하는 예외를 처리
     *
     * <p>특히 Enum 타입 파라미터에 잘못된 문자열이 전달될 때 발생하는 {@link MethodArgumentTypeMismatchException}를 처리</p>
     *
     * <p>예시:</p>
     * <ul>
     *     <li>Enum 타입 파라미터에 허용되지 않은 문자열이 들어온 경우 (예: "banana" → Position enum)</li>
     *     <li>숫자 타입 파라미터에 문자열이 들어온 경우 (예: "abc" → int)</li>
     * </ul>
     *
     * @param ex {@link MethodArgumentTypeMismatchException}
     * @return {@link ResponseEntity} - {@link APIResponse}를 포함한 타입 불일치 실패 응답
     * @author 오재민
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<APIResponse<String>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String message = String.format(
                "쿼리 파라미터 '%s'의 값 '%s'이(가) 유효하지 않습니다.",
                ex.getName(),
                ex.getValue()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(APIResponse.error(ErrorCode.INVALID_ENUM_VALUE, message));
    }

    /**
     * 파일 크기 초과 예외 처리
     * <p>
     * Spring의 multipart 크기 제한(기본 1MB) 또는 애플리케이션의 파일 크기 제한(5MB)을 초과한 경우 발생하는 예외를 처리한다.
     * 클라이언트에게 HTTP 상태 코드와 ErrorCode를 전달한다.
     * </p>
     *
     * @param ex {@link MaxUploadSizeExceededException}
     * @return {@link ResponseEntity} - {@link APIResponse}를 포함한 에러 응답
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<APIResponse<Void>> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(APIResponse.error(ErrorCode.FILE_SIZE_TOO_LARGE));
    }

    /**
     * NoResourceFoundException 예외 처리
     * <p>
     * 클라이언트가 잘못된 URL 경로로 요청을 보낸 경우 발생하는 예외를 처리한다.
     * <p>
     * 예시:
     * <ul>
     *     <li>잘못된 경로 요청 (예: GET /api/v1/apple)</li>
     * </ul>
     * <p>
     * 처리 결과로 클라이언트에게 HTTP 404 상태 코드와 함께 상세 메시지를 반환한다.
     *
     * @param ex {@link NoResourceFoundException} - 요청한 리소스를 찾을 수 없는 정보 포함
     * @return {@link ResponseEntity} - {@link APIResponse}를 포함한 404 에러 응답
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<APIResponse<String>> handleNoResourceFoundException(NoResourceFoundException ex) {
        String message = String.format(
                "잘못된 URL입니다. (%s %s)",
                ex.getHttpMethod(),
                ex.getResourcePath()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(APIResponse.error("NOT_FOUND", message));
    }

    /**
     * HttpRequestMethodNotSupportedException 예외 처리
     * <p>
     * 클라이언트가 요청한 URL은 존재하지만, 해당 URL에서 요청한 HTTP 메서드가 허용되지 않을 경우 발생하는 예외를 처리한다.
     *
     * <p>
     * 처리 결과로 클라이언트에게 HTTP 405 상태 코드와 함께 ErrorCode 및 요청 정보, 허용된 메서드를 반환한다.
     *
     * @param ex {@link HttpRequestMethodNotSupportedException} - 요청된 메서드 정보와 허용된 메서드 목록 포함
     * @param request {@link HttpServletRequest} - 요청 URL 정보를 얻기 위해 사용
     * @return {@link ResponseEntity} - {@link APIResponse}를 포함한 405 Method Not Allowed 응답
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<APIResponse<Map<String, Object>>> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request
    ) {
        String[] allowed = ex.getSupportedMethods();

        // 순서 보장을 위해 LinkedHashMap 사용
        Map<String, Object> errorDetail = new LinkedHashMap<>();
        errorDetail.put("url", request.getRequestURI());
        errorDetail.put("requested", ex.getMethod());
        errorDetail.put("allowed", allowed != null ? allowed : "허용된 메서드 없음");

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(APIResponse.error(ErrorCode.METHOD_NOT_ALLOWED, errorDetail));
    }

    /**
     * HttpMessageNotReadableException 예외 처리
     * <p>
     * 클라이언트가 보낸 HTTP 메시지 Body(JSON 등)를 읽을 수 없거나,
     * JSON 필드 값이 잘못되어 역직렬화에 실패한 경우 발생하는 예외를 처리한다.
     *
     * <p>
     * 처리 과정에서 다음 정보를 추출하여 클라이언트에 반환한다.
     * <ul>
     *     <li>field: 문제가 된 JSON 필드 이름 (존재하는 경우)</li>
     *     <li>invalidValue: 클라이언트가 요청한 값 (잘못된 값)</li>
     *     <li>expectedType: 해당 필드가 기대하는 타입 (Enum, 숫자, 날짜 등)</li>
     * </ul>
     *
     * <p>
     * 일반적인 JSON 파싱 오류나 구조 오류의 경우에는 별도의 메시지로 안내한다.
     *
     * @param ex {@link HttpMessageNotReadableException} - HTTP Body 파싱 실패 정보 포함
     * @return {@link ResponseEntity} - {@link APIResponse}를 포함한 400 Bad Request 응답
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<APIResponse<Map<String, Object>>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getCause();
        Map<String, Object> errorDetail = new LinkedHashMap<>();

        if (cause instanceof JsonMappingException jme) {

            // 어떤 필드에서 오류 났는지 path 추출
            List<JsonMappingException.Reference> path = jme.getPath();
            if (!path.isEmpty()) {
                String fieldName = path.get(path.size() - 1).getFieldName();
                errorDetail.put("field", fieldName);
            }

            // InvalidFormatException -> 잘못된 값 + 기대 타입까지 추출
            if (cause instanceof InvalidFormatException ife) {

                // 잘못된 값(Enum, 날짜, 숫자 등)
                Object invalidValue = ife.getValue();
                errorDetail.put("invalidValue", invalidValue);

                // 기대 타입(Class)
                Class<?> targetType = ife.getTargetType();
                if (targetType != null) {
                    errorDetail.put("expectedType", targetType.getSimpleName());
                } else {
                    errorDetail.put("expectedType", "알 수 없음");
                }

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(APIResponse.error(ErrorCode.INVALID_JSON_FIELD, errorDetail));
            }

        }

        // 일반적인 파싱 실패 (예: 잘못된 JSON 문법 오류 등)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(APIResponse.error(ErrorCode.UNREADABLE_JSON));
    }

    /**
     * 일반적인 서버 예외 처리
     * <p>
     * 애플리케이션 실행 중 처리되지 않은 모든 예외를 처리한다.
     * <p>
     *
     * <p>
     * 주로 예기치 않은 서버 오류, NullPointerException, RuntimeException 등
     * 처리되지 않은 예외를 포괄적으로 잡기 위해 사용된다.
     *
     * @param ex {@link Exception} - 처리되지 않은 예외
     * @return {@link ResponseEntity} - {@link APIResponse}를 포함한 500 Internal Server Error 응답
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIResponse<Void>> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(APIResponse.error(ErrorCode.INTERNAL_SERVER_ERROR));
    }
}
