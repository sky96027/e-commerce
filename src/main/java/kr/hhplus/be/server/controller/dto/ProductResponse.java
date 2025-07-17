package kr.hhplus.be.server.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

public class ProductResponse {

    @Schema(description = "상품 상세 정보 응답")
    public record GetProductDetail(
            @Schema(description = "상품 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            Long productId,

            @Schema(description = "상품명", requiredMode = Schema.RequiredMode.REQUIRED)
            String productName,

            @Schema(description = "판매 상태", requiredMode = Schema.RequiredMode.REQUIRED)
            String status,

            @Schema(description = "등록일", requiredMode = Schema.RequiredMode.REQUIRED)
            LocalDateTime createdAt,

            @Schema(description = "만료일", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
            LocalDateTime expiredAt,

            @Schema(description = "상품 옵션 리스트", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
            List<GetProductOption> options
    ) {}

    @Schema(description = "상품 목록 리스트 응답")
    public record GetProductSummary(
            @Schema(description = "상품 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            Long productId,

            @Schema(description = "상품명", requiredMode = Schema.RequiredMode.REQUIRED)
            String productName,

            @Schema(description = "판매 상태", requiredMode = Schema.RequiredMode.REQUIRED)
            String status,

            @Schema(description = "가격", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
            Long price,

            @Schema(description = "등록일", requiredMode = Schema.RequiredMode.REQUIRED)
            LocalDateTime createdAt,

            @Schema(description = "만료일", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
            LocalDateTime expiredAt
    ) {}

    @Schema(description = "상품 옵션 응답")
    public record GetProductOption(
            @Schema(description = "옵션 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            Long optionId,

            @Schema(description = "상품 ID", requiredMode = Schema.RequiredMode.REQUIRED)
            Long productId,

            @Schema(description = "옵션명", requiredMode = Schema.RequiredMode.REQUIRED)
            String content,

            @Schema(description = "판매 상태", requiredMode = Schema.RequiredMode.REQUIRED)
            String status,

            @Schema(description = "가격", requiredMode = Schema.RequiredMode.REQUIRED)
            Long price,

            @Schema(description = "재고", requiredMode = Schema.RequiredMode.REQUIRED)
            Integer stock,

            @Schema(description = "등록일", requiredMode = Schema.RequiredMode.REQUIRED)
            LocalDateTime createdAt,

            @Schema(description = "만료일", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
            LocalDateTime expiredAt
    ) {}
}