package kr.hhplus.be.server.user.domain.model;

import kr.hhplus.be.server.common.exception.RestApiException;
import kr.hhplus.be.server.user.exception.UserErrorCode;
import lombok.Getter;

/**
 * 유저 도메인 모델
 */
@Getter
public class User {

    private final Long userId;
    private final long balance;

    /**
     * 전체 필드를 초기화하는 생성자
     */
    public User(
            Long userId,
            long balance
    ) {
        this.userId = userId;
        this.balance = balance;
    }
    public static User empty(Long userId) { return new User(userId, 0L); }

    /** 입력 검증: 양수 금액만 허용 */
    public static void requirePositive(long amount) {
        if (amount <= 0) throw new RestApiException(UserErrorCode.INVALID_AMOUNT_ERROR);
    }

    /**
     * (계산용) 충전 후 잔액을 반환하는 순수 함수.
     * 실제 반영은 Repository의 increment UPDATE가 담당.
     */
    public User charge(long amount) {
        requirePositive(amount);
        return new User(this.userId, this.balance + amount);
    }

    /**
     * (계산용) 차감 후 잔액을 반환하는 순수 함수.
     * 실제 반영은 Repository의 decrement UPDATE가 담당.
     */
    public User deduct(long amount) {
        requirePositive(amount);
        if (this.balance < amount) {
            throw new RestApiException(UserErrorCode.NOT_ENOUGH_BALANCE_ERROR);
        }
        return new User(this.userId, this.balance - amount);
    }
}