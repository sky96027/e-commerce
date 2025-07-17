package kr.hhplus.be.mock;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/mock")
public class MockCouponController {

    private static final Map<Long, List<UserCouponResponse>> userCouponStore = new HashMap<>();
    private static final Map<Long, CouponBatch> couponBatchStore = new HashMap<>();
    private static final AtomicLong userCouponIdSeq = new AtomicLong(1000);

    static {
        // 쿠폰 배치 mock 데이터 등록
        couponBatchStore.put(101L, new CouponBatch(
                101L, 101L, 100, 10,
                LocalDateTime.now().minusDays(3), "ACTIVE",
                0.1f, 1000L, 5000L, 30, "RATE"
        ));
    }

    @GetMapping("/coupon/{userId}")
    public List<UserCouponResponse> getCouponsByUserId(@PathVariable Long userId) {
        return userCouponStore.getOrDefault(userId, new ArrayList<>());
    }

    @PostMapping("/coupon/issue")
    public ApiResponse issueCouponToUser(@RequestParam Long userId, @RequestParam Long couponId) {
        CouponBatch batch = couponBatchStore.get(couponId);
        if (batch == null) {
            return new ApiResponse(false, "INVALID_COUPON", "유효하지 않은 쿠폰입니다.", null);
        }

        if (batch.getIssueStartDate().isAfter(LocalDateTime.now())) {
            return new ApiResponse(false, "NOT_YET_ISSUABLE", "쿠폰 발급 기간이 아닙니다.", null);
        }

        if (batch.getRemaining() <= 0) {
            return new ApiResponse(false, "OUT_OF_STOCK", "쿠폰 발급 수량이 모두 소진되었습니다.", null);
        }

        boolean alreadyIssued = userCouponStore.getOrDefault(userId, new ArrayList<>()).stream()
                .anyMatch(c -> c.getCouponId().equals(couponId));

        if (alreadyIssued) {
            return new ApiResponse(false, "ALREADY_ISSUED", "이미 해당 쿠폰이 발급되어 있습니다.", null);
        }

        Long newUserCouponId = userCouponIdSeq.incrementAndGet();

        UserCouponResponse coupon = new UserCouponResponse(
                newUserCouponId,
                couponId,
                batch.getPolicyId(),
                "ISSUED",
                LocalDateTime.now().plusDays(batch.getExpiredDaysSnapshot())
        );

        userCouponStore.computeIfAbsent(userId, k -> new ArrayList<>()).add(coupon);
        batch.setRemaining(batch.getRemaining() - 1);

        return new ApiResponse(true, null, "쿠폰이 정상적으로 발급되었습니다.", newUserCouponId);
    }

    // ========== DTO 클래스 ==========

    static class UserCouponResponse {
        private Long userCouponId;
        private Long couponId;
        private Long policyId;
        private String status;
        private LocalDateTime expiredAt;

        public UserCouponResponse(Long userCouponId, Long couponId, Long policyId, String status, LocalDateTime expiredAt) {
            this.userCouponId = userCouponId;
            this.couponId = couponId;
            this.policyId = policyId;
            this.status = status;
            this.expiredAt = expiredAt;
        }

        public Long getUserCouponId() { return userCouponId; }
        public Long getCouponId() { return couponId; }
        public Long getPolicyId() { return policyId; }
        public String getStatus() { return status; }
        public LocalDateTime getExpiredAt() { return expiredAt; }

        public void setUserCouponId(Long userCouponId) { this.userCouponId = userCouponId; }
        public void setCouponId(Long couponId) { this.couponId = couponId; }
        public void setPolicyId(Long policyId) { this.policyId = policyId; }
        public void setStatus(String status) { this.status = status; }
        public void setExpiredAt(LocalDateTime expiredAt) { this.expiredAt = expiredAt; }
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
        public int getRemaining() { return remaining; }
        public LocalDateTime getIssueStartDate() { return issueStartDate; }
        public int getExpiredDaysSnapshot() { return expiredDaysSnapshot; }

        public void setRemaining(int remaining) { this.remaining = remaining; }
    }

    static class ApiResponse {
        private boolean success;
        private String code;
        private String message;
        private Long userCouponId;

        public ApiResponse(boolean success, String code, String message, Long userCouponId) {
            this.success = success;
            this.code = code;
            this.message = message;
            this.userCouponId = userCouponId;
        }

        public boolean isSuccess() { return success; }
        public String getCode() { return code; }
        public String getMessage() { return message; }
        public Long getUserCouponId() { return userCouponId; }

        public void setSuccess(boolean success) { this.success = success; }
        public void setCode(String code) { this.code = code; }
        public void setMessage(String message) { this.message = message; }
        public void setUserCouponId(Long userCouponId) { this.userCouponId = userCouponId; }
    }
}