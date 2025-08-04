package kr.hhplus.be.server.order.infrastructure.repository;

import kr.hhplus.be.server.order.infrastructure.entity.OrderJpaEntity;
import kr.hhplus.be.server.product.infrastructure.entity.ProductJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderJpaRepository extends JpaRepository<OrderJpaEntity, Long> {
    @Query("SELECT DISTINCT o FROM OrderJpaEntity o JOIN FETCH o.items")
    List<OrderJpaEntity> findAllWithItems();
}
