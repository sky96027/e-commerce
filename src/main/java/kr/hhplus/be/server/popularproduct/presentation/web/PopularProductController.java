package kr.hhplus.be.server.popularproduct.presentation.web;

import kr.hhplus.be.server.popularproduct.presentation.contract.PopularProductApiSpec;
import kr.hhplus.be.server.popularproduct.presentation.dto.PopularProductResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
