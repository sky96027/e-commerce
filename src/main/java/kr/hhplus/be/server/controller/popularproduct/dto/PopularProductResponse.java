package kr.hhplus.be.server.controller.popularproduct.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PopularProductResponse {

    @Schema(description = "인기 상품 조회 응답")
    public record GetPopularProduct(
            @Schema(description = "인기 상품 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            Long id,

            @Schema(description = "상품 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            Long productId,

            @Schema(description = "총 판매 수량", requiredMode = Schema.RequiredMode.REQUIRED)
            int totalSoldQuantity,

            @Schema(description = "랭킹", requiredMode = Schema.RequiredMode.REQUIRED)
            int rank,

            @Schema(description = "기준 날짜 (ex. 최근 3일)", requiredMode = Schema.RequiredMode.REQUIRED)
            LocalDate referenceDate,

            @Schema(description = "등록일시", requiredMode = Schema.RequiredMode.REQUIRED)
            LocalDateTime createdAt
    ) {}
}