package kr.hhplus.be.server.product.application.service;

import kr.hhplus.be.server.common.cache.events.ProductUpdatedEvent;
import kr.hhplus.be.server.product.domain.model.Product;
import kr.hhplus.be.server.product.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 상품 관리 서비스
 * 상품 생성/수정 시 관련 캐시를 무효화하는 이벤트를 발행
 */
@Service
@RequiredArgsConstructor
public class ProductManagementService {
    
    private final ProductRepository productRepository;
    private final ApplicationEventPublisher eventPublisher;
    
    /**
     * 상품 생성 또는 수정
     * 변경 후 ProductUpdatedEvent를 발행하여 관련 캐시를 무효화
     */
    @Transactional
    public Product saveProduct(Product product) {
        Product savedProduct = productRepository.insertOrUpdate(product);
        
        // 상품 변경 이벤트 발행 (트랜잭션 커밋 후 캐시 무효화)
        eventPublisher.publishEvent(new ProductUpdatedEvent(savedProduct.getProductId()));
        
        return savedProduct;
    }
}
