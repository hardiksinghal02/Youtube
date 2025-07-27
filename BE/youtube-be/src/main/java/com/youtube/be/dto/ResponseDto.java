package com.youtube.be.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.youtube.be.exception.AuthError;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDto<T> {
    private boolean success;
    private T data;
    private String errorMessage;
    private Integer errorCode;

    public static <T> ResponseDto<T> success(T data) {
        return new ResponseDto<>(true, data, null, null);
    }

    public static <T> ResponseDto<T> failure(AuthError error) {
        return new ResponseDto<>(false, null, error.getErrorMessage(), error.getErrorCode());
    }

    public static <T> ResponseDto<T> failure(String errorMessage) {
        return new ResponseDto<>(false, null, errorMessage, 500);
    }
}
