package kr.hhplus.be.server.popularproduct.domain.repository;

import kr.hhplus.be.server.popularproduct.domain.model.PopularProduct;

import java.util.List;

/**
 * 인기 상품 정보를 조회, 저장하기 위한 도메인 저장소 인터페이스
 * 구현체는 인프라 계층에 위치함
 */
public interface PopularProductRepository {
    List<PopularProduct> findAllSummaries();
    void replaceAll();
}
