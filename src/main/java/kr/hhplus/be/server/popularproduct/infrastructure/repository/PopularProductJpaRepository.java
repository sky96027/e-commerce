package kr.hhplus.be.server.popularproduct.infrastructure.repository;

import kr.hhplus.be.server.popularproduct.infrastructure.entity.PopularProductJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PopularProductJpaRepository extends JpaRepository<PopularProductJpaEntity, Long> {
}
