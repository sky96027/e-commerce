package kr.hhplus.be.server.application.user.dto;

import kr.hhplus.be.server.domain.user.UserEntity;

public record UserDto(long userId, long balance) {

    public static UserDto from(UserEntity entity) {
        return new UserDto(entity.getUserId(), entity.getBalance());
    }
}