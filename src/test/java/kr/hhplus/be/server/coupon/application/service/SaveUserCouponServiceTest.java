package kr.hhplus.be.server.coupon.application.service;

import kr.hhplus.be.server.coupon.application.dto.SaveUserCouponCommand;
import kr.hhplus.be.server.coupon.domain.model.CouponIssue;
import kr.hhplus.be.server.coupon.domain.model.UserCoupon;
import kr.hhplus.be.server.coupon.domain.repository.CouponIssueRepository;
import kr.hhplus.be.server.coupon.domain.repository.UserCouponRepository;
import kr.hhplus.be.server.coupon.domain.type.CouponPolicyType;
import kr.hhplus.be.server.coupon.domain.type.CouponIssueStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SaveUserCouponServiceTest {
    @Mock
    private UserCouponRepository userCouponRepository;
    @Mock
    private CouponIssueRepository couponIssueRepository;
    @InjectMocks
    private SaveUserCouponService saveUserCouponService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        saveUserCouponService = new SaveUserCouponService(userCouponRepository, couponIssueRepository);
    }

    @Test
    @DisplayName("유저 쿠폰 정상 발급 성공")
    void saveUserCoupon_success() {
        // given
        SaveUserCouponCommand command = new SaveUserCouponCommand(
                1L, 2L, 3L, CouponPolicyType.FIXED, 10.0f, 30, LocalDateTime.now().plusDays(30)
        );
        CouponIssue couponIssue = new CouponIssue(2L, 3L, 100, 10, LocalDateTime.now(), CouponIssueStatus.ISSUABLE, 10.0f, 30, CouponPolicyType.FIXED);

        when(couponIssueRepository.findById(2L)).thenReturn(couponIssue);
        doAnswer(invocation -> {
            CouponIssue arg = invocation.getArgument(0);
            assertThat(arg.getRemaining()).isEqualTo(9);
            return null;
        }).when(couponIssueRepository).save(any(CouponIssue.class));

        // when
        saveUserCouponService.save(command);

        // then
        verify(couponIssueRepository, times(1)).findById(2L);
        verify(couponIssueRepository, times(1)).save(any(CouponIssue.class));
        verify(userCouponRepository, times(1)).insertOrUpdate(any(UserCoupon.class));
    }

    @Test
    @DisplayName("쿠폰 잔량이 0일 때 예외 발생")
    void saveUserCoupon_noRemaining_throwsException() {
        // given
        SaveUserCouponCommand command = new SaveUserCouponCommand(
                1L, 2L, 3L, CouponPolicyType.FIXED, 10.0f, 30, LocalDateTime.now().plusDays(30)
        );
        CouponIssue couponIssue = new CouponIssue(2L, 3L, 100, 0, LocalDateTime.now(), CouponIssueStatus.ISSUABLE, 10.0f, 30, CouponPolicyType.FIXED);
        when(couponIssueRepository.findById(2L)).thenReturn(couponIssue);

        // when & then
        assertThatThrownBy(() -> saveUserCouponService.save(command))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("쿠폰 잔량이 소진되었습니다");
        verify(couponIssueRepository, times(1)).findById(2L);
        verify(couponIssueRepository, never()).save(any(CouponIssue.class));
        verify(userCouponRepository, never()).insertOrUpdate(any(UserCoupon.class));
    }
} 