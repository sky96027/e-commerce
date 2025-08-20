package kr.hhplus.be.server.coupon.infrastructure.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.coupon.application.dto.SaveUserCouponCommand;
import kr.hhplus.be.server.coupon.domain.repository.CouponIssueCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class CouponIssueCommandRepositoryImpl implements CouponIssueCommandRepository {

    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;

    private String cmdKey(String rid) { return "coupon:issue:cmd:" + rid; }

    @Override
    public void save(String reservationId, SaveUserCouponCommand cmd) {
        try {
            String json = objectMapper.writeValueAsString(cmd);
            redis.opsForValue().set(cmdKey(reservationId), json, Duration.ofHours(1));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("직렬화 실패", e);
        }
    }

    @Override
    public SaveUserCouponCommand find(String reservationId) {
        String json = redis.opsForValue().get(cmdKey(reservationId));
        if (json == null) return null;
        try {
            return objectMapper.readValue(json, SaveUserCouponCommand.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("역직렬화 실패", e);
        }
    }

    @Override
    public void delete(String reservationId) {
        redis.delete(cmdKey(reservationId));
    }
}
