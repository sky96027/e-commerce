package kr.hhplus.be.server.order.infrastructure.repository;

import kr.hhplus.be.server.order.infrastructure.entity.OrderJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderJpaRepository extends JpaRepository<OrderJpaEntity, Long> {
}
