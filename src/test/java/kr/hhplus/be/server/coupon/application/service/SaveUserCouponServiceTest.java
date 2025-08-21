package kr.hhplus.be.server.coupon.application.service;

import kr.hhplus.be.server.common.exception.RestApiException;
import kr.hhplus.be.server.coupon.application.dto.SaveUserCouponCommand;
import kr.hhplus.be.server.coupon.domain.model.CouponIssue;
import kr.hhplus.be.server.coupon.domain.model.UserCoupon;
import kr.hhplus.be.server.coupon.domain.repository.CouponIssueRepository;
import kr.hhplus.be.server.coupon.domain.repository.UserCouponRepository;
import kr.hhplus.be.server.coupon.domain.type.CouponPolicyType;
import kr.hhplus.be.server.coupon.domain.type.CouponIssueStatus;
import kr.hhplus.be.server.coupon.exception.CouponErrorCode;
import kr.hhplus.be.server.coupon.infrastructure.redis.CouponIssueCounter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

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
    @Mock
    private ApplicationEventPublisher publisher;
    @Mock
    private CouponIssueCounter counter;
    @InjectMocks
    private SaveUserCouponService saveUserCouponService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        saveUserCouponService = new SaveUserCouponService(userCouponRepository, couponIssueRepository, publisher, counter);
    }

    @Test
    @DisplayName("유저 쿠폰 정상 발급 성공")
    void saveUserCoupon_success() {
        // given
        SaveUserCouponCommand command = new SaveUserCouponCommand(
                1L, 2L, 3L, CouponPolicyType.FIXED, 10.0f, 30, LocalDateTime.now().plusDays(30)
        );
        CouponIssue couponIssue = new CouponIssue(2L, 3L, 100, 10, LocalDateTime.now(), CouponIssueStatus.ISSUABLE, 10.0f, 30, CouponPolicyType.FIXED);

        when(couponIssueRepository.findRemainingById(2L)).thenReturn(java.util.Optional.of(10));
        when(couponIssueRepository.decrementRemaining(2L)).thenReturn(1);
        when(counter.getRemaining(2L)).thenReturn(-2L, 10L); // 첫 번째 호출은 -2L, 두 번째 호출은 10L
        when(counter.tryDecrement(2L, 1)).thenReturn(9L);

        // when
        saveUserCouponService.save(command);

        // then
        verify(couponIssueRepository, times(1)).findRemainingById(2L);
        verify(couponIssueRepository, times(1)).decrementRemaining(2L);
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
        when(couponIssueRepository.findRemainingById(2L)).thenReturn(java.util.Optional.of(0));
        when(counter.getRemaining(2L)).thenReturn(-2L, 0L); // 첫 번째 호출은 -2L, 두 번째 호출은 0L
        when(counter.tryDecrement(2L, 1)).thenReturn(-1L);

        // when & then
        assertThatThrownBy(() -> saveUserCouponService.save(command))
                .isInstanceOf(RestApiException.class)
                .hasFieldOrPropertyWithValue("errorCode", CouponErrorCode.COUPON_REMAINING_EMPTY_ERROR);
        verify(couponIssueRepository, times(1)).findRemainingById(2L);
        verify(couponIssueRepository, never()).decrementRemaining(2L);
        verify(userCouponRepository, never()).insertOrUpdate(any(UserCoupon.class));
    }
}