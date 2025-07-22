package kr.hhplus.be.server.user.domain;

import jakarta.persistence.*;
import lombok.Getter;

/**
 * 유저 정보를 나타내는 JPA 엔티티 클래스
 * 상태 변경을 포함함
 */
@Getter
@Entity
public class UserEntity {
    public UserEntity() {}

    public UserEntity(long userId, long balance) {
        this.userId = userId;
        this.balance = balance;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private long userId;

    @Column(name = "balance", nullable = false)
    private long balance;

    public static UserEntity empty(long userId) {
        return new UserEntity(userId, 0L);
    }

    public UserEntity charge(long amount) {
        return new UserEntity(this.userId, this.balance + amount);
    }
}
