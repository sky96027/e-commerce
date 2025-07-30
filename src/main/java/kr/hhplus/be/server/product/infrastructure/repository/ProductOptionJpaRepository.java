package kr.hhplus.be.server.product.infrastructure.repository;

import kr.hhplus.be.server.product.infrastructure.entity.ProductOptionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ProductOptionJpaRepository extends JpaRepository<ProductOptionJpaEntity, Long> {
    List<ProductOptionJpaEntity> findOptionsByProductId(Long productId);
}
