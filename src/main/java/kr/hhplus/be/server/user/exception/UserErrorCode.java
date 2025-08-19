package kr.hhplus.be.server.user.exception;

import kr.hhplus.be.server.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {
    INVALID_AMOUNT_ERROR(HttpStatus.BAD_REQUEST, "금액은 양수여야 합니다."),
    NOT_ENOUGH_BALANCE_ERROR(HttpStatus.BAD_REQUEST, "잔액이 부족합니다."),
    INVALID_INITIAL_BALANCE_ERROR(HttpStatus.BAD_REQUEST, "초기 잔액은 음수일 수 없습니다."),
    USER_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다.");

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
