package kr.hhplus.be.server.coupon.exception;

import kr.hhplus.be.server.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CouponErrorCode implements ErrorCode {
    COUPON_ISSUE_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "해당 쿠폰 발급 정보가 존재하지 않습니다."),
    COUPON_POLICY_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "해당 쿠폰 정책이 존재하지 않습니다."),
    USER_COUPON_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "쿠폰을 찾을 수 없습니다."),
    COUPON_REMAINING_EMPTY_ERROR(HttpStatus.BAD_REQUEST, "쿠폰 잔량이 소진되었습니다.");

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
