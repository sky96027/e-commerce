package kr.hhplus.be.server.product.presentation.web;

import kr.hhplus.be.server.product.application.dto.ProductDetailDto;
import kr.hhplus.be.server.product.application.facade.ProductFacade;
import kr.hhplus.be.server.product.presentation.contract.ProductApiSpec;
import kr.hhplus.be.server.product.presentation.dto.ProductResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/product")
public class ProductController implements ProductApiSpec {

    private final ProductFacade productFacade;

    public ProductController(ProductFacade productFacade) {
        this.productFacade = productFacade;
    }

    /**
     * 상품 목록 조회 API
     * 상품 정보와 최저 옵션 가격을 포함한 응답 반환
     */
    @GetMapping
    @Override
    public ResponseEntity<List<ProductResponse.GetProductSummary>> getProductSummaries() {
        List<ProductResponse.GetProductSummary> response = productFacade.getProductSummaries().stream()
                .map(dto -> new ProductResponse.GetProductSummary(
                        dto.productId(),
                        dto.productName(),
                        dto.status(),
                        dto.minPrice(),
                        dto.createdAt(),
                        dto.expiredAt()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * 상품 상세 조회 API
     * 상품 정보와 옵션 목록을 포함한 응답 반환
     */
    @GetMapping("/{id}")
    @Override
    public ResponseEntity<ProductResponse.GetProductDetail> getProductDetail(@PathVariable("id") Long productId) {
        ProductDetailDto dto = productFacade.getProductDetail(productId);

        List<ProductResponse.GetProductOption> options = dto.options().stream()
                .map(opt -> new ProductResponse.GetProductOption(
                        opt.optionId(),
                        opt.productId(),
                        opt.content(),
                        opt.status(),
                        opt.price(),
                        opt.stock(),
                        opt.createdAt(),
                        opt.expiredAt()
                ))
                .collect(Collectors.toList());

        ProductResponse.GetProductDetail response = new ProductResponse.GetProductDetail(
                dto.productId(),
                dto.productName(),
                dto.status(),
                dto.createdAt(),
                dto.expiredAt(),
                options
        );

        return ResponseEntity.ok(response);
    }
}