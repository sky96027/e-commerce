package kr.hhplus.be.server.coupon.application.kafka.consumer;

import kr.hhplus.be.server.coupon.application.dto.SaveUserCouponCommand;
import kr.hhplus.be.server.coupon.application.event.dto.CouponIssueRequestedEvent;
import kr.hhplus.be.server.coupon.application.usecase.SaveUserCouponUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponIssueConsumer {

    private final SaveUserCouponUseCase saveUserCouponUseCase;

    @KafkaListener(topics = "coupon-issue", groupId = "coupon-issue-group")
    public void consume(CouponIssueRequestedEvent event) {
        log.info("Consumed event: {}", event);
        
        try {
            SaveUserCouponCommand command = new SaveUserCouponCommand(
                    event.userId(),
                    event.couponId(),
                    event.policyId(),
                    event.typeSnapshot(),
                    event.discountRateSnapshot(),
                    event.usagePeriodSnapshot(),
                    event.expiredAt()
            );
            
            saveUserCouponUseCase.save(command);
            log.info("Successfully issued couponId={} userId={} rid={}", 
                    event.couponId(), event.userId(), event.reservationId());
                    
        } catch (Exception e) {
            log.error("Failed to issue couponId={} userId={} rid={}, error: {}", 
                    event.couponId(), event.userId(), event.reservationId(), e.getMessage(), e);
        }
    }
}
