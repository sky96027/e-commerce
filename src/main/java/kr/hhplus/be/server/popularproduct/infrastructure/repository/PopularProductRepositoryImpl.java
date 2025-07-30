package kr.hhplus.be.server.popularproduct.infrastructure.repository;

import kr.hhplus.be.server.popularproduct.domain.model.PopularProduct;
import kr.hhplus.be.server.popularproduct.domain.repository.PopularProductRepository;
import kr.hhplus.be.server.popularproduct.infrastructure.mapper.PopularProductMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * In-memory 구현체
 */
@Repository
public class PopularProductRepositoryImpl implements PopularProductRepository {

    private final PopularProductJpaRepository jpaRepository;
    private final PopularProductMapper mapper;

    public PopularProductRepositoryImpl(PopularProductJpaRepository jpaRepository,
                                        PopularProductMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<PopularProduct> findAllSummaries() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void replaceAll() {
        jpaRepository.deleteAll(); // 전체 삭제
    }
}
