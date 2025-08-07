package kr.hhplus.be.server.product.infrastructure.repository;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.product.infrastructure.entity.ProductOptionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductOptionJpaRepository extends JpaRepository<ProductOptionJpaEntity, Long> {
    List<ProductOptionJpaEntity> findOptionsByProductId(Long productId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM ProductOptionJpaEntity o WHERE o.optionId = :id")
    Optional<ProductOptionJpaEntity> findByIdForUpdate(@Param("id")Long id);
}
