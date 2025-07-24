package kr.hhplus.be.server.popularproduct.application.service;

import kr.hhplus.be.server.popularproduct.application.usecase.SaveListUseCase;
import kr.hhplus.be.server.popularproduct.domain.repository.PopularProductRepository;
import org.springframework.stereotype.Service;

/**
 * [UseCase 구현체]
 * SaveListUseCase 인터페이스를 구현한 클래스.
 *
 * 도메인 계층의 PopularProductRepository 사용하여 인기 상품 목록을 삭제/저장한다.
 *
 * 이 클래스는 오직 "인기 상품 목록 삭제/저장"라는 하나의 유스케이스만 책임지며,
 * 단일 책임 원칙(SRP)을 따르는 구조로 확장성과 테스트 용이성을 높인다.
 */
@Service
public class SaveListService implements SaveListUseCase {
    private final PopularProductRepository repository;

    public SaveListService(PopularProductRepository repository) {
        this.repository = repository;
    }

    // 현재 미구현
    @Override
    public void replaceAll() {
        repository.replaceAll();
    }
}
