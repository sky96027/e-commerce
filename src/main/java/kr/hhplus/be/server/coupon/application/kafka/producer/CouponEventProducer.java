package kr.hhplus.be.server.coupon.application.kafka.producer;

import kr.hhplus.be.server.coupon.application.event.dto.CouponIssueRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "coupon-issue";

    public void send(CouponIssueRequestedEvent event) {
        log.info("Producing CouponIssueRequestedEvent: {}", event);
        kafkaTemplate.send(TOPIC, event);
    }
}
