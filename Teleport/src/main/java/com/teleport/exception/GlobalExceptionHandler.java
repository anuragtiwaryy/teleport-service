package com.teleport.exception;

import com.teleport.dto.GenericResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private String extractRequestBody(HttpServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper wrapper) {
            byte[] content = wrapper.getContentAsByteArray();
            if (content.length > 0) {
                return new String(content, StandardCharsets.UTF_8);
            }
        }
        return "[empty]";
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationErrors(HttpServletRequest request,
                                                         MethodArgumentNotValidException ex) {

        String requestBody = extractRequestBody(request);
        Map<String, String> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        fieldError -> fieldError.getField(),
                        fieldError -> fieldError.getDefaultMessage(),
                        (existing, replacement) -> existing
                ));

        GenericResponseDto<Object> response = GenericResponseDto.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(errors.toString())
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build();
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAllExceptions(Exception ex) {
        HttpStatus status = determineHttpStatus(ex);

        GenericResponseDto<Object> response = GenericResponseDto.builder()
                .status(status)
                .message(ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred")
                .statusCode(status.value())
                .build();

        return new ResponseEntity<>(response, status);
    }

    private HttpStatus determineHttpStatus(Exception ex) {
        if (ex instanceof IllegalArgumentException) {
            return HttpStatus.BAD_REQUEST;
        } else if (ex instanceof MethodArgumentNotValidException) {
            return HttpStatus.BAD_REQUEST;
        } else if (ex instanceof IllegalStateException) {
            return HttpStatus.GATEWAY_TIMEOUT;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

}
