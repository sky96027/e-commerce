package kr.hhplus.be.server.transactionhistory.application.kafka.consumer;

import kr.hhplus.be.server.payment.application.event.dto.PaymentCompletedEvent;
import kr.hhplus.be.server.transactionhistory.application.usecase.SaveTransactionUseCase;
import kr.hhplus.be.server.transactionhistory.domain.type.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionHistoryConsumer {
    private final SaveTransactionUseCase saveTransactionUseCase;

    @KafkaListener(topics = "payment-completed", groupId = "transaction-group")
    public void consume(PaymentCompletedEvent event) {
        saveTransactionUseCase.save(event.userId(), TransactionType.USE, event.totalAmount());
    }
}
