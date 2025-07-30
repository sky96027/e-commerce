package kr.hhplus.be.server.order.infrastructure.repository;

import kr.hhplus.be.server.order.infrastructure.entity.OrderItemJpaEntity;
import kr.hhplus.be.server.transactionhistory.infrastructure.entity.TransactionHistoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface OrderItemJpaRepository extends JpaRepository<OrderItemJpaEntity, Long> {
    List<OrderItemJpaEntity> findAllByOrderId(long orderId);
}
