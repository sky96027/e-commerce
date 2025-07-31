package kr.hhplus.be.server.coupon.integration;

import kr.hhplus.be.server.coupon.application.service.ChangeUserCouponStatusService;
import kr.hhplus.be.server.coupon.domain.model.UserCoupon;
import kr.hhplus.be.server.coupon.domain.repository.UserCouponRepository;
import kr.hhplus.be.server.coupon.domain.type.CouponPolicyType;
import kr.hhplus.be.server.coupon.domain.type.UserCouponStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@DisplayName("통합 테스트 - 유저 쿠폰 상태 변경")
public class ChangeUserCouponStatusIntegrationTest {

    @Autowired
    private ChangeUserCouponStatusService changeUserCouponStatusService;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Test
    @DisplayName("유저 쿠폰 상태를 정상적으로 변경한다")
    void changeStatus_success() {
        // given
        UserCoupon original = new UserCoupon(
                null,
                1L,
                1L,
                1L,
                UserCouponStatus.ISSUED,
                CouponPolicyType.RATE,
                10F,
                30,
                LocalDateTime.now().plusDays(30)
        );

        userCouponRepository.insertOrUpdate(original);
        Long savedId = userCouponRepository.findByUserId(1L)
                .stream()
                .filter(c -> c.getPolicyId() == 1L)
                .findFirst()
                .orElseThrow()
                .getUserCouponId();

        // when
        UserCoupon updated = changeUserCouponStatusService.changeStatus(savedId, UserCouponStatus.USED);

        // then
        assertThat(updated).isNotNull();
        assertThat(updated.getUserCouponId()).isEqualTo(savedId);
        assertThat(updated.getStatus()).isEqualTo(UserCouponStatus.USED);

        // DB 반영 확인
        UserCoupon reloaded = userCouponRepository.findByUserCouponId(savedId).orElseThrow();
        assertThat(reloaded.getStatus()).isEqualTo(UserCouponStatus.USED);
    }

    @Test
    @DisplayName("존재하지 않는 쿠폰 ID로 변경 시 예외 발생")
    void changeStatus_invalidId_throwsException() {
        // given
        long invalidId = 9999L;

        // when & then
        assertThatThrownBy(() -> changeUserCouponStatusService.changeStatus(invalidId, UserCouponStatus.USED))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("해당 ID의 쿠폰이 존재하지 않습니다");
    }
}