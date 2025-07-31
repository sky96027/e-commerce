package kr.hhplus.be.server.coupon.integration;

import jakarta.persistence.EntityManager;
import kr.hhplus.be.server.coupon.application.dto.UserCouponDto;
import kr.hhplus.be.server.coupon.application.service.FindUserCouponSummaryService;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@DisplayName("통합 테스트 - 유저 쿠폰 목록 조회")
class FindUserCouponSummaryIntegrationTest {

    @Autowired
    private FindUserCouponSummaryService findUserCouponSummaryService;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("유저 ID로 쿠폰 목록을 정상 조회한다")
    void findSummary_success() {
        // given
        long userId = 1L;

        UserCoupon coupon1 = new UserCoupon(
                null,
                100L,
                userId,
                10L,
                UserCouponStatus.ISSUED,
                CouponPolicyType.FIXED,
                null,
                5,
                LocalDateTime.now().plusDays(5)
        );

        UserCoupon coupon2 = new UserCoupon(
                null,
                101L,
                userId,
                11L,
                UserCouponStatus.ISSUED,
                CouponPolicyType.RATE,
                20F,
                7,
                LocalDateTime.now().plusDays(7)
        );
        userCouponRepository.insertOrUpdate(coupon1);
        userCouponRepository.insertOrUpdate(coupon2);

        em.flush();
        em.clear();

        // when
        List<UserCouponDto> result = findUserCouponSummaryService.findSummary(userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);

        List<CouponPolicyType> types = result.stream().map(UserCouponDto::typeSnapshot).toList();
        assertThat(types).contains(CouponPolicyType.FIXED, CouponPolicyType.RATE);

        List<UserCouponStatus> statuses = result.stream().map(UserCouponDto::status).toList();
        assertThat(statuses).containsOnly(UserCouponStatus.ISSUED);
    }

    @Test
    @DisplayName("해당 유저가 쿠폰을 보유하지 않은 경우 빈 리스트 반환")
    void findSummary_emptyList() {
        // given
        long userId = 999L;

        // when
        List<UserCouponDto> result = findUserCouponSummaryService.findSummary(userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }
}