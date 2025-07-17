package kr.hhplus.be.mock;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/mock")
public class MockPopularProductController {

    private static final List<PopularProduct> popularProductStore = new ArrayList<>();

    static {
        LocalDate today = LocalDate.now();

        popularProductStore.add(new PopularProduct(1L, 101L, 120, 1, today.minusDays(1), LocalDateTime.now()));
        popularProductStore.add(new PopularProduct(2L, 102L, 85, 2, today.minusDays(1), LocalDateTime.now()));
        popularProductStore.add(new PopularProduct(3L, 103L, 60, 3, today.minusDays(2), LocalDateTime.now()));
        popularProductStore.add(new PopularProduct(4L, 104L, 30, 4, today.minusDays(4), LocalDateTime.now())); // 제외됨
    }

    @GetMapping("/popular-products")
    public List<PopularProduct> getRecentPopularProducts() {
        LocalDate threeDaysAgo = LocalDate.now().minusDays(3);

        return popularProductStore.stream()
                .filter(p -> !p.getReferenceDate().isBefore(threeDaysAgo))
                .sorted(Comparator.comparingInt(PopularProduct::getRank))
                .collect(Collectors.toList());
    }

    static class PopularProduct {
        private Long id;
        private Long productId;
        private int totalSoldQuantity;
        private int rank;
        private LocalDate referenceDate;
        private LocalDateTime createdAt;

        public PopularProduct(Long id, Long productId, int totalSoldQuantity, int rank,
                              LocalDate referenceDate, LocalDateTime createdAt) {
            this.id = id;
            this.productId = productId;
            this.totalSoldQuantity = totalSoldQuantity;
            this.rank = rank;
            this.referenceDate = referenceDate;
            this.createdAt = createdAt;
        }

        public Long getId() { return id; }
        public Long getProductId() { return productId; }
        public int getTotalSoldQuantity() { return totalSoldQuantity; }
        public int getRank() { return rank; }
        public LocalDate getReferenceDate() { return referenceDate; }
        public LocalDateTime getCreatedAt() { return createdAt; }

        public void setId(Long id) { this.id = id; }
        public void setProductId(Long productId) { this.productId = productId; }
        public void setTotalSoldQuantity(int totalSoldQuantity) { this.totalSoldQuantity = totalSoldQuantity; }
        public void setRank(int rank) { this.rank = rank; }
        public void setReferenceDate(LocalDate referenceDate) { this.referenceDate = referenceDate; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    }
}
