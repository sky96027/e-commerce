package kr.hhplus.be.server.coupon.application.facade;

import kr.hhplus.be.server.common.redis.RedisDistributedLockManager;
import kr.hhplus.be.server.coupon.application.dto.SaveUserCouponCommand;
import kr.hhplus.be.server.coupon.application.usecase.SaveUserCouponUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class CouponFacade {
    private final RedisDistributedLockManager lockManager; // 이미 있는 매니저 사용
    private final SaveUserCouponUseCase saveUserCouponUseCase;

    public void issueToUser(SaveUserCouponCommand command) {
        String key = "coupon:issue:" + command.couponId();

        // [PUB/SUB LOCK] 해제 알림 기반 블로킹 획득
        String token = lockManager.lockBlockingPubSub(
                key,
                Duration.ofSeconds(10),  // TTL (p99 처리시간보다 짧지 않게)
                Duration.ofSeconds(30)   // 전체 대기 한도
        );
        if (token == null) throw new IllegalStateException("잠시 후 다시 시도해 주세요.");

        try {
            saveUserCouponUseCase.save(command);
        } finally {
            lockManager.unlock(key, token);
        }
    }

}
