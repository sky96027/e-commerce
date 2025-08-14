package kr.hhplus.be.server.product.domain.model;

import kr.hhplus.be.server.product.domain.type.ProductOptionStatus;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 상품 옵션 도메인 모델
 */
@Getter
public class ProductOption {

    private final Long optionId;
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
            Long optionId,
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

    /** 양수 수량만 허용 (입력 검증) */
    public static void requirePositive(int qty) {
        if (qty <= 0) throw new IllegalArgumentException("수량은 양수여야 합니다.");
    }

    /** (계산/시뮬레이션용) 차감 후 스냅샷 – 영속 반영 아님 */
    public ProductOption deduct(int amount) {
        requirePositive(amount);
        if (this.stock < amount) throw new IllegalStateException("재고가 부족합니다.");
        return new ProductOption(optionId, productId, content, status, price,
                this.stock - amount, createdAt, expiredAt);
    }

    /** (계산/시뮬레이션용) 증가 후 스냅샷 – 영속 반영 아님 */
    public ProductOption add(int amount) {
        requirePositive(amount);
        return new ProductOption(optionId, productId, content, status, price,
                this.stock + amount, createdAt, expiredAt);
    }

    /*public ProductOption deduct(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("차감량은 음수일 수 없습니다.");
        }
        if (this.stock < amount) {
            throw new IllegalStateException("재고가 부족합니다.");
        }
        return new ProductOption(
                this.optionId,
                this.productId,
                this.content,
                this.status,
                this.price,
                this.stock - amount,
                this.createdAt,
                this.expiredAt);
    }

    public ProductOption add(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("증가량은 음수일 수 없습니다.");
        }
        return new ProductOption(
                this.optionId,
                this.productId,
                this.content,
                this.status,
                this.price,
                this.stock + amount,
                this.createdAt,
                this.expiredAt);
    }*/
}