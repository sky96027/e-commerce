package kr.hhplus.be.server.product.application.service;

import kr.hhplus.be.server.product.application.usecase.DeductStockUseCase;
import kr.hhplus.be.server.product.domain.model.ProductOption;
import kr.hhplus.be.server.product.domain.repository.ProductOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * [UseCase 구현체]
 * DeductStockUseCase 인터페이스를 구현한 클래스.
 *
 * 도메인 계층의 ProductOptionRepository를 사용하여 재고를 차감시킨다.
 *
 * 이 클래스는 오직 "재고 차감"라는 하나의 유스케이스만 책임지며,
 * 단일 책임 원칙(SRP)을 따르는 구조로 확장성과 테스트 용이성을 높인다.
 */
@Service
@RequiredArgsConstructor
public class DeductStockService implements DeductStockUseCase {
    private final ProductOptionRepository repository;

    /**
     * 주어진 옵션 ID를 기반으로 옵션 정보를 조회하고, 차감한다.
     * @param optionId 차감할 옵션 ID
     * @param quantity 판매량
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deductStock(long optionId, int quantity) {
        ProductOption.requirePositive(quantity);
        boolean ok = repository.decrementStock(optionId, quantity);
        if (!ok) throw new IllegalStateException("재고 부족 또는 옵션 없음: optionId=" + optionId + ", qty=" + quantity);
    }
}
