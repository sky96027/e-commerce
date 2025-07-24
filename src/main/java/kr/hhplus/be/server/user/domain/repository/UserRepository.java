package kr.hhplus.be.server.user.domain.repository;

import kr.hhplus.be.server.user.domain.model.User;

/**
 * 유저 정보를 조회, 저장하기 위한 도메인 저장소 인터페이스
 * 구현체는 인프라 계층에 위치함
 */
public interface UserRepository {
    User selectById(long userId);
    User insertOrUpdate(long userId, long Balance);
}