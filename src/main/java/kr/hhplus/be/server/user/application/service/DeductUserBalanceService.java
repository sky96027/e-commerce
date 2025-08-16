package kr.hhplus.be.server.user.application.service;

import kr.hhplus.be.server.user.application.dto.UserDto;
import kr.hhplus.be.server.user.application.usecase.DeductUserBalanceUseCase;
import kr.hhplus.be.server.user.domain.model.User;
import kr.hhplus.be.server.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * [UseCase 구현체]
 * ChargeUserBalanceUseCase 인터페이스를 구현한 클래스.
 *
 * 도메인 계층의 UserRepository를 사용하여 실제 사용자 데이터를 조회하고,
 * 그 결과를 UserDto로 변환하여 반환한다.
 *
 * 이 클래스는 오직 "잔액 차감"라는 하나의 유스케이스만 책임지며,
 * 단일 책임 원칙(SRP)을 따르는 구조로 확장성과 테스트 용이성을 높인다.
 */
@Service
@RequiredArgsConstructor
public class DeductUserBalanceService implements DeductUserBalanceUseCase {
    private final UserRepository userRepository;

    /**
     * 주어진 사용자 ID를 기반으로 사용자 정보를 조회하고, 차감하여,
     * 그 결과를 UserDto로 변환하여 반환한다.
     * @param userId 차감할 사용자 ID
     * @param amount 차감 금액
     * @return 차감 후 사용자 정보를 담은 UserDto
     */
    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public UserDto deduct(long userId, long amount) {
        userRepository.deduct(userId, amount);
        User updated = userRepository.findById(userId);

        return UserDto.from(updated);
    }
}
