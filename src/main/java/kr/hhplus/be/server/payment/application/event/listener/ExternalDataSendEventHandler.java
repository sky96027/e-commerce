package kr.hhplus.be.server.payment.application.event.listener;

import kr.hhplus.be.server.payment.application.usecase.ExternalDataUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ExternalDataSendEventHandler {

    private final ExternalDataUseCase externalDataUseCase;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendExternalData() {
        externalDataUseCase.send();
    }
}
