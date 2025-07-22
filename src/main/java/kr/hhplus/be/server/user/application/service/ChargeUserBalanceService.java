package kr.hhplus.be.server.user.application.service;

import kr.hhplus.be.server.user.application.dto.UserDto;
import kr.hhplus.be.server.user.application.usecase.ChargeUserBalanceUseCase;
import kr.hhplus.be.server.user.domain.UserEntity;
import kr.hhplus.be.server.user.domain.UserRepository;
import org.springframework.stereotype.Service;

/**
 * [UseCase 구현체]
 * ChargeUserBalanceUseCase 인터페이스를 구현한 클래스.
 *
 * 도메인 계층의 UserRepository를 사용하여 실제 사용자 데이터를 조회하고,
 * 그 결과를 UserDto로 변환하여 반환한다.
 *
 * 이 클래스는 오직 "유저 조회"라는 하나의 유스케이스만 책임지며,
 * 단일 책임 원칙(SRP)을 따르는 구조로 확장성과 테스트 용이성을 높인다.
 */
@Service
public class ChargeUserBalanceService implements ChargeUserBalanceUseCase {

    private final UserRepository userRepository;

    public ChargeUserBalanceService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 주어진 사용자 ID를 기반으로 사용자 정보를 조회하고, 충전하여,
     * 그 결과를 UserDto로 변환하여 반환한다.
     * @param userId 충전할 사용자 ID
     * @param amount 충전 금액
     * @return 충전 후 사용자 정보를 담은 UserDto
     */
    @Override
    public UserDto charge(long userId, long amount) {
        UserEntity user = userRepository.selectById(userId);
        UserEntity updated = user.charge(amount);
        UserEntity saved = userRepository.insertOrUpdate(userId, updated.getBalance());

        return UserDto.from(saved);
    }
}