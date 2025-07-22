package kr.hhplus.be.server.popularproduct.presentation.web;

import kr.hhplus.be.server.popularproduct.presentation.dto.PopularProductResponse;
import kr.hhplus.be.server.popularproduct.presentation.contract.PopularProductApiSpec;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/popular-products")
public class PopularProductController implements PopularProductApiSpec {

    @GetMapping
    @Override
    public ResponseEntity<List<PopularProductResponse.GetPopularProduct>> getRecentPopularProducts() {
        List<PopularProductResponse.GetPopularProduct> response = List.of(
                new PopularProductResponse.GetPopularProduct(
                        1L,
                        101L,
                        523,
                        1,
                        LocalDate.parse("2025-07-17"),
                        LocalDateTime.parse("2025-07-17T21:00")
                ),
                new PopularProductResponse.GetPopularProduct(
                        2L,
                        102L,
                        332,
                        2,
                        LocalDate.parse("2025-07-17"),
                        LocalDateTime.parse("2025-07-17T21:00")
                )
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/create")
    @Override
    public ResponseEntity<Void> createPopularProduct() {
        return ResponseEntity.ok().build();
    }
}
