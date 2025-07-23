package kr.hhplus.be.server.user.domain.model;

import kr.hhplus.be.server.user.infrastructure.entity.UserJpaEntity;
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
        return new User(this.userId, this.balance + amount);
    }
}