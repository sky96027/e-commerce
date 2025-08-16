package kr.hhplus.be.server.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode{
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "Invalid parameter included"),
    LOCK_ACQUISITION_FAILED_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Lock acquisition failed"),;
    private final HttpStatus httpStatus;
    private final String message;
    @Override
    public HttpStatus statusCode() {
        return this.httpStatus;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
