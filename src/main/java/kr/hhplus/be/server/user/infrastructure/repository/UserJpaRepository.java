package kr.hhplus.be.server.user.infrastructure.repository;

import kr.hhplus.be.server.user.infrastructure.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {

}