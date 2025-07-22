package kr.hhplus.be.server.order.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class OrderRequest {

    @Schema(description = "주문 생성 요청")
    public record CreateOrderRequest(
            @Schema(description = "유저 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            Long userId,

            @Schema(description = "주문 상품 목록", requiredMode = Schema.RequiredMode.REQUIRED)
            List<OrderItem> items
    ) {}

    public record OrderItem(
            @Schema(description = "상품 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            Long productId,

            @Schema(description = "상품명", requiredMode = Schema.RequiredMode.REQUIRED)
            String productName,

            @Schema(description = "옵션 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            Long optionId,

            @Schema(description = "상품 가격 스냅샷", requiredMode = Schema.RequiredMode.REQUIRED)
            long productPriceSnapshot,

            @Schema(description = "할인 금액", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
            long discountAmount,

            @Schema(description = "쿠폰 ID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
            Long couponId,

            @Schema(description = "수량", requiredMode = Schema.RequiredMode.REQUIRED)
            int quantity
    ) {}
}