package kr.hhplus.be.server.user.application.usecase;

import kr.hhplus.be.server.user.application.dto.UserDto;

/**
 * [UseCase - Port In]
 * 사용자의 잔액을 차감하는 유스케이스에 대한 추상 정의.
 *
 * 이 인터페이스는 애플리케이션 계층에서 사용자의 ID와 사용량을 기반으로
 * 사용자의 잔액을 차감하는 기능을 외부(presentation, facade 등)에 노출하기 위한 계약(Contract)이다.
 *
 * 구현체는 service 패키지 내에서 정의되며,
 * presentation 계층은 이 인터페이스만 의존함으로써 구현체에 대한 결합을 피할 수 있다.
 */
public interface DeductUserBalanceUseCase {
    /**
     * 사용자 ID와 사용 금액을 받아 잔액을 차감시킨다.
     * @param userId 사용할 사용자 ID
     * @param amount 사용 금액
     * @return 차감된 이후의 사용자 정보
     */
    UserDto deductBalance(long userId, long amount);
}
