package kr.hhplus.be.server.order.application.service;

import kr.hhplus.be.server.coupon.domain.model.UserCoupon;
import kr.hhplus.be.server.coupon.domain.repository.UserCouponRepository;
import kr.hhplus.be.server.order.application.dto.SaveOrderCommand;
import kr.hhplus.be.server.order.application.usecase.SaveOrderUseCase;
import kr.hhplus.be.server.order.domain.model.Order;
import kr.hhplus.be.server.order.domain.model.OrderItem;
import kr.hhplus.be.server.order.domain.repository.OrderItemRepository;
import kr.hhplus.be.server.order.domain.repository.OrderRepository;
import kr.hhplus.be.server.order.domain.type.OrderStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class SaveOrderService implements SaveOrderUseCase {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserCouponRepository userCouponRepository;

    public SaveOrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            UserCouponRepository userCouponRepository
    ) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.userCouponRepository = userCouponRepository;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public long save(SaveOrderCommand command) {
        long totalAmount = command.items().stream()
                .mapToLong(item -> item.productPrice() * item.quantity())
                .sum();

        // 주문 자체는 아직 OrderItem 없이 먼저 생성
        Order order = new Order(
                null,
                command.userId(),
                totalAmount,
                0L,
                OrderStatus.BEFORE_PAYMENT,
                LocalDateTime.now()
        );

        Order savedOrder = orderRepository.save(order);
        final long savedOrderId = savedOrder.getOrderId();

        List<OrderItem> orderItems = command.items().stream()
                .map(item -> {
                    long discountAmount = 0L;

                    if (item.userCouponId() != null) {
                        UserCoupon coupon = userCouponRepository.findByUserCouponId(item.userCouponId());

                        long totalItemPrice = item.productPrice() * item.quantity();

                        discountAmount = Math.round(totalItemPrice * (coupon.getDiscountRateSnapshot() / 100.0));
                    }

                    return new OrderItem(
                            null,
                            savedOrderId,
                            item.productId(),
                            item.optionId(),
                            item.productName(),
                            item.productPrice(),
                            discountAmount,
                            item.userCouponId(),
                            item.quantity()
                    );
                })
                .toList();

        long totalDiscountAmount = orderItems.stream()
                .mapToLong(OrderItem::getDiscountAmount)
                .sum();

        // 할인금액을 반영한 주문 객체로 다시 업데이트
        savedOrder = savedOrder.withTotalDiscountAmount(totalDiscountAmount);
        orderRepository.save(savedOrder);  // update

        orderItemRepository.saveAll(orderItems);

        return savedOrder.getOrderId();
    }
}