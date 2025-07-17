package kr.hhplus.be.mock;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/mock")
public class MockOrderController {

    private static final AtomicLong orderIdSeq = new AtomicLong(1);

    // Mock 데이터 저장소
    private static final Map<Long, ProductOption> productOptionStore = new HashMap<>();
    private static final Map<Long, CouponBatch> couponStore = new HashMap<>();
    private static final Map<Long, List<UserCoupon>> userCouponStore = new HashMap<>();

    static {
        // 상품 옵션 등록
        productOptionStore.put(1001L, new ProductOption(1001L, 101L, 5000L, 10));  // 충분한 재고
        productOptionStore.put(1002L, new ProductOption(1002L, 101L, 9000L, 1));   // 1개 재고

        // 쿠폰 등록 (발급된 것으로 가정)
        couponStore.put(101L, new CouponBatch(101L, 101L, 100, 10,
                LocalDateTime.now().minusDays(1), "ACTIVE", 0.0f, 5000L, 0L, 30, "FIXED"));
        userCouponStore.put(1L, new ArrayList<>(List.of(
                new UserCoupon(1001L, 101L, 101L, "ISSUED", LocalDateTime.now().plusDays(10))
        )));
    }

    @PostMapping("/order")
    public OrderResponse createOrder(@RequestBody OrderRequest request) {
        long totalAmount = 0L;
        long totalDiscount = 0L;
        boolean couponUsed = false;

        for (OrderItemRequest item : request.getItems()) {
            ProductOption option = productOptionStore.get(item.getOptionId());
            if (option == null || option.getStock() < item.getQuantity()) {
                return new OrderResponse(null, 0, 0, "FAILED", "상품 재고가 부족합니다.");
            }

            long itemPrice = option.getPrice() * item.getQuantity();
            totalAmount += itemPrice;

            if (item.getCouponId() != null) {
                // 중복 쿠폰 적용 방지
                if (couponUsed) {
                    return new OrderResponse(null, 0, 0, "FAILED", "쿠폰은 한 상품에만 적용 가능합니다.");
                }
                List<UserCoupon> userCoupons = userCouponStore.getOrDefault(request.getUserId(), new ArrayList<>());
                boolean hasCoupon = userCoupons.stream()
                        .anyMatch(c -> c.getCouponId().equals(item.getCouponId()) && c.getStatus().equals("ISSUED")
                                && c.getExpiredAt().isAfter(LocalDateTime.now()));
                if (!hasCoupon) {
                    return new OrderResponse(null, 0, 0, "FAILED", "유효하지 않은 쿠폰입니다.");
                }
                CouponBatch coupon = couponStore.get(item.getCouponId());
                totalDiscount += coupon.getDiscountAmountSnapshot();
                couponUsed = true;
            }
        }

        Long orderId = orderIdSeq.getAndIncrement();
        return new OrderResponse(orderId, totalAmount, totalDiscount, "BEFORE_PAYMENT", "주문이 정상적으로 생성되었습니다.");
    }

    // === DTO ===
    static class OrderRequest {
        private Long userId;
        private List<OrderItemRequest> items;

        public Long getUserId() { return userId; }
        public List<OrderItemRequest> getItems() { return items; }
    }

    static class OrderItemRequest {
        private Long productId;
        private Long optionId;
        private Integer quantity;
        private Long couponId;

        public Long getProductId() { return productId; }
        public Long getOptionId() { return optionId; }
        public Integer getQuantity() { return quantity; }
        public Long getCouponId() { return couponId; }
    }

    static class OrderResponse {
        private Long orderId;
        private long totalAmount;
        private long totalDiscountAmount;
        private String status;
        private String message;

        public OrderResponse(Long orderId, long totalAmount, long totalDiscountAmount, String status, String message) {
            this.orderId = orderId;
            this.totalAmount = totalAmount;
            this.totalDiscountAmount = totalDiscountAmount;
            this.status = status;
            this.message = message;
        }

        public Long getOrderId() { return orderId; }
        public long getTotalAmount() { return totalAmount; }
        public long getTotalDiscountAmount() { return totalDiscountAmount; }
        public String getStatus() { return status; }
        public String getMessage() { return message; }
    }

    // === MOCK ENTITY ===
    static class ProductOption {
        private Long optionId;
        private Long productId;
        private Long price;
        private int stock;

        public ProductOption(Long optionId, Long productId, Long price, int stock) {
            this.optionId = optionId;
            this.productId = productId;
            this.price = price;
            this.stock = stock;
        }

        public Long getOptionId() { return optionId; }
        public Long getProductId() { return productId; }
        public Long getPrice() { return price; }
        public int getStock() { return stock; }
    }

    static class CouponBatch {
        private Long couponId;
        private Long policyId;
        private int totalIssued;
        private int remaining;
        private LocalDateTime issueStartDate;
        private String status;
        private float discountRateSnapshot;
        private Long discountAmountSnapshot;
        private Long minimumOrderAmountSnapshot;
        private int expiredDaysSnapshot;
        private String typeSnapshot;

        public CouponBatch(Long couponId, Long policyId, int totalIssued, int remaining, LocalDateTime issueStartDate,
                           String status, float discountRateSnapshot, Long discountAmountSnapshot,
                           Long minimumOrderAmountSnapshot, int expiredDaysSnapshot, String typeSnapshot) {
            this.couponId = couponId;
            this.policyId = policyId;
            this.totalIssued = totalIssued;
            this.remaining = remaining;
            this.issueStartDate = issueStartDate;
            this.status = status;
            this.discountRateSnapshot = discountRateSnapshot;
            this.discountAmountSnapshot = discountAmountSnapshot;
            this.minimumOrderAmountSnapshot = minimumOrderAmountSnapshot;
            this.expiredDaysSnapshot = expiredDaysSnapshot;
            this.typeSnapshot = typeSnapshot;
        }

        public Long getCouponId() { return couponId; }
        public Long getPolicyId() { return policyId; }
        public Long getDiscountAmountSnapshot() { return discountAmountSnapshot; }
    }

    static class UserCoupon {
        private Long userCouponId;
        private Long couponId;
        private Long policyId;
        private String status;
        private LocalDateTime expiredAt;

        public UserCoupon(Long userCouponId, Long couponId, Long policyId, String status, LocalDateTime expiredAt) {
            this.userCouponId = userCouponId;
            this.couponId = couponId;
            this.policyId = policyId;
            this.status = status;
            this.expiredAt = expiredAt;
        }

        public Long getCouponId() { return couponId; }
        public String getStatus() { return status; }
        public LocalDateTime getExpiredAt() { return expiredAt; }
    }
}