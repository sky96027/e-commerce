package kr.hhplus.be.server.order.application.service;

import kr.hhplus.be.server.common.redis.cache.events.OrderStatusChangedEvent;
import kr.hhplus.be.server.order.application.dto.OrderDto;
import kr.hhplus.be.server.order.application.usecase.ChangeOrderStatusUseCase;
import kr.hhplus.be.server.order.domain.model.Order;
import kr.hhplus.be.server.order.domain.model.OrderItem;
import kr.hhplus.be.server.order.domain.repository.OrderItemRepository;
import kr.hhplus.be.server.order.domain.repository.OrderRepository;
import kr.hhplus.be.server.order.domain.type.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * [UseCase 구현체]
 * ChangeStatusUseCase 인터페이스를 구현한 클래스.
 *
 * 도메인 계층의 OrderRepository를 사용하여 주문 데이터를 조회하고,
 * 그 결과를 OrderDto로 변환하여 반환한다.
 *
 * 이 클래스는 오직 "주문 조회"라는 하나의 유스케이스만 책임지며,
 * 단일 책임 원칙(SRP)을 따르는 구조로 확장성과 테스트 용이성을 높인다.
 */
@Service
@RequiredArgsConstructor
public class ChangeOrderStatusService implements ChangeOrderStatusUseCase {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ApplicationEventPublisher publisher;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public OrderDto changeStatus(long orderId, OrderStatus newStatus) {
        Order current = orderRepository.findById(orderId);
        if (current == null) {
            throw new IllegalArgumentException("해당 주문이 존재하지 않습니다. orderId = " + orderId);
        }

        Order updated = current.changeStatus(newStatus);
        orderRepository.save(updated);

        List<OrderItem> items = orderItemRepository.findAllByOrderId(orderId);
        publisher.publishEvent(new OrderStatusChangedEvent(orderId));
        return OrderDto.from(updated, items);
    }
}