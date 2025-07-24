package kr.hhplus.be.server.order.application.service;

import kr.hhplus.be.server.order.application.dto.OrderDto;
import kr.hhplus.be.server.order.application.usecase.FindOrderByOrderIdUseCase;
import kr.hhplus.be.server.order.domain.model.Order;
import kr.hhplus.be.server.order.domain.model.OrderItem;
import kr.hhplus.be.server.order.domain.repository.OrderItemRepository;
import kr.hhplus.be.server.order.domain.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * [UseCase 구현체]
 * FindOrderByOrderIdUseCase 인터페이스를 구현한 클래스.
 *
 * 도메인 계층의 OrderRepository를 사용하여 주문 데이터를 조회하고,
 * 그 결과를 OrderDto로 변환하여 반환한다.
 *
 * 이 클래스는 오직 "주문 조회"라는 하나의 유스케이스만 책임지며,
 * 단일 책임 원칙(SRP)을 따르는 구조로 확장성과 테스트 용이성을 높인다.
 */
@Service
public class FindOrderByOrderIdService implements FindOrderByOrderIdUseCase {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public FindOrderByOrderIdService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository
    ) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    /**
     * 주어진 주문 ID를 기반으로 주문 및 주문 항목 정보를 조회하고, DTO로 변환하여 반환한다.
     * @param orderId 조회할 주문 ID
     * @return 주문 정보를 담은 OrderDto
     */
    @Override
    public OrderDto findById(long orderId) {
        Order order = orderRepository.selectByOrderId(orderId);
        List<OrderItem> items = orderItemRepository.selectByOrderId(orderId);

        return OrderDto.from(order, items);
    }
}
