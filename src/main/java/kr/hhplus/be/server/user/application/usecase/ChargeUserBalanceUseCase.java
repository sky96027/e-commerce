package kr.hhplus.be.server.user.application.usecase;

import kr.hhplus.be.server.user.application.dto.UserDto;

public interface ChargeUserBalanceUseCase {
    /**
     * 사용자 ID와 충전 금액을 받아 잔액을 증가시킨다.
     * @param userId 충전할 사용자 ID
     * @param amount 충전 금액
     * @return 충전된 이후의 사용자 정보
     */
    UserDto charge(long userId, long amount);
}
