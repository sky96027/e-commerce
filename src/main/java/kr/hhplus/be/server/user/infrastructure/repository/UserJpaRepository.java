package kr.hhplus.be.server.user.infrastructure.repository;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.user.domain.model.User;
import kr.hhplus.be.server.user.infrastructure.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM UserJpaEntity u WHERE u.userId = :id")
    Optional<UserJpaEntity> findByIdForUpdate(@Param("id") Long id);
}