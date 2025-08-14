package kr.hhplus.be.server.user.application.usecase;

import kr.hhplus.be.server.user.application.dto.UserDto;

public interface DeductUserBalanceWithHistoryUseCase {
    UserDto execute(long userId, long amount);
}
