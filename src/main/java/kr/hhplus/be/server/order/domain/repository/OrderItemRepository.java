package kr.hhplus.be.server.order.domain.repository;

import kr.hhplus.be.server.order.domain.model.OrderItem;

import java.util.List;

/**
 * 주문에 담긴 아이템을 조회, 저장하기 위한 도메인 저장소 인터페이스
 * 구현체는 인프라 계층에 위치함
 */
public interface OrderItemRepository {
    List<OrderItem> selectByOrderId(long orderId);
    void saveAll(List<OrderItem> items);
}
