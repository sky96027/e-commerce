package kr.hhplus.be.server.order.domain.repository;

import kr.hhplus.be.server.order.domain.model.Order;

/**
 * 주문을 조회, 저장하기 위한 도메인 저장소 인터페이스
 * 구현체는 인프라 계층에 위치함
 */
public interface OrderRepository {
    Order selectByOrderId (long orderId);
    void save(Order order);
}
