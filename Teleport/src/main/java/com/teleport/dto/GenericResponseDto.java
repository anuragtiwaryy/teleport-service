package com.teleport.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@Getter
@Setter
@Builder
@ToString
public class GenericResponseDto<T> implements Serializable {
    private HttpStatus status;
    private T data;
    private String message;
    private Integer statusCode;


    public static <T> GenericResponseDto<T> success(T data, String message) {
        return GenericResponseDto.<T>builder()
                .status(HttpStatus.OK)
                .message(message)
                .statusCode(HttpStatus.OK.value())
                .data(data)
                .build();
    }

    public static <T> GenericResponseDto<T> created(String message) {
        return GenericResponseDto.<T>builder()
                .status(HttpStatus.CREATED)
                .message(message)
                .statusCode(HttpStatus.CREATED.value())
                .build();
    }

    public static <T> GenericResponseDto<T> serviceUnavailable(String message) {
        return GenericResponseDto.<T>builder()
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .message(message)
                .statusCode(HttpStatus.SERVICE_UNAVAILABLE.value())
                .build();
    }

}
