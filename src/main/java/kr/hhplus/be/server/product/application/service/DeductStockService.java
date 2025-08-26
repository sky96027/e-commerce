package kr.hhplus.be.server.product.application.service;

import kr.hhplus.be.server.common.redis.cache.events.StockChangedEvent;
import kr.hhplus.be.server.product.infrastructure.redis.StockCounter;
import kr.hhplus.be.server.product.application.usecase.DeductStockUseCase;
import kr.hhplus.be.server.product.domain.model.ProductOption;
import kr.hhplus.be.server.product.domain.repository.ProductOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * [UseCase 구현체]
 * DeductStockUseCase 인터페이스를 구현한 클래스.
 *
 * 도메인 계층의 ProductOptionRepository를 사용하여 재고를 차감시킨다.
 * 재고 차감 후 StockChangedEvent를 발행하여 관련 캐시를 무효화한다.
 *
 * 이 클래스는 오직 "재고 차감"라는 하나의 유스케이스만 책임지며,
 * 단일 책임 원칙(SRP)을 따르는 구조로 확장성과 테스트 용이성을 높인다.
 */
@Service
@RequiredArgsConstructor
public class DeductStockService implements DeductStockUseCase {
    private final ProductOptionRepository repository;
    private final ApplicationEventPublisher eventPublisher;
    private final StockCounter stockCounter;

    /**
     * 주어진 옵션 ID를 기반으로 옵션 정보를 조회하고, 차감한다.
     * 재고 차감 후 StockChangedEvent를 발행하여 관련 캐시를 무효화한다.
     * 
     * @param optionId 차감할 옵션 ID
     * @param quantity 판매량
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deductStock(long optionId, int quantity) {
        ProductOption.requirePositive(quantity);
        
        // 상품 ID 조회
        ProductOption productOption = repository.findOptionByOptionId(optionId);
        long productId = productOption.getProductId();
        
        // Redis에서 재고 차감 시도 (1차 소스)
        long remainingStock = stockCounter.tryDeductHash(productId, optionId, quantity);
        boolean redisDecremented = (remainingStock != -1);


        try {
            if (!redisDecremented) {
                // Redis 재고 부족 시 DB에서 재확인 및 차감
                repository.decrementStock(optionId, quantity);
                // DB 성공 시 Redis 재고 동기화
                long fresh = repository.findOptionByOptionId(optionId).getStock();
                stockCounter.initStockHash(productId, optionId, fresh);
            } else {
                // Redis 차감 성공 -> DB도 Write-Through
                repository.decrementStock(optionId, quantity);
            }
        } catch (RuntimeException e) {
            // 보상 코드
            if (redisDecremented) {
                stockCounter.compensateHash(productId, optionId, quantity);
            }
            throw e;
        }
        
        // 재고 변경 이벤트 발행
        eventPublisher.publishEvent(new StockChangedEvent(
            productId, 
            optionId, 
            StockChangedEvent.DEDUCT, 
            quantity
        ));
    }
}
