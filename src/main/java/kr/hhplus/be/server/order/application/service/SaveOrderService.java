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

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class SaveOrderService implements SaveOrderUseCase {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserCouponRepository userCouponRepository;

    private final AtomicLong orderIdSequence = new AtomicLong(1);
    private final AtomicLong orderItemIdSequence = new AtomicLong(1);

    public SaveOrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            UserCouponRepository userCouponRepository
    ) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.userCouponRepository = userCouponRepository;
    }
    @Override
    public long save (SaveOrderCommand command) {
        long orderId = orderIdSequence.getAndIncrement();

        long totalAmount = command.items().stream()
                .mapToLong(item -> item.productPrice() * item.quantity())
                .sum();

        List<OrderItem> orderItems = command.items().stream()
                .map(item -> {
                    long orderItemId = orderItemIdSequence.getAndIncrement();
                    long discountAmount = 0L;

                    if (item.userCouponId() != null) {
                        UserCoupon coupon = userCouponRepository.findByUserCouponId(item.userCouponId())
                                .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다: id = " + item.userCouponId()));

                        if ("FIXED".equalsIgnoreCase(coupon.getTypeSnapshot().name())) {
                            discountAmount = coupon.getDiscountAmountSnapshot();
                        } else if ("RATE".equalsIgnoreCase(coupon.getTypeSnapshot().name())) {
                            discountAmount = Math.round(item.productPrice() * (coupon.getDiscountRateSnapshot() / 100.0));
                        }

                        long totalItemPrice = item.productPrice() * item.quantity();
                        if (coupon.getMinimumOrderAmountSnapshot() != null &&
                                totalItemPrice < coupon.getMinimumOrderAmountSnapshot()) {
                            discountAmount = 0L;
                        }
                    }

                    return new OrderItem(
                            orderItemId,
                            orderId,
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

        Order order = new Order(
                orderId,
                command.userId(),
                totalAmount,
                totalDiscountAmount,
                OrderStatus.BEFORE_PAYMENT,
                LocalDateTime.now()
        );

        orderRepository.save(order);
        orderItemRepository.saveAll(orderItems);

        return orderId;
    }
}