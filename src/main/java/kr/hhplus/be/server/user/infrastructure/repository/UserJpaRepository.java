package kr.hhplus.be.server.user.infrastructure.repository;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.user.domain.model.User;
import kr.hhplus.be.server.user.infrastructure.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {
    // 비관적 락 (legacy)
    /*@Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM UserJpaEntity u WHERE u.userId = :id")
    Optional<UserJpaEntity> findByIdForUpdate(@Param("id") Long id);*/

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE UserJpaEntity u
           SET u.balance = u.balance + :amount
        WHERE u.userId = :id
        """)
    int incrementBalance(@Param("id") Long id, @Param("amount") Long amount);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE UserJpaEntity u 
           SET u.balance = u.balance - :amount
        WHERE u.userId = :id
           AND (u.balance - :amount) >= 0
        """)
    int decrementBalanceIfEnough(@Param("id") Long id, @Param("amount") Long amount);

}