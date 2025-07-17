package kr.hhplus.be.server.controller.spec;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.controller.dto.PopularProductResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "인기 상품", description = "인기 상품 관련 API")
public interface PopularProductApiSpec {

    @Operation(summary = "인기 상품 조회")
    ResponseEntity<List<PopularProductResponse.GetPopularProduct>> getRecentPopularProducts();

    @Operation(summary = "인기 상품 등록")
    ResponseEntity<Void> createPopularProduct();

}