package kr.hhplus.be.server.product.application.service;

import kr.hhplus.be.server.common.cache.events.ProductOptionsChangedEvent;
import kr.hhplus.be.server.product.domain.model.ProductOption;
import kr.hhplus.be.server.product.domain.repository.ProductOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 상품 옵션 관리 서비스
 * 상품 옵션 생성/수정 시 관련 캐시를 무효화하는 이벤트를 발행
 */
@Service
@RequiredArgsConstructor
public class ProductOptionManagementService {
    
    private final ProductOptionRepository productOptionRepository;
    private final ApplicationEventPublisher eventPublisher;
    
    /**
     * 상품 옵션 생성 또는 수정
     * 변경 후 ProductOptionsChangedEvent를 발행하여 관련 캐시를 무효화
     */
    @Transactional
    public ProductOption saveProductOption(ProductOption productOption) {
        ProductOption savedOption = productOptionRepository.insertOrUpdate(productOption);
        
        // 상품 옵션 변경 이벤트 발행 (트랜잭션 커밋 후 캐시 무효화)
        eventPublisher.publishEvent(new ProductOptionsChangedEvent(savedOption.getProductId()));
        
        return savedOption;
    }
}
