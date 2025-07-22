package kr.hhplus.be.server.user.application.service;

import kr.hhplus.be.server.user.application.dto.UserDto;
import kr.hhplus.be.server.user.application.usecase.FindUserUseCase;
import kr.hhplus.be.server.user.domain.UserEntity;
import kr.hhplus.be.server.user.domain.UserRepository;
import org.springframework.stereotype.Service;

/**
 * [UseCase 구현체]
 * FindUserUseCase 인터페이스를 구현한 클래스.
 *
 * 도메인 계층의 UserRepository를 사용하여 실제 사용자 데이터를 조회하고,
 * 그 결과를 UserDto로 변환하여 반환한다.
 *
 * 이 클래스는 오직 "유저 조회"라는 하나의 유스케이스만 책임지며,
 * 단일 책임 원칙(SRP)을 따르는 구조로 확장성과 테스트 용이성을 높인다.
 */
@Service
public class FindUserService implements FindUserUseCase {

    private final UserRepository userRepository;

    public FindUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 주어진 사용자 ID를 기반으로 사용자 정보를 조회하고, DTO로 변환하여 반환한다.
     * @param userId 조회할 사용자 ID
     * @return 사용자 정보를 담은 UserDto
     */
    @Override
    public UserDto findById(long userId) {
        UserEntity user = userRepository.selectById(userId);

        return UserDto.from(user);
    }
}