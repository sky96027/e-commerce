package kr.hhplus.be.server.controller;

import kr.hhplus.be.server.controller.dto.ProductResponse;
import kr.hhplus.be.server.spec.ProductApiSpec;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/product")
public class ProductController implements ProductApiSpec {

    @GetMapping
    @Override
    public ResponseEntity<List<ProductResponse.GetProductSummary>> getProductSummaries() {
        List<ProductResponse.GetProductSummary> response = List.of(
                new ProductResponse.GetProductSummary(
                        101L,
                        "커피",
                        "FOR-SALE",
                        5000L,
                        LocalDateTime.parse("2025-07-17T21:00"),
                        LocalDateTime.parse("2027-07-17T21:00")

                ),
                new ProductResponse.GetProductSummary(
                        102L,
                        "칫솔",
                        "NOT-SALE",
                        3000L,
                        LocalDateTime.parse("2025-06-17T21:00"),
                        LocalDateTime.parse("2025-06-30T21:00")

                ),
                new ProductResponse.GetProductSummary(
                        103L,
                        "양말",
                        "FOR-SALE",
                        2000L,
                        LocalDateTime.parse("2025-06-17T21:00"),
                        LocalDateTime.parse("2025-06-30T21:00")

                )
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/product/{id}")
    @Override
    public ResponseEntity<ProductResponse.GetProductDetail> getProductDetail(@PathVariable("id") Long productId) {
        List<ProductResponse.GetProductOption> options = List.of(
                new ProductResponse.GetProductOption(
                        1001L,
                        productId,
                        "100g",
                        "AVAILABLE",
                        5000L,
                        20,
                        LocalDateTime.parse("2025-07-17T21:00"),
                        null
                ),
                new ProductResponse.GetProductOption(
                        1002L,
                        productId,
                        "200g",
                        "SOLD_OUT",
                        9000L,
                        0,
                        LocalDateTime.parse("2025-07-17T21:00"),
                        LocalDateTime.parse("2026-01-01T00:00")
                )
        );

        ProductResponse.GetProductDetail response = new ProductResponse.GetProductDetail(
                productId,
                "커피",
                "FOR-SALE",
                LocalDateTime.parse("2025-07-17T21:00"),
                LocalDateTime.parse("2027-07-17T21:00"),
                options
        );
        return ResponseEntity.ok(response);
    }
}