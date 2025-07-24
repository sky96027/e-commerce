package kr.hhplus.be.server.order.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

public class OrderResponse {

    @Schema(description = "주문 생성 응답")
    public record CreateOrderResponse(
            @Schema(description = "주문 ID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
            Long orderId,

            @Schema(description = "유저 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            Long userId,

            @Schema(description = "총 주문 금액", requiredMode = Schema.RequiredMode.REQUIRED)
            long totalAmount,

            @Schema(description = "총 할인 금액", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
            long totalDiscountAmount,

            @Schema(description = "주문 상태", requiredMode = Schema.RequiredMode.REQUIRED)
            String status,

            @Schema(description = "주문일시", requiredMode = Schema.RequiredMode.REQUIRED)
            LocalDateTime orderedAt,

            @Schema(description = "주문 상품 목록", requiredMode = Schema.RequiredMode.REQUIRED)
            List<OrderItem> items

    ) {}

    public record OrderItem(
            @Schema(description = "상품 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            long productId,

            @Schema(description = "상품명", requiredMode = Schema.RequiredMode.REQUIRED)
            String productName,

            @Schema(description = "상품 가격 스냅샷", requiredMode = Schema.RequiredMode.REQUIRED)
            long productPrice,

            @Schema(description = "옵션 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            long optionId,

            @Schema(description = "수량", requiredMode = Schema.RequiredMode.REQUIRED)
            int quantity,

            @Schema(description = "할인 금액", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
            long discountAmount,

            @Schema(description = "쿠폰 ID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
            long userCouponId
    ) {}
}