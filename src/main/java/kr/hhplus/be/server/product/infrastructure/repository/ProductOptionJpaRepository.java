package kr.hhplus.be.server.product.infrastructure.repository;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.product.infrastructure.entity.ProductOptionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductOptionJpaRepository extends JpaRepository<ProductOptionJpaEntity, Long> {
    List<ProductOptionJpaEntity> findOptionsByProductId(Long productId);

    // 비관적 락 (legacy)
    /*@Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM ProductOptionJpaEntity o WHERE o.optionId = :id")
    Optional<ProductOptionJpaEntity> findByIdForUpdate(@Param("id")Long id);*/

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE ProductOptionJpaEntity o
           SET o.stock = o.stock - :quantity
         WHERE o.optionId = :id
           AND o.stock >= :quantity
        """)
    int decrementStockIfEnough(@Param("id") Long id, @Param("quantity") int quantity);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE ProductOptionJpaEntity o
           SET o.stock = o.stock + :quantity
         WHERE o.optionId = :id
        """)
    int incrementStock(@Param("id") Long id, @Param("quantity") int quantity);
}
