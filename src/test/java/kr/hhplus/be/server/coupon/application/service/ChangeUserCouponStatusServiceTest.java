package kr.hhplus.be.server.coupon.application.service;

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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ChangeUserCouponStatusServiceTest {
    @Mock
    private UserCouponRepository userCouponRepository;
    @InjectMocks
    private ChangeUserCouponStatusService changeUserCouponStatusService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        changeUserCouponStatusService = new ChangeUserCouponStatusService(userCouponRepository);
    }

    @Test
    @DisplayName("유저 쿠폰 상태 정상 변경")
    void changeStatus_success() {
        // given
        long userCouponId = 1L;
        UserCouponStatus newStatus = UserCouponStatus.USED;
        UserCoupon original = new UserCoupon(
                userCouponId, 2L, 3L, 4L, UserCouponStatus.ISSUED,
                CouponPolicyType.FIXED, 10.0f, 1000L, 5000L, 30, LocalDateTime.now().plusDays(30)
        );
        UserCoupon updated = original.changeStatus(newStatus);

        when(userCouponRepository.selectByUserCouponId(userCouponId)).thenReturn(Optional.of(original));
        doNothing().when(userCouponRepository).insertOrUpdate(any(UserCoupon.class));

        // when
        UserCoupon result = changeUserCouponStatusService.changeStatus(userCouponId, newStatus);

        // then
        assertThat(result.getStatus()).isEqualTo(newStatus);
        verify(userCouponRepository, times(1)).selectByUserCouponId(userCouponId);
        verify(userCouponRepository, times(1)).insertOrUpdate(any(UserCoupon.class));
    }

    @Test
    @DisplayName("존재하지 않는 쿠폰 ID로 상태 변경 시 예외 발생")
    void changeStatus_notFound_throwsException() {
        // given
        long userCouponId = 1L;
        UserCouponStatus newStatus = UserCouponStatus.USED;
        when(userCouponRepository.selectByUserCouponId(userCouponId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> changeUserCouponStatusService.changeStatus(userCouponId, newStatus))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("해당 ID의 쿠폰이 존재하지 않습니다");
        verify(userCouponRepository, times(1)).selectByUserCouponId(userCouponId);
        verify(userCouponRepository, never()).insertOrUpdate(any(UserCoupon.class));
    }
} 