package kr.hhplus.be.server.product.domain.type;

/**
 * 상품 옵션의 판매 유형을 나타내는 enum
 */
public enum ProductOptionStatus {
    ON_SALE,         // 판매 중
    OUT_OF_STOCK,    // 품절
    STOPPED,         // 판매 중지
    DISCONTINUED,    // 단종
    EXPIRED          // 판매 기간 만료
}

