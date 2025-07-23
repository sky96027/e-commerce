package kr.hhplus.be.server.popularproduct.presentation.web;

import kr.hhplus.be.server.popularproduct.application.dto.PopularProductDto;
import kr.hhplus.be.server.popularproduct.application.usecase.FindPopularProductSummaryUseCase;
import kr.hhplus.be.server.popularproduct.application.usecase.SaveListUseCase;
import kr.hhplus.be.server.popularproduct.presentation.contract.PopularProductApiSpec;
import kr.hhplus.be.server.popularproduct.presentation.dto.PopularProductResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/popular-products")
public class PopularProductController implements PopularProductApiSpec {

    private final FindPopularProductSummaryUseCase findUseCase;
    private final SaveListUseCase saveUseCase;

    public PopularProductController(
            FindPopularProductSummaryUseCase findUseCase,
            SaveListUseCase saveUseCase
    ) {
        this.findUseCase = findUseCase;
        this.saveUseCase = saveUseCase;
    }

    @GetMapping
    @Override
    public ResponseEntity<List<PopularProductResponse.GetPopularProduct>> getRecentPopularProducts() {
        List<PopularProductResponse.GetPopularProduct> response = findUseCase.findSummary().stream()
                .map(dto -> new PopularProductResponse.GetPopularProduct(
                        dto.id(),
                        dto.productId(),
                        dto.totalSoldQuantity(),
                        dto.rank(),
                        dto.referenceDate(),
                        dto.createdAt()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/create")
    @Override
    public ResponseEntity<Void> createPopularProduct() {
        saveUseCase.replaceAll();  // 현재 내부 구현은 미구현 상태
        return ResponseEntity.ok().build();
    }
}