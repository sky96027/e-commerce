package kr.hhplus.be.server.spec;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.controller.dto.ProductResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Tag(name = "상품", description = "상품 관련 API")
public interface ProductApiSpec {

    @Operation(summary = "상세 조회")
    ResponseEntity<ProductResponse.GetProductDetail> getProductDetail(@PathVariable("id") Long productId);

    @Operation(summary = "목록 조회")
    ResponseEntity<List<ProductResponse.GetProductSummary>> getProductSummaries();
}