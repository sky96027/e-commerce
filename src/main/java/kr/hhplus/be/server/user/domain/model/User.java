package kr.hhplus.be.server.user.domain.model;

import lombok.Getter;

/**
 * 유저 도메인 모델
 */
@Getter
public class User {

    private final long userId;
    private final long balance;

    /**
     * 전체 필드를 초기화하는 생성자
     */
    public User(
            long userId,
            long balance
    ) {
        this.userId = userId;
        this.balance = balance;
    }
    public static User empty(long userId) { return new User(userId, 0L); }

    public User charge(long amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("충전 금액은 음수일 수 없습니다.");
        }
        return new User(this.userId, this.balance + amount);
    }

    public User deduct(long amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("차감 금액은 음수일 수 없습니다.");
        }
        if (this.balance < amount) {
            throw new IllegalStateException("잔액이 부족합니다.");
        }
        return new User(this.userId, this.balance - amount);
    }
}