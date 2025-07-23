package kr.hhplus.be.server.product.domain.model;

import kr.hhplus.be.server.product.domain.type.ProductOptionStatus;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 상품 옵션 도메인 모델
 */
@Getter
public class ProductOption {

    private final long optionId;
    private final long productId;
    private final String content;
    private final ProductOptionStatus status;
    private final long price;
    private final int stock;
    private final LocalDateTime createdAt;
    private final LocalDateTime expiredAt;

    /**
     * 전체 필드를 초기화하는 생성자
     */
    public ProductOption(
            long optionId,
            long productId,
            String content,
            ProductOptionStatus status,
            long price,
            int stock,
            LocalDateTime createdAt,
            LocalDateTime expiredAt
    ) {
        this.optionId = optionId;
        this.productId = productId;
        this.content = content;
        this.status = status;
        this.price = price;
        this.stock = stock;
        this.createdAt = createdAt;
        this.expiredAt = expiredAt;
    }
}