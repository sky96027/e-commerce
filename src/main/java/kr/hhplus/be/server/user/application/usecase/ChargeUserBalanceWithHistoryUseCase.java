package kr.hhplus.be.server.user.application.usecase;

import kr.hhplus.be.server.user.application.dto.UserDto;

public interface ChargeUserBalanceWithHistoryUseCase {
    UserDto execute(long userId, long amount);
}