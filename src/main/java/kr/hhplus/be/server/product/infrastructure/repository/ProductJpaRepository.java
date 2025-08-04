package kr.hhplus.be.server.product.infrastructure.repository;

import kr.hhplus.be.server.product.infrastructure.entity.ProductJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductJpaRepository extends JpaRepository<ProductJpaEntity, Long> {
    @Query("SELECT DISTINCT p FROM ProductJpaEntity p JOIN FETCH p.options")
    List<ProductJpaEntity> findAllWithOptions();
}
