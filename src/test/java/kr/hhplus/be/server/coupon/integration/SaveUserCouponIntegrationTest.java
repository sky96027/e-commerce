package kr.hhplus.be.server.coupon.integration;

import kr.hhplus.be.server.IntegrationTestBase;
import kr.hhplus.be.server.coupon.application.dto.SaveUserCouponCommand;
import kr.hhplus.be.server.coupon.application.service.SaveUserCouponService;
import kr.hhplus.be.server.coupon.domain.model.CouponIssue;
import kr.hhplus.be.server.coupon.domain.model.UserCoupon;
import kr.hhplus.be.server.coupon.domain.repository.CouponIssueRepository;
import kr.hhplus.be.server.coupon.domain.repository.UserCouponRepository;
import kr.hhplus.be.server.coupon.domain.type.CouponIssueStatus;
import kr.hhplus.be.server.coupon.domain.type.CouponPolicyType;
import kr.hhplus.be.server.coupon.domain.type.UserCouponStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@DisplayName("통합 테스트 - SaveUserCouponService")
class SaveUserCouponIntegrationTest extends IntegrationTestBase {

    @Autowired
    private SaveUserCouponService saveUserCouponService;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private CouponIssueRepository couponIssueRepository;

    private final long policyId = 200L;
    private Long couponIssueId;

    @BeforeEach
    void setUp() {
        CouponIssue couponIssue = new CouponIssue(
                null,
                policyId,
                500,
                450,
                LocalDateTime.now(),
                CouponIssueStatus.ISSUABLE,
                15f,
                10,
                CouponPolicyType.RATE
        );
        CouponIssue savedCouponIssue = couponIssueRepository.save(couponIssue);
        couponIssueId = savedCouponIssue.getCouponIssueId();
    }

    @Test
    @DisplayName("쿠폰을 발급하고 저장한다")
    void saveUserCoupon_success() {
        // given
        long userId = 100L;
        SaveUserCouponCommand command = new SaveUserCouponCommand(
                userId,
                couponIssueId,
                policyId,
                CouponPolicyType.FIXED,
                15f,
                10,
                LocalDateTime.now().plusDays(10)
        );

        // when
        saveUserCouponService.save(command);

        // then
        List<UserCoupon> userCoupons = userCouponRepository.findByUserId(userId);
        assertThat(userCoupons).hasSize(1);

        UserCoupon saved = userCoupons.get(0);
        assertThat(saved.getPolicyId()).isEqualTo(policyId);
        assertThat(saved.getUserId()).isEqualTo(userId);
        assertThat(saved.getStatus()).isEqualTo(UserCouponStatus.ISSUED);
        assertThat(saved.getTypeSnapshot()).isEqualTo(CouponPolicyType.FIXED);

        CouponIssue updatedIssue = couponIssueRepository.findById(couponIssueId);
        assertThat(updatedIssue.getRemaining()).isEqualTo(449); // 1 감소
    }
}