package kr.hhplus.be.server.coupon.application.service;

import kr.hhplus.be.server.coupon.application.dto.UserCouponDto;
import kr.hhplus.be.server.coupon.domain.model.UserCoupon;
import kr.hhplus.be.server.coupon.domain.repository.UserCouponRepository;
import kr.hhplus.be.server.coupon.domain.type.CouponPolicyType;
import kr.hhplus.be.server.coupon.domain.type.UserCouponStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class FindUserCouponSummaryServiceTest {
    @Mock
    private UserCouponRepository userCouponRepository;
    @InjectMocks
    private FindUserCouponSummaryService findUserCouponSummaryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        findUserCouponSummaryService = new FindUserCouponSummaryService(userCouponRepository);
    }

    @Test
    @DisplayName("유저 쿠폰 목록 정상 반환")
    void findSummary_success() {
        // given
        long userId = 1L;
        UserCoupon userCoupon = new UserCoupon(
                10L, 20L, userId, 30L, UserCouponStatus.ISSUED,
                CouponPolicyType.FIXED, 10.0f, 30, LocalDateTime.now().plusDays(30)
        );
        when(userCouponRepository.findByUserId(userId)).thenReturn(List.of(userCoupon));

        // when
        List<UserCouponDto> result = findUserCouponSummaryService.findSummary(userId);

        // then
        assertThat(result).hasSize(1);
        UserCouponDto dto = result.get(0);
        assertThat(dto.userCouponId()).isEqualTo(10L);
        assertThat(dto.couponId()).isEqualTo(20L);
        assertThat(dto.userId()).isEqualTo(userId);
        assertThat(dto.policyId()).isEqualTo(30L);
        assertThat(dto.status()).isEqualTo(UserCouponStatus.ISSUED);
        assertThat(dto.typeSnapshot()).isEqualTo(CouponPolicyType.FIXED);
        assertThat(dto.discountRateSnapshot()).isEqualTo(10.0f);
        assertThat(dto.usagePeriodSnapshot()).isEqualTo(30);
    }

    @Test
    @DisplayName("유저 쿠폰이 없을 때 빈 목록 반환")
    void findSummary_empty() {
        // given
        long userId = 2L;
        when(userCouponRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

        // when
        List<UserCouponDto> result = findUserCouponSummaryService.findSummary(userId);

        // then
        assertThat(result).isEmpty();
    }
}
