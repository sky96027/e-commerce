package kr.hhplus.be.server.user.application.dto;

import kr.hhplus.be.server.user.domain.UserEntity;

public record UserDto(long userId, long balance) {

    public static UserDto from(UserEntity entity) {
        return new UserDto(entity.getUserId(), entity.getBalance());
    }
}