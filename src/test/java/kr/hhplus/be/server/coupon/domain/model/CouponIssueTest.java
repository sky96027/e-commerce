package kr.hhplus.be.server.coupon.domain.model;

import kr.hhplus.be.server.coupon.domain.type.CouponIssueStatus;
import kr.hhplus.be.server.coupon.domain.type.CouponPolicyType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CouponIssueTest {
    @Test
    @DisplayName("쿠폰 잔량 정상 감소")
    void decreaseRemaining_success() {
        // given
        CouponIssue issue = new CouponIssue(
                1L, 2L, 100, 5, LocalDateTime.now(), CouponIssueStatus.ISSUABLE,
                10.0f, 1000L, 5000L, 30, CouponPolicyType.FIXED
        );
        // when
        CouponIssue updated = issue.decreaseRemaining();
        // then
        assertThat(updated.getRemaining()).isEqualTo(4);
    }

    @Test
    @DisplayName("쿠폰 잔량이 0일 때 예외 발생")
    void decreaseRemaining_noStock_throwsException() {
        // given
        CouponIssue issue = new CouponIssue(
                1L, 2L, 100, 0, LocalDateTime.now(), CouponIssueStatus.ISSUABLE,
                10.0f, 1000L, 5000L, 30, CouponPolicyType.FIXED
        );
        // when & then
        assertThatThrownBy(issue::decreaseRemaining)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("쿠폰 잔량이 소진되었습니다");
    }
} 