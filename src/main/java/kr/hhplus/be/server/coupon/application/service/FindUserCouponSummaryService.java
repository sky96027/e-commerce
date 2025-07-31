package kr.hhplus.be.server.coupon.application.service;

import kr.hhplus.be.server.coupon.application.dto.UserCouponDto;
import kr.hhplus.be.server.coupon.application.usecase.FindUserCouponSummaryUseCase;
import kr.hhplus.be.server.coupon.domain.repository.UserCouponRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * [UseCase 구현체]
 * FindUserCouponSummaryUseCase 인터페이스를 구현한 클래스.
 *
 * 도메인 계층의 UserCouponRepository를 사용하여 유저 쿠폰 데이터를 조회하고,
 * 그 결과를 UserCouponDto List로 변환하여 반환한다.
 *
 * 이 클래스는 오직 "유저의 쿠폰 목록 조회"라는 하나의 유스케이스만 책임지며,
 * 단일 책임 원칙(SRP)을 따르는 구조로 확장성과 테스트 용이성을 높인다.
 */
@Service
public class FindUserCouponSummaryService implements FindUserCouponSummaryUseCase {

    private final UserCouponRepository userCouponRepository;

    public FindUserCouponSummaryService(UserCouponRepository userCouponRepository) {
        this.userCouponRepository = userCouponRepository;
    }

    /**
     * 전체 쿠폰 목록을 조회하여 DTO 리스트로 변환한다.
     * @param userId 조회할 유저의 ID
     * @return 쿠폰 DTO 목록
     */
    @Override
    public List<UserCouponDto> findSummary(long userId) {
        return userCouponRepository.findByUserId(userId).stream()
                .map(UserCouponDto::from)
                .collect(Collectors.toList());
    }
}
