package kr.hhplus.be.server.product.infrastructure.repository;

import kr.hhplus.be.server.product.infrastructure.entity.ProductJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductJpaRepository extends JpaRepository<ProductJpaEntity, Long> {
}
