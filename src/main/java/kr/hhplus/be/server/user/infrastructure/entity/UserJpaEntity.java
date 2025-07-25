package kr.hhplus.be.server.user.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Getter;

/**
 * 유저 정보를 나타내는 JPA 엔티티 클래스
 */
@Getter
@Entity
@Table(name = "user")
public class UserJpaEntity {
    public UserJpaEntity() {}

    public UserJpaEntity(long userId, long balance) {
        this.userId = userId;
        this.balance = balance;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private long userId;

    @Column(name = "balance", nullable = false)
    private long balance;
}
