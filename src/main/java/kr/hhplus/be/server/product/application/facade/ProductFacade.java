package kr.hhplus.be.server.product.application.facade;

import kr.hhplus.be.server.common.exception.CommonErrorCode;
import kr.hhplus.be.server.common.exception.RestApiException;
import kr.hhplus.be.server.common.redis.lock.RedisDistributedLockManager;
import kr.hhplus.be.server.product.application.dto.ProductDetailDto;
import kr.hhplus.be.server.product.application.dto.ProductDto;
import kr.hhplus.be.server.product.application.dto.ProductOptionDto;
import kr.hhplus.be.server.product.application.dto.ProductSummaryDto;
import kr.hhplus.be.server.product.application.usecase.DeductStockUseCase;
import kr.hhplus.be.server.product.application.usecase.FindDetailUseCase;
import kr.hhplus.be.server.product.application.usecase.FindProductOptionsUseCase;
import kr.hhplus.be.server.product.application.usecase.FindProductSummaryUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * [Facade]
 * 상품 도메인과 옵션 도메인을 조합하여 외부에 통합된 정보를 제공하는 파사드 클래스.
 * 도메인 간 경계를 명확히 하기 위해 다른 도메인은 항상 facade를 거친다.
 */
@Component
@RequiredArgsConstructor
public class ProductFacade {

    private final FindProductSummaryUseCase findProductSummaryUseCase;
    private final FindDetailUseCase findDetailUseCase;
    private final FindProductOptionsUseCase findProductOptionsUseCase;
    private final RedisDistributedLockManager lockManager;
    private final DeductStockUseCase deductStockUseCase;

    /**
     * 전체 상품 목록을 조회하고, 각 상품의 옵션 중 최저가를 함께 반환한다.
     * 상품과 옵션은 각각의 유스케이스를 통해 도메인 경계를 유지한 채 조회된다.
     *
     * @return 상품 요약 정보 리스트 (상품명, 최저 옵션 가격 등 포함)
     */
    public List<ProductSummaryDto> getProductSummaries() {
        return findProductSummaryUseCase.findSummary().stream()
                .map(product -> {
                    List<ProductOptionDto> options = findProductOptionsUseCase.findByProductId(product.productId());
                    long minPrice = options.stream()
                            .mapToLong(ProductOptionDto::price)
                            .min()
                            .orElse(0L);

                    return ProductSummaryDto.from(product, minPrice);
                })
                .collect(Collectors.toList());
    }

    /**
     * 단일 상품의 상세 정보를 조회하고, 해당 상품의 옵션 목록도 함께 반환한다.
     * 두 도메인의 유스케이스를 조합하여 상세 응답을 구성한다.
     *
     * @param productId 조회할 상품 ID
     * @return 상품 정보 + 옵션 목록을 포함한 상세 DTO
     */
    public ProductDetailDto getProductDetail(long productId) {
        ProductDto product = findDetailUseCase.findById(productId);
        List<ProductOptionDto> options = findProductOptionsUseCase.findByProductId(productId);

        return ProductDetailDto.from(product, options);
    }

    /**
     * [PUB/SUB LOCK] 상품 옵션 재고 차감
     *
     * - 쿠폰/핫SKU처럼 경합이 높을 수 있으므로 Pub/Sub 기반 잠금 적용
     * - 트랜잭션(@Transactional)은 Service 메서드에서 시작/종료되며,
     *   아래 finally에서 unlock은 Service 커밋이 끝난 뒤 호출됨(동일 스레드 전제)
     */
    public void deductStock(long optionId, int quantity) {
        final String key = "product:stock:option:" + optionId;

        String token = lockManager.lockBlockingPubSub(
                key,
                Duration.ofSeconds(10),  // TTL: p99 크리티컬 섹션 시간 이상 또는 워치독 사용
                Duration.ofSeconds(30)   // 전체 대기 한도
        );
        if (token == null) {
            throw new RestApiException(CommonErrorCode.LOCK_ACQUISITION_FAILED_ERROR);
        }

        try {
            deductStockUseCase.deductStock(optionId, quantity);
        } finally {
            lockManager.unlock(key, token);
        }
    }

    /**
     * [SPIN LOCK] 상품 옵션 재고 차감
     */
    public void deductStockSpinLock(long optionId, int quantity) {
        final String key = "product:stock:option:" + optionId;

        String token = lockManager.lockBlocking(
                key,
                Duration.ofSeconds(3),
                Duration.ofSeconds(5),
                Duration.ofMillis(50)
        );
        if (token == null) {
            throw new RestApiException(CommonErrorCode.LOCK_ACQUISITION_FAILED_ERROR);
        }
        try {
            deductStockUseCase.deductStock(optionId, quantity);
        } finally {
            lockManager.unlock(key, token);
        }
    }
}