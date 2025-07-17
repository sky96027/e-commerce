package kr.hhplus.be.mock;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/mock")
public class MockPaymentController {

    private static final Map<Long, Order> orderStore = new HashMap<>();
    private static final Map<Long, List<OrderItem>> orderItemStore = new HashMap<>();
    private static final Map<Long, ProductOption> productOptionStore = new HashMap<>();
    private static final Map<Long, List<UserCoupon>> userCouponStore = new HashMap<>();
    private static final Map<Long, Payment> paymentStore = new HashMap<>();
    private static final Map<Long, Long> userBalanceStore = new HashMap<>();
    private static final AtomicLong paymentIdSeq = new AtomicLong(2000);

    static {
        // 초기 주문 등록 (orderId = 101)
        orderStore.put(101L, new Order(101L, 1L, 19000L, 5000L, "PENDING", LocalDateTime.now()));

        // 주문 아이템 2개
        orderItemStore.put(101L, List.of(
                new OrderItem(1L, 101L, 101L, 5000L, 5000L, 101L, 1),
                new OrderItem(2L, 101L, 101L, 9000L, 0L, null, 1)
        ));

        // 상품 옵션 재고
        productOptionStore.put(1001L, new ProductOption(1001L, 101L, 5000L, 10));
        productOptionStore.put(1002L, new ProductOption(1002L, 101L, 9000L, 5));

        // 유저 쿠폰 등록
        userCouponStore.put(1L, new ArrayList<>(List.of(
                new UserCoupon(1001L, 101L, 101L, "ISSUED", LocalDateTime.now().plusDays(1))
        )));

        // 유저 잔액 등록
        userBalanceStore.put(1L, 20000L);
    }

    @PostMapping("/payment")
    public PaymentResponse processPayment(@RequestBody PaymentRequest request) {
        Order order = orderStore.get(request.getOrderId());

        if (order == null || !order.getUserId().equals(request.getUserId())) {
            return new PaymentResponse(false, null, "주문 정보가 유효하지 않습니다.");
        }

        if (!"PENDING".equals(order.getStatus())) {
            return new PaymentResponse(false, null, "이미 결제가 완료된 주문입니다.");
        }

        long userBalance = userBalanceStore.getOrDefault(request.getUserId(), 0L);
        long payAmount = order.getTotalAmount() - order.getTotalDiscountAmount();

        if (userBalance < payAmount) {
            order.setStatus("FAILED");
            return new PaymentResponse(false, null, "잔액이 부족합니다.");
        }

        // 재고 차감 확인
        List<OrderItem> items = orderItemStore.getOrDefault(order.getOrderId(), new ArrayList<>());
        for (OrderItem item : items) {
            ProductOption option = productOptionStore.get(item.getProductId());
            if (option == null || option.getStock() < item.getQuantity()) {
                order.setStatus("FAILED");
                return new PaymentResponse(false, null, "상품 재고가 부족합니다.");
            }
        }

        // 재고 차감
        for (OrderItem item : items) {
            ProductOption option = productOptionStore.get(item.getProductId());
            option.setStock(option.getStock() - item.getQuantity());
        }

        // 잔액 차감
        userBalanceStore.put(request.getUserId(), userBalance - payAmount);

        // 결제 저장
        Long paymentId = paymentIdSeq.incrementAndGet();
        Payment payment = new Payment(paymentId, order.getOrderId(), order.getUserId(),
                order.getTotalAmount(), order.getTotalDiscountAmount(), "SUCCESS");
        paymentStore.put(paymentId, payment);

        // 주문 상태 변경
        order.setStatus("SUCCEEDED");

        // 쿠폰 상태 변경
        for (OrderItem item : items) {
            if (item.getCouponId() != null) {
                userCouponStore.getOrDefault(request.getUserId(), new ArrayList<>())
                        .stream()
                        .filter(c -> c.getCouponId().equals(item.getCouponId()))
                        .forEach(c -> c.setStatus("USED"));
            }
        }

        return new PaymentResponse(true, paymentId, "결제가 성공적으로 완료되었습니다.");
    }

    // ===== DTO & Entity =====

    static class PaymentRequest {
        private Long userId;
        private Long orderId;
        public Long getUserId() { return userId; }
        public Long getOrderId() { return orderId; }
    }

    static class PaymentResponse {
        private boolean success;
        private Long paymentId;
        private String message;

        public PaymentResponse(boolean success, Long paymentId, String message) {
            this.success = success;
            this.paymentId = paymentId;
            this.message = message;
        }

        public boolean isSuccess() { return success; }
        public Long getPaymentId() { return paymentId; }
        public String getMessage() { return message; }
    }

    static class Order {
        private Long orderId;
        private Long userId;
        private long totalAmount;
        private long totalDiscountAmount;
        private String status;
        private LocalDateTime orderedAt;

        public Order(Long orderId, Long userId, long totalAmount, long totalDiscountAmount, String status, LocalDateTime orderedAt) {
            this.orderId = orderId;
            this.userId = userId;
            this.totalAmount = totalAmount;
            this.totalDiscountAmount = totalDiscountAmount;
            this.status = status;
            this.orderedAt = orderedAt;
        }

        public Long getOrderId() { return orderId; }
        public Long getUserId() { return userId; }
        public long getTotalAmount() { return totalAmount; }
        public long getTotalDiscountAmount() { return totalDiscountAmount; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    static class OrderItem {
        private Long orderItemId;
        private Long orderId;
        private Long productId;
        private Long productPriceSnapshot;
        private Long discountAmount;
        private Long couponId;
        private int quantity;

        public OrderItem(Long orderItemId, Long orderId, Long productId,
                         Long productPriceSnapshot, Long discountAmount,
                         Long couponId, int quantity) {
            this.orderItemId = orderItemId;
            this.orderId = orderId;
            this.productId = productId;
            this.productPriceSnapshot = productPriceSnapshot;
            this.discountAmount = discountAmount;
            this.couponId = couponId;
            this.quantity = quantity;
        }

        public Long getProductId() { return productId; }
        public Long getCouponId() { return couponId; }
        public int getQuantity() { return quantity; }
    }

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

        public int getStock() { return stock; }
        public void setStock(int stock) { this.stock = stock; }
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
        public void setStatus(String status) { this.status = status; }
    }

    static class Payment {
        private Long paymentId;
        private Long orderId;
        private Long userId;
        private Long totalAmountSnapshot;
        private Long totalDiscountAmountSnapshot;
        private String status;

        public Payment(Long paymentId, Long orderId, Long userId, Long totalAmountSnapshot, Long totalDiscountAmountSnapshot, String status) {
            this.paymentId = paymentId;
            this.orderId = orderId;
            this.userId = userId;
            this.totalAmountSnapshot = totalAmountSnapshot;
            this.totalDiscountAmountSnapshot = totalDiscountAmountSnapshot;
            this.status = status;
        }
    }
}