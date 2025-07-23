package kr.hhplus.be.server.user.application.dto;

import kr.hhplus.be.server.user.domain.UserEntity;

/**
 * 사용자 정보를 표현하는 응답 DTO 클래스
 * Application Layer에서 사용되며,
 * 도메인 객체(UserEntity)를 외부에 직접 노출하지 않도록 분리한 계층용 DTO
 */
public record UserDto(
        long userId,     // 사용자 ID
        long balance     // 사용자 잔액
) {
    /**
     * 도메인 엔티티(UserEntity)로부터 DTO로 변환하는 정적 팩토리 메서드
     * @param entity UserEntity 객체
     * @return UserDto 객체
     */
    public static UserDto from(UserEntity entity) {
        return new UserDto(
                entity.getUserId(),
                entity.getBalance()
        );
    }
}