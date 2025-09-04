package kr.hhplus.be.server.payment.application.kafka.producer;

import kr.hhplus.be.server.payment.application.event.dto.PaymentCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "payment-completed";

    public void send(PaymentCompletedEvent event) {
        log.info("Producing PaymentCompletedEvent: {}", event);
        kafkaTemplate.send(TOPIC, event);
    }
}
