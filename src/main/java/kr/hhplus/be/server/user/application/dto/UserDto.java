package kr.hhplus.be.server.user.application.dto;

import kr.hhplus.be.server.user.domain.model.User;

/**
 * 사용자 정보를 표현하는 응답 DTO 클래스
 * Application Layer에서 사용되며,
 * 도메인 객체를 외부에 직접 노출하지 않도록 분리한 계층용 DTO
 */
public record UserDto(
        long userId,     // 사용자 ID
        long balance     // 사용자 잔액
) {
    /**
     * 도메인 모델로부터 DTO로 변환하는 정적 팩토리 메서드
     * @param user User 객체
     * @return UserDto 객체
     */
    public static UserDto from(User user) {
        return new UserDto(
                user.getUserId(),
                user.getBalance()
        );
    }
}