package kr.hhplus.be.mock;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/mock")
public class MockProductController {

    private static final Map<Long, ProductDetailResponse> mockProduct = new HashMap<>();

    static {
        // 샘플 데이터 등록
        ProductDetailResponse product1 = new ProductDetailResponse(
                101L,
                "프리미엄 커피",
                "AVAILABLE",
                LocalDateTime.now().minusDays(5),
                null
        );

        product1.addOption(new ProductOptionResponse(
                1001L,
                101L,
                "100g",
                "AVAILABLE",
                5000L,
                20,
                LocalDateTime.now().minusDays(5),
                null
        ));

        product1.addOption(new ProductOptionResponse(
                1002L,
                101L,
                "200g",
                "SOLD_OUT",
                9000L,
                0,
                LocalDateTime.now().minusDays(4),
                LocalDateTime.now().plusDays(30)
        ));

        mockProduct.put(101L, product1);
    }





    @GetMapping("/products")
    public List<ProductListResponse> getAllProductSummaries() {
        List<ProductListResponse> responseList = new ArrayList<>();

        for (ProductDetailResponse product : mockProduct.values()) {
            ProductOptionResponse cheapestOption = product.getOptions().stream()
                    .min(Comparator.comparingLong(ProductOptionResponse::getPrice))
                    .orElse(null);

            responseList.add(new ProductListResponse(
                    product.getProductId(),
                    product.getProductName(),
                    product.getStatus(),
                    cheapestOption != null ? cheapestOption.getPrice() : null,
                    cheapestOption != null ? cheapestOption.getContent() : null
            ));
        }

        return responseList;
    }

    @GetMapping("/product/{id}")
    public ProductDetailResponse getProduct(@PathVariable("id") Long productId) {
        ProductDetailResponse product = mockProduct.get(productId);
        if (product == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
        return product;
    }





    // DTO
    static class ProductDetailResponse {
        private Long productId;
        private String productName;
        private String status;
        private LocalDateTime createdAt;
        private LocalDateTime expiredAt;
        private List<ProductOptionResponse> options = new ArrayList<>();

        public ProductDetailResponse(Long productId, String productName, String status,
                       LocalDateTime createdAt, LocalDateTime expiredAt) {
            this.productId = productId;
            this.productName = productName;
            this.status = status;
            this.createdAt = createdAt;
            this.expiredAt = expiredAt;
        }

        public void addOption(ProductOptionResponse option) {
            this.options.add(option);
        }

        public Long getProductId() { return productId; }
        public String getProductName() { return productName; }
        public String getStatus() { return status; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public LocalDateTime getExpiredAt() { return expiredAt; }
        public List<ProductOptionResponse> getOptions() { return options; }

        public void setProductId(Long productId) { this.productId = productId; }
        public void setProductName(String productName) { this.productName = productName; }
        public void setStatus(String status) { this.status = status; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public void setExpiredAt(LocalDateTime expiredAt) { this.expiredAt = expiredAt; }
        public void setOptions(List<ProductOptionResponse> options) { this.options = options; }
    }

    static class ProductOptionResponse {
        private Long optionId;
        private Long productId;
        private String content;
        private String status;
        private Long price;
        private int stock;
        private LocalDateTime createdAt;
        private LocalDateTime expiredAt;

        public ProductOptionResponse(Long optionId, Long productId, String content, String status,
                             Long price, int stock, LocalDateTime createdAt, LocalDateTime expiredAt) {
            this.optionId = optionId;
            this.productId = productId;
            this.content = content;
            this.status = status;
            this.price = price;
            this.stock = stock;
            this.createdAt = createdAt;
            this.expiredAt = expiredAt;
        }

        public Long getOptionId() { return optionId; }
        public Long getProductId() { return productId; }
        public String getContent() { return content; }
        public String getStatus() { return status; }
        public Long getPrice() { return price; }
        public int getStock() { return stock; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public LocalDateTime getExpiredAt() { return expiredAt; }

        public void setOptionId(Long optionId) { this.optionId = optionId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public void setContent(String content) { this.content = content; }
        public void setStatus(String status) { this.status = status; }
        public void setPrice(Long price) { this.price = price; }
        public void setStock(int stock) { this.stock = stock; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public void setExpiredAt(LocalDateTime expiredAt) { this.expiredAt = expiredAt; }
    }

    static class ProductListResponse {
        private Long productId;
        private String productName;
        private String status;
        private Long lowestPrice;
        private String optionName;

        public ProductListResponse(Long productId, String productName, String status, Long lowestPrice, String optionName) {
            this.productId = productId;
            this.productName = productName;
            this.status = status;
            this.lowestPrice = lowestPrice;
            this.optionName = optionName;
        }

        public Long getProductId() { return productId; }
        public String getProductName() { return productName; }
        public String getStatus() { return status; }
        public Long getLowestPrice() { return lowestPrice; }
        public String getOptionName() { return optionName; }

        public void setProductId(Long productId) { this.productId = productId; }
        public void setProductName(String productName) { this.productName = productName; }
        public void setStatus(String status) { this.status = status; }
        public void setLowestPrice(Long lowestPrice) { this.lowestPrice = lowestPrice; }
        public void setOptionName(String optionName) { this.optionName = optionName; }
    }
}