package kr.hhplus.be.server.product.exception;

import kr.hhplus.be.server.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ProductErrorCode implements ErrorCode {
    OPTION_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "옵션을 찾을 수 없습니다."),
    PRODUCT_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다."),
    INVALID_QUANTITY_ERROR(HttpStatus.BAD_REQUEST, "수량은 0이거나 음수일 수 없습니다."),
    OUT_OF_STOCK_ERROR(HttpStatus.BAD_REQUEST, "재고가 부족합니다.");

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
