package kr.hhplus.be.server.order.application.service;

import kr.hhplus.be.server.order.application.dto.OrderItemDto;
import kr.hhplus.be.server.order.application.usecase.FindOrderItemByOrderIdUseCase;
import kr.hhplus.be.server.order.domain.model.OrderItem;
import kr.hhplus.be.server.order.domain.repository.OrderItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * [UseCase 구현체]
 * FindOrderItemByOrderIdUseCase 인터페이스를 구현한 클래스.
 *
 * 도메인 계층의 OrderItemRepository를 사용하여 주문 아이템을 조회한다.
 *
 * 이 클래스는 오직 "주문 ID로 주문 아이템 조회"라는 하나의 유스케이스만 책임지며,
 * 단일 책임 원칙(SRP)을 따르는 구조로 확장성과 테스트 용이성을 높인다.
 */
@Service
public class FindOrderItemByOrderIdService implements FindOrderItemByOrderIdUseCase {

    private final OrderItemRepository orderItemRepository;

    public FindOrderItemByOrderIdService(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public List<OrderItemDto> findByOrderId(long orderId) {
        List<OrderItem> items = orderItemRepository.findAllByOrderId(orderId);
        return items.stream()
                .map(OrderItemDto::from)
                .toList();
    }
}