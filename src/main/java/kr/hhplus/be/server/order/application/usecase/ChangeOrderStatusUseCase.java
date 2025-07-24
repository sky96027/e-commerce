package kr.hhplus.be.server.order.application.usecase;

import kr.hhplus.be.server.order.application.dto.OrderDto;
import kr.hhplus.be.server.order.domain.type.OrderStatus;

/**
 * [UseCase - Port In]
 * 주문을 상태를 변경하는 유스케이스에 대한 추상 정의.
 *
 * 이 인터페이스는 애플리케이션 계층에서 주문 ID를 기반으로
 * 주문을 상태를 변경하는 기능을 외부(presentation, facade 등)에 노출하기 위한 계약(Contract)이다.
 *
 * 구현체는 service 패키지 내에서 정의되며,
 * presentation 계층은 이 인터페이스만 의존함으로써 구현체에 대한 결합을 피할 수 있다.
 */
public interface ChangeOrderStatusUseCase {

    /**
     * 주문 상태를 변경한다.
     * @param orderId 주문 ID
     * @param newStatus 변경할 상태
     * @return 변경된 주문 객체
     */
    OrderDto changeStatus(long orderId, OrderStatus newStatus);
}
