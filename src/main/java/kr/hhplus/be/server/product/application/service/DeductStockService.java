package kr.hhplus.be.server.product.application.service;

import kr.hhplus.be.server.product.application.usecase.DeductStockUseCase;
import kr.hhplus.be.server.product.domain.model.ProductOption;
import kr.hhplus.be.server.product.domain.repository.ProductOptionRepository;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * [UseCase 구현체]
 * ChargeUserBalanceUseCase 인터페이스를 구현한 클래스.
 *
 * 도메인 계층의 UserRepository를 사용하여 실제 사용자 데이터를 조회하고,
 * 그 결과를 UserDto로 변환하여 반환한다.
 *
 * 이 클래스는 오직 "잔액 차감"라는 하나의 유스케이스만 책임지며,
 * 단일 책임 원칙(SRP)을 따르는 구조로 확장성과 테스트 용이성을 높인다.
 */
@Service
public class DeductStockService implements DeductStockUseCase {
    private final ProductOptionRepository repository;
    private final ConcurrentHashMap<Long, ReentrantLock> lockMap = new ConcurrentHashMap<>();

    public DeductStockService(ProductOptionRepository repository) { this.repository = repository; }

    /**
     * 주어진 옵션 ID를 기반으로 옵션 정보를 조회하고, 차감한다.
     * @param optionId 차감할 옵션 ID
     * @param quantity 판매량
     */
    @Override
    public void deductStock(long optionId, int quantity) {
        ReentrantLock lock = lockMap.computeIfAbsent(optionId, k -> new ReentrantLock());
        lock.lock();
        try {
            ProductOption option = repository.findByOptionId(optionId);
            ProductOption updated = option.deduct(quantity);
            repository.insertOrUpdate(updated);
        } finally {
            lock.unlock();
        }
    }
}
