package kr.hhplus.be.server.payment.application.kafka.consumer;

import kr.hhplus.be.server.payment.application.event.dto.PaymentCompletedEvent;
import kr.hhplus.be.server.payment.application.usecase.ExternalDataUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExternalDataConsumer {

    private final ExternalDataUseCase externalDataUseCase;

    @KafkaListener(topics = "payment-completed", groupId = "external-data-group")
    public void consume(PaymentCompletedEvent event) {
        externalDataUseCase.send();
    }
}
